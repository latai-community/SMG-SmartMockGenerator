package com.smg.fileio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * FileManager provides a centralized utility for all file-related operations,
 * such as writing content to files and handling file permissions.
 */
public class FileManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);
	
	/**
	 * Writes a string content to a specified file.
	 *
	 * @param filePath The path of the file to write to.
	 * @param content The string content to be written.
	 * @param encoding The character encoding to use (e.g., "UTF-8").
	 * @throws IOException if an I/O error occurs during file writing.
	 */
	public void writeToFile(String filePath, String content, String encoding) throws IOException {
		Path path = Paths.get(filePath);
		Charset charset = getCharset(encoding);
		
		try {
			LOGGER.info("Writing content to file: {}", filePath);
			Files.writeString(path, content, charset);
		} catch (IOException e) {
			LOGGER.error("Failed to write to file: {}. Reason: {}", filePath, e.getMessage());
			throw e;
		}
	}
	
	/**
	 * Deletes a file at the specified path.
	 *
	 * @param filePath The path of the file to delete.
	 * @return true if the file was deleted successfully, false otherwise.
	 */
	public boolean deleteFile(String filePath) {
		try {
			LOGGER.info("Attempting to delete file: {}", filePath);
			return Files.deleteIfExists(Paths.get(filePath));
		} catch (IOException e) {
			LOGGER.error("Failed to delete file: {}. Reason: {}", filePath, e.getMessage());
			return false;
		}
	}
	
	/**
	 * Helper method to get a Charset object from a string name.
	 *
	 * @param encoding The string name of the encoding (e.g., "UTF-8").
	 * @return The Charset object.
	 */
	private Charset getCharset(String encoding) {
		if (encoding == null || encoding.isEmpty()) {
			return StandardCharsets.UTF_8;
		}
		try {
			return Charset.forName(encoding);
		} catch (Exception e) {
			LOGGER.warn("Invalid encoding '{}' specified. Falling back to UTF-8.", encoding);
			return StandardCharsets.UTF_8;
		}
	}
}