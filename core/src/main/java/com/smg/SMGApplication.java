package com.smg;

import com.smg.config.PropertyReader;
import com.smg.config.SMGConfig;
import com.smg.generation.DataGenerator;
import com.smg.schemas.SchemaManager;
import com.smg.logging.ErrorLogger;
import com.smg.logging.SummaryLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SMGApplication {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SMGApplication.class);
	
	public static void main(String[] args) {
		// Inicialización de loggers
		ErrorLogger errorLogger = new ErrorLogger();
		SummaryLogger summaryLogger = new SummaryLogger();
		
		try {
			// 1. Cargar la configuración por defecto desde smg.properties
			SMGConfig config = PropertyReader.loadDefaultConfig();
			
			// 2. Sobrescribir la configuración con los argumentos de la línea de comandos
			parseAndApplyArgs(args, config);
			
			// 3. Validar la configuración
			validateConfig(config);
			
			// 4. Cargar y limpiar el esquema
			SchemaManager schemaManager = new SchemaManager();
			schemaManager.loadSchemas(); // Carga todos los esquemas (HR, OE, etc.) desde recursos
			var cleanedSchema = schemaManager.cleanSchema(config.getModel(), config.getTables());
			
			// 5. Generar los datos
			DataGenerator dataGenerator = new DataGenerator(config, errorLogger, summaryLogger);
			dataGenerator.generate(cleanedSchema);
			
			summaryLogger.logSummary("SMG process finished successfully.");
			
		} catch (Exception e) {
			errorLogger.logError("An unexpected error occurred during the process.", e);
			LOGGER.error("Application failed with an error. Check error log for details.");
		}
	}
	
	/**
	 * Parses command-line arguments and updates the configuration.
	 * Arguments are expected in the format "-key value".
	 */
	private static void parseAndApplyArgs(String[] args, SMGConfig config) {
		// Simple argument parsing. A more robust solution might use a library,
		// but given the Node.js CLI, this is sufficient.
		for (int i = 0; i < args.length; i += 2) {
			String key = args[i];
			// Ensure there's a value for the key
			if (i + 1 < args.length) {
				String value = args[i + 1];
				
				switch (key) {
					case "-model" -> config.setModel(value);
					case "-tables" -> config.setTables(Arrays.asList(value.split(",")));
					case "-diagram" -> config.setDiagramOutput(value);
					case "-schemaOutput" -> config.setSchemaOutput(value);
					case "-dataOutput" -> config.setDataOutput(value);
					case "-syntheticGenerate" -> config.setSyntheticGenerate(parseSyntheticGenerate(value));
					case "-encoding" -> config.setEncoding(value);
					case "-errorFile" -> config.setErrorFile(value);
					case "-summaryFile" -> config.setSummaryFile(value);
					case "-mockConfig" -> config.setMockConfig(value);
					case "-mockApiKey" -> config.setMockApiKey(value);
					default -> LOGGER.warn("Unknown argument: {}", key);
				}
			} else {
				LOGGER.error("Missing value for argument: {}", key);
			}
		}
	}
	
	/**
	 * Parses the synthetic generate string (e.g., "employee(100),department(50)").
	 */
	private static Map<String, Integer> parseSyntheticGenerate(String value) {
		Map<String, Integer> rowCounts = new HashMap<>();
		if (value != null && !value.isEmpty()) {
			String[] pairs = value.split(",");
			for (String pair : pairs) {
				String[] parts = pair.trim().split("\\(");
				if (parts.length == 2) {
					String tableName = parts[0].trim();
					try {
						int rowCount = Integer.parseInt(parts[1].replace(")", ""));
						if (rowCount > 0) {
							rowCounts.put(tableName, rowCount);
						} else {
							LOGGER.error("Invalid row count for table {}: must be a positive integer.", tableName);
						}
					} catch (NumberFormatException e) {
						LOGGER.error("Invalid row count format for table {}.", tableName);
					}
				}
			}
		}
		return rowCounts;
	}
	
	/**
	 * Performs a simple validation of the configuration.
	 * Throws an IllegalArgumentException if a critical configuration is missing.
	 */
	private static void validateConfig(SMGConfig config) {
		Optional.ofNullable(config.getModel())
			.filter(s -> !s.isEmpty())
			.orElseThrow(() -> new IllegalArgumentException("Model must be specified."));
		// Additional validation can be added here
	}
}