package com.smg.fileio;

import java.io.IOException;

/**
 * An interface for all data exporters. Defines a contract for converting
 * generated mock data into a specific file format.
 */
public interface IExporter {
	
	/**
	 * Exports the data to a file in a specific format.
	 *
	 * @param outputFilePath The full path of the output file.
	 * @param tableName The name of the table the data belongs to.
	 * @param mockDataJson The raw JSON string received from Mockaroo.
	 * @throws IOException if an I/O error occurs during file writing.
	 */
	void export(String outputFilePath, String tableName, String mockDataJson) throws IOException;
}