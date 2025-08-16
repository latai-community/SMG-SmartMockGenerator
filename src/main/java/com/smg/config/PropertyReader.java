package com.smg.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PropertyReader handles the loading of configuration properties from smg.properties.
 * It provides default values that can be overridden by command-line arguments.
 */
public class PropertyReader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyReader.class);
	private static final String DEFAULT_PROPERTIES_FILE = "smg.properties";
	
	/**
	 * Loads default configuration from the smg.properties file.
	 * If the file is not found, a new SMGConfig object with null values is returned.
	 *
	 * @return A new SMGConfig object populated with default values from the properties file.
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
			config.setTables(Arrays.asList(properties.getProperty("model.tables", "").split(",")));
			config.setDiagramOutput(properties.getProperty("model.diagram"));
			config.setSchemaOutput(properties.getProperty("output.schema"));
			config.setDataOutput(properties.getProperty("output.data"));
			// The logic for syntheticGenerate is more complex and will be handled by the main application class
			// or a dedicated parser based on the string format. For now, we'll assume it's set via CLI.
			config.setEncoding(properties.getProperty("config.encoding", "UTF8"));
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