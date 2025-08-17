package com.smg.sqlparser.parser.SQL99;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.*;
import com.smg.sqlparser.domain.sql.Schema;
import com.smg.sqlparser.domain.sql.Table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SqlSchemaParser {
	
	public static Schema parseSchemaFromFile(Path filePath, String schemaName) throws IOException, Exception {
		String sqlContent = Files.readString(filePath);
		return parseSchemaFromString(sqlContent, schemaName);
	}
	
	public static Schema parseSchemaFromString(String sqlContent, String schemaName) throws Exception {
		Schema schema = new Schema(schemaName);
		String cleanSqlContent = cleanComments(sqlContent);
		
		List<Statement> statements = CCJSqlParserUtil.parseStatements(cleanSqlContent);
		
		for (Statement stmt : statements) {
			if (stmt instanceof CreateTable createTable) {
				Table table = TableParser.parseTableWithoutForeignKeys(createTable);
				schema.addTable(table);
			}
		}
		
		for (Statement stmt : statements) {
			if (stmt instanceof CreateTable createTable) {
				Table table = schema.getTables().get(createTable.getTable().getName());
				TableParser.addForeignKeys(table, createTable, schema);
			}
		}
		
		return schema;
	}
	
	/**
	 * Removes SQL comments from the provided SQL content.
	 *
	 * @param sqlContent SQL content as a string.
	 * @return SQL content without comments.
	 */
	private static String cleanComments(String sqlContent) {
		return sqlContent.replaceAll("(?m)^\\s*--.*(?:\\r?\\n)?", "");
	}
}