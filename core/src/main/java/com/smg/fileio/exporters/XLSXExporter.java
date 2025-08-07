package com.smg.fileio.exporters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smg.fileio.IExporter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * XLSXExporter exports data by converting Mockaroo's JSON output into a
 * a multi-sheet Excel (.xlsx) file. Each table's data is written to a separate sheet.
 */
public class XLSXExporter implements IExporter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XLSXExporter.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public void export(String outputFilePath, String tableName, String mockDataJson) throws IOException {
		LOGGER.info("Exporting data for table '{}' to XLSX file: {}", tableName, outputFilePath);
		
		Workbook workbook;
		try {
			// Attempt to load existing workbook to add a new sheet
			workbook = new XSSFWorkbook(outputFilePath);
		} catch (IOException e) {
			// If file doesn't exist, create a new workbook
			workbook = new XSSFWorkbook();
		}
		
		try {
			JsonNode dataArray = objectMapper.readTree(mockDataJson);
			
			if (dataArray.isArray() && !dataArray.isEmpty()) {
				Sheet sheet = workbook.createSheet(tableName);
				
				// Create header row
				Row headerRow = sheet.createRow(0);
				JsonNode firstRow = dataArray.get(0);
				Iterator<String> fieldNames = firstRow.fieldNames();
				int colNum = 0;
				while (fieldNames.hasNext()) {
					Cell cell = headerRow.createCell(colNum++);
					cell.setCellValue(fieldNames.next());
				}
				
				// Create data rows
				int rowNum = 1;
				for (JsonNode jsonRow : dataArray) {
					Row excelRow = sheet.createRow(rowNum++);
					colNum = 0;
					for (JsonNode field : jsonRow) {
						Cell cell = excelRow.createCell(colNum++);
						if (field.isTextual()) {
							cell.setCellValue(field.asText());
						} else if (field.isNumber()) {
							cell.setCellValue(field.asDouble());
						} else if (field.isBoolean()) {
							cell.setCellValue(field.asBoolean());
						} else {
							cell.setCellValue(field.toString());
						}
					}
				}
				LOGGER.info("Successfully exported {} rows to sheet '{}'.", dataArray.size(), tableName);
			} else {
				LOGGER.warn("No data to export for table '{}'.", tableName);
			}
			
			// Write the workbook to the file
			try (FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {
				workbook.write(fileOut);
			}
			
		} finally {
			workbook.close();
		}
	}
}