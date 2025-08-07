package com.smg.schemas;

import com.smg.schemas.entities.Column;
import com.smg.schemas.entities.ForeignKey;
import com.smg.schemas.entities.Schema;
import com.smg.schemas.entities.Table;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SchemaParser is responsible for parsing SQL DDL statements to build a Schema object.
 * It identifies tables, columns, and foreign key constraints from the SQL file content.
 */
public class SchemaParser {
	
	// Regex para capturar la tabla, las columnas y las constraints
	private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile(
		"CREATE\\s+TABLE\\s+\"?(\\w+)\"?\\s*\\((.*?)\\);",
		Pattern.DOTALL | Pattern.CASE_INSENSITIVE
	);
	
	// Regex para capturar columnas dentro de la tabla
	private static final Pattern COLUMN_PATTERN = Pattern.compile(
		"\"?(\\w+)\"?\\s+([a-zA-Z0-9_\\s]+?)(?:,|$)",
		Pattern.CASE_INSENSITIVE
	);
	
	// Regex para capturar constraints de clave foránea
	private static final Pattern FOREIGN_KEY_PATTERN = Pattern.compile(
		"CONSTRAINT\\s+\"?(\\w+)\"?\\s+FOREIGN\\s+KEY\\s+\\(\"?(\\w+)\"?\\)\\s+REFERENCES\\s+\"?(\\w+)\"?\\s*\\(\"?(\\w+)\"?\\)",
		Pattern.CASE_INSENSITIVE
	);
	
	/**
	 * Parses the SQL content of a given model and builds a Schema object.
	 *
	 * @param modelName The name of the database model (e.g., "HR").
	 * @param sqlContent The full SQL DDL content as a string.
	 * @return A Schema object populated with tables, columns, and foreign keys.
	 */
	public Schema parse(String modelName, String sqlContent) {
		Schema schema = new Schema(modelName);
		Matcher tableMatcher = CREATE_TABLE_PATTERN.matcher(sqlContent);
		
		while (tableMatcher.find()) {
			String tableName = tableMatcher.group(1);
			String tableContent = tableMatcher.group(2);
			
			Table table = new Table(tableName);
			
			// Parsear columnas
			Matcher columnMatcher = COLUMN_PATTERN.matcher(tableContent);
			while (columnMatcher.find()) {
				String columnName = columnMatcher.group(1);
				String dataType = columnMatcher.group(2).trim();
				if (!dataType.toLowerCase().startsWith("constraint")) { // Ignorar las constraints al principio
					table.addColumn(new Column(columnName, dataType));
				}
			}
			
			// Parsear claves foráneas
			Matcher fkMatcher = FOREIGN_KEY_PATTERN.matcher(tableContent);
			while (fkMatcher.find()) {
				String fkName = fkMatcher.group(1);
				String columnName = fkMatcher.group(2);
				String referencedTable = fkMatcher.group(3);
				String referencedColumn = fkMatcher.group(4);
				table.addForeignKey(new ForeignKey(fkName, columnName, referencedTable, referencedColumn));
			}
			
			schema.addTable(table);
		}
		return schema;
	}
}