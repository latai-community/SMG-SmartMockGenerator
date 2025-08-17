package com.smg;

import com.smg.config.PropertyReader;
import com.smg.config.SMGConfig;
import com.smg.logging.ErrorLogger;
import com.smg.logging.SummaryLogger;
import com.smg.sqlparser.domain.sql.Schema;
import com.smg.sqlparser.parser.SQL99.SqlSchemaParser;
import com.smg.sqlparser.services.SqlGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The main application class for the SMG (Synthetic Model Generator) tool.
 * This class orchestrates the entire data generation process, including
 * configuration loading, command-line argument parsing, schema management,
 * and data generation.
 *
 * Workflow:
 * 1. Load default configuration from smg.properties.
 * 2. Override configuration settings with command-line arguments.
 * 3. Validate the final configuration.
 * 4. Load and prepare the database schema(s).
 * 5. Generate synthetic data based on the schema.
 * 6. Log summary or errors.
 */
public class SMGApplication {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SMGApplication.class);
	private static final Integer ROWS_PER_TABLE = 10;
	private static final String SCHEMA_RESOURCE = "models/hr/struct/HR_struct.sql";
	
	public static void main(String[] args) {
		ErrorLogger errorLogger = new ErrorLogger();
		SummaryLogger summaryLogger = new SummaryLogger();
		
		try {
			// 1. Load default configuration
			SMGConfig config = PropertyReader.loadDefaultConfig();
			
			// 2. Override with CLI arguments
			parseAndApplyArgs(args, config);
			
			// 3. Validate config
			validateConfig(config);
			
			// 4. Load schema from resources
			Schema schema;
			try (InputStream in = SMGApplication.class.getClassLoader().getResourceAsStream(SCHEMA_RESOURCE)) {
				if (in == null) {
					throw new IllegalStateException("Resource not found: " + SCHEMA_RESOURCE);
				}
				String schemaSql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
				schema = SqlSchemaParser.parseSchemaFromString(schemaSql, "HR");
			}
			
			SqlGeneratorService sqlgService = new SqlGeneratorService(schema);
			
			// 5. Generate DDL
			String ddl = sqlgService.generateCreateSql(config.getTables());
			System.out.println("---------------------------CREATE SQL---------------------------");
			System.out.println(ddl);
			
			// 6. Generate inserts
			String insert = sqlgService.generateInsertSql(config.getTables(), ROWS_PER_TABLE);
			System.out.println("---------------------------INSERT SQL---------------------------");
			System.out.println(insert);
			
			summaryLogger.logSummary("SMG process finished successfully.");
			
		} catch (IllegalArgumentException e) {
			LOGGER.error("Configuration validation failed.", e);
			errorLogger.logError("Configuration validation failed: " + e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error("Failed to read schema resource.", e);
			errorLogger.logError("Failed to read schema resource: " + e.getMessage(), e);
		} catch (Exception e) {
			errorLogger.logError("Unexpected error during the process.", e);
			LOGGER.error("Application failed with unexpected error.", e);
		}
	}
	
	private static void parseAndApplyArgs(String[] args, SMGConfig config) {
		for (int i = 0; i < args.length; i += 2) {
			String key = args[i];
			if (i + 1 < args.length) {
				String value = args[i + 1];
				switch (key) {
					case "-model" -> config.setModel(value);
					case "-tables" -> config.setTables(new HashSet<>(Arrays.asList(value.split(","))));
					case "-diagram" -> config.setDiagramOutput(value);
					case "-schemaOutput" -> config.setSchemaOutput(value);
					case "-dataOutput" -> config.setDataOutput(value);
					case "-syntheticGenerate" -> config.setSyntheticGenerate(parseSyntheticGenerate(value));
					case "-encoding" -> config.setEncoding(value);
					case "-errorFile" -> config.setErrorFile(value);
					case "-summaryFile" -> config.setSummaryFile(value);
					case "-mockConfig" -> config.setMockConfig(value);
					case "-mockApiKey" -> config.setMockApiKey(value);
					default -> LOGGER.warn("Unknown CLI argument: {}", key);
				}
			} else {
				LOGGER.error("Missing value for CLI argument: {}", key);
			}
		}
	}
	
	private static Map<String, Integer> parseSyntheticGenerate(String value) {
		return Optional.ofNullable(value)
			.filter(s -> !s.isEmpty())
			.map(s -> Arrays.stream(s.split(","))
				.map(String::trim)
				.filter(pair -> pair.matches("\\w+\\(\\d+\\)"))
				.collect(Collectors.toMap(
					pair -> pair.substring(0, pair.indexOf('(')),
					pair -> Integer.parseInt(pair.substring(pair.indexOf('(') + 1, pair.indexOf(')'))),
					(existing, replacement) -> existing,
					HashMap::new
				)))
			.orElse(new HashMap<>());
	}
	
	public static void validateConfig(SMGConfig config) {
		Optional.ofNullable(config.getModel())
			.filter(s -> !s.isEmpty())
			.orElseThrow(() -> new IllegalArgumentException("Model must be specified in the configuration."));
		
		Optional.ofNullable(config.getTables())
			.filter(list -> !list.isEmpty())
			.orElseThrow(() -> new IllegalArgumentException("Tables must be specified in the configuration."));
		
		Optional.ofNullable(config.getErrorFile())
			.filter(s -> !s.isEmpty())
			.orElseThrow(() -> new IllegalArgumentException("Error file must be specified in the configuration."));
		
		Optional.ofNullable(config.getSummaryFile())
			.filter(s -> !s.isEmpty())
			.orElseThrow(() -> new IllegalArgumentException("Summary file must be specified in the configuration."));
	}
}
