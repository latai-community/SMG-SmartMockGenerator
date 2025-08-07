package com.smg.mockaroo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smg.schemas.entities.Table;
import com.smg.schemas.entities.Column;

/**
 * MockarooSchemaGenerator is responsible for converting a database Table object
 * into a JSON schema that can be sent to the Mockaroo API for data generation.
 */
public class MockarooSchemaGenerator {
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	/**
	 * Generates a JSON schema for the Mockaroo API based on a given table structure.
	 * The schema is an array of field objects.
	 *
	 * @param table The Table object to generate a schema for.
	 * @param numRows The number of rows to generate.
	 * @return A JSON ArrayNode representing the Mockaroo schema.
	 */
	public ArrayNode generateSchema(Table table, int numRows) {
		ArrayNode mockarooSchema = objectMapper.createArrayNode();
		
		// Mockaroo requires the number of rows to be a part of the schema
		ObjectNode numRowsNode = objectMapper.createObjectNode();
		numRowsNode.put("name", "num_rows");
		numRowsNode.put("type", "number");
		numRowsNode.put("value", numRows);
		mockarooSchema.add(numRowsNode);
		
		for (Column column : table.getColumns()) {
			ObjectNode fieldNode = objectMapper.createObjectNode();
			fieldNode.put("name", column.getName());
			fieldNode.put("type", inferMockarooType(column.getDataType()));
			mockarooSchema.add(fieldNode);
		}
		
		return mockarooSchema;
	}
	
	/**
	 * Infers the appropriate Mockaroo data type based on the SQL data type.
	 * This is a simple, hardcoded mapping. A more advanced version could use
	 * a configuration file.
	 *
	 * @param sqlDataType The SQL data type (e.g., VARCHAR2, NUMBER, DATE).
	 * @return The corresponding Mockaroo data type.
	 */
	private String inferMockarooType(String sqlDataType) {
		String lowerCaseType = sqlDataType.toLowerCase();
		
		if (lowerCaseType.contains("char") || lowerCaseType.contains("varchar")) {
			return "Words";
		} else if (lowerCaseType.contains("number") || lowerCaseType.contains("int") || lowerCaseType.contains("long")) {
			return "Number";
		} else if (lowerCaseType.contains("date") || lowerCaseType.contains("timestamp")) {
			return "Date";
		} else {
			// Default to a simple words type for unrecognized types
			return "Words";
		}
	}
}