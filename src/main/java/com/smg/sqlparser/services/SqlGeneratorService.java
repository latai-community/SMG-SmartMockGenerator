package com.smg.sqlparser.services;

import com.smg.generation.DataGeneratorTmp;
import com.smg.sqlparser.domain.sql.Column;
import com.smg.sqlparser.domain.sql.Schema;
import com.smg.sqlparser.domain.sql.Table;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsible for generating SQL statements (DDL and DML)
 * from a parsed database schema and a set of selected tables.
 * <p>
 * This service can generate:
 * <ul>
 *   <li>CREATE TABLE statements (with optional foreign key filtering)</li>
 *   <li>INSERT statements (synthetic/example data)</li>
 *   <li>TRUNCATE TABLE statements</li>
 *   <li>DROP TABLE statements</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Schema schema = SqlSchemaParser.parseSchemaFromFile(path, "HR");
 * Set<String> selectedTables = Set.of("departments", "employees");
 *
 * SqlGeneratorService generator = new SqlGeneratorService(schema);
 *
 * String createSql = generator.generateCreateSql(selectedTables);
 * String insertSql = generator.generateInsertSql(selectedTables);
 * String truncateSql = generator.generateTruncateSql(selectedTables);
 * String dropSql = generator.generateDropSql(selectedTables);
 * }</pre>
 *
 * This design keeps all SQL generation logic centralized, making it
 * easy to extend and test independently from the application entry point.
 */
public class SqlGeneratorService {
	
	private final Schema schema;
	
	/**
	 * Constructs a new {@code SqlGeneratorService}.
	 *
	 * @param schema the parsed schema containing tables and metadata
	 */
	public SqlGeneratorService(Schema schema) {
		this.schema = schema;
	}
	
	/**
	 * Generates CREATE TABLE statements for the given set of tables.
	 * <p>
	 * Foreign keys referencing tables not included in {@code selectedTables}
	 * will be ignored.
	 *
	 * @param selectedTables a set of table names to generate CREATE statements for
	 * @return a formatted SQL string containing CREATE TABLE statements
	 */
	public String generateCreateSql(Set<String> selectedTables) {
		return selectedTables.stream()
			.map(name -> {
				Table table = schema.getTables().get(name);
				if (table == null) {
					throw new IllegalArgumentException("Table not found in schema: " + name);
				}
				Table filteredTable = table.getFilteredCopy(selectedTables);
				return filteredTable.toCreateSql();
			})
			.collect(Collectors.joining("\n\n"));
	}
	
	/**
	 * Generates INSERT statements for the given set of tables.
	 * <p>
	 * Currently, this method provides placeholders and should be extended
	 * to generate real synthetic/example data.
	 *
	 * @param selectedTables a set of table names to generate INSERT statements for
	 * @return a formatted SQL string containing INSERT statements
	 */
	public String generateInsertSql(Set<String> selectedTables, int rowsPerTable) {
		return selectedTables.stream()
			.map(name -> {
				var table = schema.getTables().get(name).getFilteredCopy(selectedTables);
				if (table == null) {
					throw new IllegalArgumentException("Table not found: " + name);
				}
				
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < rowsPerTable; i++) {
					int finalI = i;
					String values = table.getColumns().stream()
						.map(col -> DataGeneratorTmp.generateValue(col, finalI))
						.collect(Collectors.joining(", "));
					
					String cols = table.getColumns().stream()
						.map(Column::getName)
						.collect(Collectors.joining(", "));
					
					sb.append("INSERT INTO ").append(name)
						.append(" (").append(cols).append(") VALUES (")
						.append(values).append(");\n");
				}
				return sb.toString();
			})
			.collect(Collectors.joining("\n"));
	}
	
	/**
	 * Generates TRUNCATE TABLE statements for the given set of tables.
	 *
	 * @param selectedTables a set of table names to generate TRUNCATE statements for
	 * @return a formatted SQL string containing TRUNCATE TABLE statements
	 */
	public String generateTruncateSql(Set<String> selectedTables) {
		return selectedTables.stream()
			.map(name -> "TRUNCATE TABLE " + name + ";")
			.collect(Collectors.joining("\n"));
	}
	
	/**
	 * Generates DROP TABLE statements for the given set of tables.
	 *
	 * @param selectedTables a set of table names to generate DROP statements for
	 * @return a formatted SQL string containing DROP TABLE statements
	 */
	public String generateDropSql(Set<String> selectedTables) {
		return selectedTables.stream()
			.map(name -> "DROP TABLE " + name + ";")
			.collect(Collectors.joining("\n"));
	}
}
