package com.smg.schemas.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete database schema, containing a list of tables.
 */
public class Schema {
	
	private String name;
	private List<Table> tables;
	
	public Schema(String name) {
		this.name = name;
		this.tables = new ArrayList<>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<Table> getTables() {
		return tables;
	}
	
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}
	
	public void addTable(Table table) {
		this.tables.add(table);
	}
}