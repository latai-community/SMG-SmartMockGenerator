package com.smg.generation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.smg.config.SMGConfig;
import com.smg.fileio.IExporter;
import com.smg.fileio.exporters.*;
import com.smg.logging.ErrorLogger;
import com.smg.logging.SummaryLogger;
import com.smg.mockaroo.MockarooClient;
import com.smg.mockaroo.MockarooSchemaGenerator;
import com.smg.schemas.entities.Schema;
import com.smg.schemas.entities.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * DataGenerator is the main orchestrator for generating synthetic data.
 * It uses the cleaned schema, communicates with the Mockaroo API, and
 * exports the data in the specified formats.
 */
public class DataGenerator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);
	
	private final SMGConfig config;
	private final ErrorLogger errorLogger;
	private final SummaryLogger summaryLogger;
	private final MockarooClient mockarooClient;
	private final MockarooSchemaGenerator mockarooSchemaGenerator;
	private final ObjectMapper objectMapper;
	
	public DataGenerator(SMGConfig config, ErrorLogger errorLogger, SummaryLogger summaryLogger) {
		this.config = config;
		this.errorLogger = errorLogger;
		this.summaryLogger = summaryLogger;
		this.mockarooClient = new MockarooClient(config.getMockApiKey(), errorLogger);
		this.mockarooSchemaGenerator = new MockarooSchemaGenerator();
		this.objectMapper = new ObjectMapper();
	}
	
	/**
	 * Generates synthetic data for each table in the cleaned schema and exports it.
	 *
	 * @param cleanedSchema The schema object with only the selected tables.
	 */
	public void generate(Schema cleanedSchema) {
		if (config.getSyntheticGenerate() == null || config.getSyntheticGenerate().isEmpty()) {
			LOGGER.warn("No tables specified for synthetic data generation. Process will not generate data.");
			return;
		}
		
		// Generate schema output if requested
		if (config.getSchemaOutput() != null && !config.getSchemaOutput().isEmpty()) {
			// Logic for schema export would go here, calling a dedicated Exporter.
		}
		
		// Generate and export data for each table
		for (Table table : cleanedSchema.getTables()) {
			String tableName = table.getName();
			int rowCount = config.getSyntheticGenerate().getOrDefault(tableName, 100);
			
			try {
				// Step 1: Generate Mockaroo schema JSON
				ArrayNode mockarooSchema = mockarooSchemaGenerator.generateSchema(table, rowCount);
				
				// Step 2: Fetch synthetic data from Mockaroo.
				// The method now returns an ArrayNode.
				ArrayNode mockDataArray = mockarooClient.generateData(mockarooSchema);
				
				// Step 3: Serialize the ArrayNode to a JSON string for export.
				String mockDataJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mockDataArray);
				
				// Step 4: Export the data in the specified format
				exportData(tableName, mockDataJson);
				
				summaryLogger.logFileGenerated(config.getDataOutput(), mockDataJson.length());
			} catch (IOException | InterruptedException e) {
				String errorMessage = "Failed to generate data for table: " + tableName;
				errorLogger.logError(errorMessage, e);
				summaryLogger.logFailedFile(config.getDataOutput(), errorMessage);
			}
		}
	}
	
	/**
	 * Exports the generated data to the specified output format.
	 *
	 * @param tableName The name of the table the data belongs to.
	 * @param mockDataJson The JSON string containing the generated data.
	 * @throws IOException if there is an error writing the file.
	 */
	private void exportData(String tableName, String mockDataJson) throws IOException {
		String dataOutput = config.getDataOutput();
		String fileExtension = getFileExtension(dataOutput);
		
		IExporter exporter;
		switch (fileExtension.toLowerCase()) {
			case "sql" -> exporter = new SQLExporter();
			case "json" -> exporter = new JSONExporter();
			case "csv" -> exporter = new CSVExporter();
			case "xlsx" -> exporter = new XLSXExporter();
			default -> throw new IllegalArgumentException("Unsupported output format: " + fileExtension);
		}
		
		// The exporter now receives the serialized JSON string.
		exporter.export(dataOutput, tableName, mockDataJson);
	}
	
	private String getFileExtension(String filename) {
		if (filename == null || filename.lastIndexOf('.') == -1) {
			return "";
		}
		return filename.substring(filename.lastIndexOf('.') + 1);
	}
}
