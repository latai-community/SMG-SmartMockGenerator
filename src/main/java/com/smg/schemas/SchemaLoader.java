package com.smg.schemas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * SchemaLoader is responsible for loading the SQL content of predefined schemas
 * from the classpath resources.
 */
public class SchemaLoader {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SchemaLoader.class);
	private static final String SCHEMAS_PATH = "schemas/";
	private static final String SQL_EXTENSION = ".sql";
	
	/**
	 * Loads the SQL content for a given model from the resources folder.
	 *
	 * @param modelName The name of the model (e.g., "HR", "OE", "Invest").
	 * @return The SQL content as a String, or null if the file is not found.
	 */
	public String loadSqlContent(String modelName) {
		String filePath = SCHEMAS_PATH + modelName + SQL_EXTENSION;
		
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
			if (is == null) {
				LOGGER.error("Schema file not found: {}", filePath);
				return null;
			}
			// Leer el contenido del InputStream a un String
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOGGER.error("Error reading schema file: {}", filePath, e);
			return null;
		}
	}
}