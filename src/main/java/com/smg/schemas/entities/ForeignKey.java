package com.smg.schemas.entities;

/**
 * Represents a foreign key constraint, linking a column in one table to a column
 * in another table.
 */
public class ForeignKey {
	
	private String name;
	private String columnName;
	private String referencedTable;
	private String referencedColumn;
	
	public ForeignKey(String name, String columnName, String referencedTable, String referencedColumn) {
		this.name = name;
		this.columnName = columnName;
		this.referencedTable = referencedTable;
		this.referencedColumn = referencedColumn;
	}
	
	// Getters and Setters
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public String getReferencedTable() {
		return referencedTable;
	}
	
	public void setReferencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
	}
	
	public String getReferencedColumn() {
		return referencedColumn;
	}
	
	public void setReferencedColumn(String referencedColumn) {
		this.referencedColumn = referencedColumn;
	}
}