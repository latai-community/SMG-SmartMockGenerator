package com.smg;

import com.smg.config.PropertyReader;
import com.smg.config.SMGConfig;
import com.smg.logging.ErrorLogger;
import com.smg.logging.SummaryLogger;
import com.smg.sqlparser.parser.SQL99.SqlSchemaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.smg.sqlparser.domain.sql.Schema;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The main application class for the SMG (Synthetic Model Generator) tool.
 * This class orchestrates the entire data generation process, including
 * configuration loading, command-line argument parsing, schema management,
 * and data generation.
 *
 * The application's workflow is as follows:
 * 1. Load default configuration from `smg.properties`.
 * 2. Override configuration settings with command-line arguments.
 * 3. Validate the final configuration to ensure all required parameters are present.
 * 4. Load and prepare the database schema(s).
 * 5. Generate synthetic data based on the loaded schema and configuration.
 * 6. Log a summary of the successful process or an error if an exception occurs.
 */
public class SMGApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(SMGApplication.class);

	/**
	 * The main entry point for the SMG application.
	 *
	 * @param args Command-line arguments passed to the application.
	 */
	public static void main(String[] args) {
		ErrorLogger errorLogger = new ErrorLogger();
		SummaryLogger summaryLogger = new SummaryLogger();

		try {
			// 1. Load the default configuration from smg.properties.
			SMGConfig config = PropertyReader.loadDefaultConfig();

			// 2. Override the configuration with command-line arguments.
			parseAndApplyArgs(args, config);

			// 3. Validate the final configuration.
			validateConfig(config);

			// 4. Load and clean the schema(s) based on the configuration.
			Path hr_path = Path.of("C:\\Users\\joseb\\Desktop\\SMG-SmartMockGenerator\\src\\main\\resources\\models\\hr\\struct\\HR_struct.sql");
			
			Schema schema = SqlSchemaParser.parseSchemaFromFile(hr_path, "HR");
			Set<String> selectedTable = config.getTables();
			String create_hr = schema.getTables().get("departments").getCreateSql(selectedTable);
			
			// tablas --> departments --> create-table --> fk desconectadas
			System.out.println(create_hr);

			summaryLogger.logSummary("SMG process finished successfully.");

		} catch (IllegalArgumentException e) {
			// Catch specific validation errors and provide a clear message
			LOGGER.error("Configuration validation failed. Please check the provided configuration.", e);
			errorLogger.logError("Configuration validation failed: " + e.getMessage(), e);
		} catch (Exception e) {
			// Generic catch-all for other unexpected errors
			errorLogger.logError("An unexpected error occurred during the process.", e);
			LOGGER.error("Application failed with an unexpected error. Check the error log for details.");
		}
	}

	/**
	 * Parses command-line arguments and updates the provided configuration object.
	 * Arguments are expected in the format "-key value".
	 *
	 * @param args   The array of command-line arguments.
	 * @param config The configuration object to update.
	 */
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
					default -> LOGGER.warn("Unknown command-line argument: {}", key);
				}
			} else {
				LOGGER.error("Missing value for command-line argument: {}", key);
			}
		}
	}

	/**
	 * Parses a string of synthetic generation instructions and returns a map
	 * of table names to row counts.
	 *
	 * <p>The input string is expected to be in the format: "tableName1(rowCount1),tableName2(rowCount2)".
	 *
	 * @param value The string to parse.
	 * @return A {@code Map<String, Integer>} where the key is the table name and the value is the row count.
	 */
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

	/**
	 * Performs a simple validation of the configuration to ensure all critical
	 * parameters are present.
	 *
	 * @param config The configuration object to validate.
	 * @throws IllegalArgumentException if a critical configuration parameter is missing.
	 */
	public static void validateConfig(SMGConfig config) {
		// Critical parameters: model, tables, errorFile, summaryFile
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