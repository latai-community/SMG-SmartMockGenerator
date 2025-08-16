package com.smg.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ErrorLogger is a utility class for logging detailed error messages,
 * including stack traces, to a designated error file.
 */
public class ErrorLogger {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorLogger.class);
	
	/**
	 * Logs a detailed error message with a timestamp and stack trace.
	 *
	 * @param message The error message to log.
	 * @param e The exception that caused the error (can be null).
	 */
	public void logError(String message, Throwable e) {
		// En un escenario real, Logback se configuraría para escribir en un archivo específico.
		// Aquí, usamos el logger estándar de SLF4J, que está configurado por el pom.xml.
		// Para asegurar que los errores se registren, usamos el nivel ERROR.
		LOGGER.error("SMG Error: {}", message, e);
	}
}