package com.smg.fileio.exporters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import com.smg.fileio.IExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CSVExporter exports data by converting Mockaroo's JSON output into a
 * CSV (Comma-Separated Values) format file.
 */
public class CSVExporter implements IExporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVExporter.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public void export(String outputFilePath, String tableName, String mockDataJson) throws IOException {
		LOGGER.info("Exporting data for table '{}' to CSV file: {}", tableName, outputFilePath);
		
		try (FileWriter writer = new FileWriter(outputFilePath, true)) { // 'true' para modo append
			JsonNode dataArray = objectMapper.readTree(mockDataJson);
			
			if (dataArray.isArray() && !dataArray.isEmpty()) {
				// Escribir la cabecera CSV (nombres de las columnas)
				JsonNode firstObject = dataArray.get(0);
				Iterator<String> fieldNames = firstObject.fieldNames();
				StringBuilder header = new StringBuilder();
				while (fieldNames.hasNext()) {
					header.append("\"").append(fieldNames.next()).append("\"");
					if (fieldNames.hasNext()) {
						header.append(",");
					}
				}
				writer.write(header.toString() + "\n");
				
				// Escribir los datos de cada fila
				for (JsonNode row : dataArray) {
					StringBuilder values = new StringBuilder();
					Iterator<JsonNode> fieldValues = row.iterator();
					while (fieldValues.hasNext()) {
						JsonNode value = fieldValues.next();
						values.append("\"").append(value.isTextual() ? value.asText().replace("\"", "\"\"") : value.toString()).append("\"");
						if (fieldValues.hasNext()) {
							values.append(",");
						}
					}
					writer.write(values.toString() + "\n");
				}
				LOGGER.info("Successfully exported {} rows for table '{}'.", dataArray.size(), tableName);
			} else {
				LOGGER.warn("No data to export for table '{}'.", tableName);
			}
		}
	}
}