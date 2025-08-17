package com.smg.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PropertyReader handles the loading of configuration properties from `smg.properties`.
 * It provides default values that can be overridden by command-line arguments.
 */
public class PropertyReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyReader.class);
	private static final String DEFAULT_PROPERTIES_FILE = "smg.properties";

	/**
	 * Loads the default configuration from the `smg.properties` file located in the classpath.
	 * <p>
	 * The method reads the properties, populates an {@link SMGConfig} object, and handles
	 * cases where the file is not found or an I/O error occurs. It provides default
	 * values for `config.encoding`, `error.file`, and `summary.file` if they are not
	 * specified in the properties file.
	 *
	 * @return A new {@link SMGConfig} object populated with default values from the properties file.
	 * Returns an empty configuration object if the properties file is not found.
	 */
	public static SMGConfig loadDefaultConfig() {
		SMGConfig config = new SMGConfig();
		Properties properties = new Properties();

		try (InputStream input = PropertyReader.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE)) {
			if (input == null) {
				LOGGER.warn("File '{}' not found in the classpath. Using default null configuration.", DEFAULT_PROPERTIES_FILE);
				return config;
			}
			properties.load(input);

			// Populate the SMGConfig object with values from the properties file
			config.setModel(properties.getProperty("model"));

			// Use Optional to handle potential null or empty string from properties file
			Optional.ofNullable(properties.getProperty("model.tables"))
				.map(s -> Arrays.stream(s.split(","))
					.map(String::trim)
					.collect(Collectors.toSet()))
				.ifPresent(config::setTables);

			config.setDiagramOutput(properties.getProperty("model.diagram"));
			config.setSchemaOutput(properties.getProperty("output.schema"));
			config.setDataOutput(properties.getProperty("output.data"));

			// Note: syntheticGenerate is expected to be handled by the CLI parser

			// Use getProperty with default values to ensure non-null results
			config.setEncoding(properties.getProperty("config.encoding", "UTF-8"));
			config.setMockConfig(properties.getProperty("config.mock"));
			config.setMockApiKey(properties.getProperty("config.apikey_mockaroo"));
			config.setErrorFile(properties.getProperty("error.file", "logErrorSmg.log"));
			config.setSummaryFile(properties.getProperty("summary.file", "summarySmg.log"));

		} catch (IOException e) {
			LOGGER.error("Error loading properties file: {}", DEFAULT_PROPERTIES_FILE, e);
		}

		return config;
	}
}