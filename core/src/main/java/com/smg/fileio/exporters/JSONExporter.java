package com.smg.fileio.exporters;

import java.io.FileWriter;
import java.io.IOException;

import com.smg.fileio.IExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSONExporter exports data by writing Mockaroo's raw JSON output directly
 * to a file, which is the most straightforward export format.
 */
public class JSONExporter implements IExporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JSONExporter.class);
	
	@Override
	public void export(String outputFilePath, String tableName, String mockDataJson) throws IOException {
		LOGGER.info("Exporting data for table '{}' to JSON file: {}", tableName, outputFilePath);
		
		try (FileWriter writer = new FileWriter(outputFilePath, true)) { // 'true' para modo append
			// Escribir el JSON directamente al archivo
			writer.write(mockDataJson);
			LOGGER.info("Successfully exported data for table '{}' to JSON.", tableName);
		}
	}
}