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
 * SQLExporter exports data by converting Mockaroo's JSON output into a series of
 * SQL INSERT statements for a given table.
 */
public class SQLExporter implements IExporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SQLExporter.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public void export(String outputFilePath, String tableName, String mockDataJson) throws IOException {
		LOGGER.info("Exporting data for table '{}' to SQL file: {}", tableName, outputFilePath);
		
		try (FileWriter writer = new FileWriter(outputFilePath, true)) { // 'true' para modo append
			JsonNode dataArray = objectMapper.readTree(mockDataJson);
			
			if (dataArray.isArray() && !dataArray.isEmpty()) {
				// Obtener los nombres de las columnas a partir del primer objeto JSON
				JsonNode firstObject = dataArray.get(0);
				StringBuilder columnNames = new StringBuilder();
				Iterator<String> fieldNames = firstObject.fieldNames();
				while(fieldNames.hasNext()) {
					columnNames.append(fieldNames.next());
					if (fieldNames.hasNext()) {
						columnNames.append(", ");
					}
				}
				
				// Generar las sentencias INSERT
				for (JsonNode row : dataArray) {
					StringBuilder values = new StringBuilder();
					Iterator<JsonNode> fieldValues = row.iterator();
					while(fieldValues.hasNext()) {
						JsonNode value = fieldValues.next();
						if (value.isTextual()) {
							values.append("'").append(value.asText().replace("'", "''")).append("'");
						} else if (value.isNull()) {
							values.append("NULL");
						} else {
							values.append(value);
						}
						if (fieldValues.hasNext()) {
							values.append(", ");
						}
					}
					
					writer.write(String.format("INSERT INTO %s (%s) VALUES (%s);\n", tableName, columnNames, values));
				}
				LOGGER.info("Successfully exported {} rows for table '{}'.", dataArray.size(), tableName);
			} else {
				LOGGER.warn("No data to export for table '{}'.", tableName);
			}
		}
	}
}