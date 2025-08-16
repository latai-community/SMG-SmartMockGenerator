package com.smg.schemas.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a database table, including its columns and foreign keys.
 */
public class Table {
	
	private String name;
	private List<Column> columns;
	private List<ForeignKey> foreignKeys;
	
	public Table(String name) {
		this.name = name;
		this.columns = new ArrayList<>();
		this.foreignKeys = new ArrayList<>();
	}
	
	// Getters and Setters
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Column> getColumns() {
		return columns;
	}
	
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	
	public List<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}
	
	public void setForeignKeys(List<ForeignKey> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}
	
	public void addColumn(Column column) {
		this.columns.add(column);
	}
	
	public void addForeignKey(ForeignKey foreignKey) {
		this.foreignKeys.add(foreignKey);
	}
}