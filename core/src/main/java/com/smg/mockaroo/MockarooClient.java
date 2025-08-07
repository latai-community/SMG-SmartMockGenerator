package com.smg.mockaroo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smg.logging.ErrorLogger;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Client for interacting with the Mockaroo API to generate synthetic data.
 */
public class MockarooClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MockarooClient.class);
	private static final String API_ENDPOINT = "https://api.mockaroo.com/api/generate.json";
	
	private final String mockApiKey;
	private final ErrorLogger errorLogger;
	private final ObjectMapper objectMapper;
	
	public MockarooClient(String mockApiKey, ErrorLogger errorLogger) {
		this.mockApiKey = mockApiKey;
		this.errorLogger = errorLogger;
		this.objectMapper = new ObjectMapper();
	}
	
	/**
	 * Sends a schema to the Mockaroo API and returns the generated data.
	 * The return type is now ArrayNode to correctly handle the JSON response.
	 *
	 * @param mockarooSchema The schema to use for data generation.
	 * @return An ArrayNode containing the generated data.
	 * @throws IOException If a network or I/O error occurs.
	 * @throws InterruptedException If the operation is interrupted.
	 */
	public ArrayNode generateData(ArrayNode mockarooSchema) throws IOException, InterruptedException {
		// Create the JSON payload for the API request
		ObjectNode payload = objectMapper.createObjectNode();
		payload.set("schema", mockarooSchema);
		payload.put("key", mockApiKey);
		
		HttpPost httpPost = new HttpPost(API_ENDPOINT);
		httpPost.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
		HttpEntity entity = new StringEntity(payload.toString(), StandardCharsets.UTF_8);
		httpPost.setEntity(entity);
		
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
			 CloseableHttpResponse response = httpClient.execute(httpPost)) {
			
			int statusCode = response.getCode();
			if (statusCode == HttpStatus.SC_OK) {
				// Parse the response body as a JSON array
				String responseBody = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
				JsonNode rootNode = objectMapper.readTree(responseBody);
				if (rootNode.isArray()) {
					return (ArrayNode) rootNode;
				} else {
					String errorMessage = "Mockaroo API did not return a JSON array.";
					errorLogger.logError(errorMessage, null);
					throw new IOException(errorMessage);
				}
			} else {
				String errorMessage = "Mockaroo API call failed with status code: " + statusCode;
				String errorDetails = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
				LOGGER.error(errorMessage + " - Details: " + errorDetails);
				errorLogger.logError(errorMessage + " - Details: " + errorDetails, null);
				throw new IOException(errorMessage);
			}
		}
	}
}
