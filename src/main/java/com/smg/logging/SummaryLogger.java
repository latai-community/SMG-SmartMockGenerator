package com.smg.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * SummaryLogger is a utility class for writing a high-level summary of the
 * SMG application's execution to a designated summary log file.
 */
public class SummaryLogger {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SummaryLogger.class);
	
	private final List<String> generatedFiles = new ArrayList<>();
	private final List<String> failedFiles = new ArrayList<>();
	private long totalBytesGenerated = 0;
	
	/**
	 * Logs a general summary message at the end of the process.
	 *
	 * @param message The final message to log.
	 */
	public void logSummary(String message) {
		LOGGER.info("--- SMG Execution Summary ---");
		LOGGER.info(message);
		LOGGER.info("Total files generated: {}", generatedFiles.size());
		for (String file : generatedFiles) {
			LOGGER.info("- {}", file);
		}
		LOGGER.info("Total files that failed to process: {}", failedFiles.size());
		for (String file : failedFiles) {
			LOGGER.info("- {}", file);
		}
		LOGGER.info("Total data size generated: {} bytes", totalBytesGenerated);
		LOGGER.info("-----------------------------");
	}
	
	/**
	 * Records a successfully generated file to the summary.
	 *
	 * @param fileName The name of the file that was successfully generated.
	 * @param fileSizeInBytes The size of the generated file in bytes.
	 */
	public void logFileGenerated(String fileName, long fileSizeInBytes) {
		this.generatedFiles.add(fileName);
		this.totalBytesGenerated += fileSizeInBytes;
	}
	
	/**
	 * Records a file that failed to be processed.
	 *
	 * @param fileName The name of the file that failed.
	 * @param reason The reason for the failure.
	 */
	public void logFailedFile(String fileName, String reason) {
		this.failedFiles.add(fileName + " (Reason: " + reason + ")");
	}
}