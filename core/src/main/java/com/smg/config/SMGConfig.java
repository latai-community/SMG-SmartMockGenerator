package com.smg.config;

import java.util.List;
import java.util.Map;

/**
 * SMGConfig class holds all configuration parameters for the SMG application.
 * This includes settings for the database model, tables, output files,
 * data generation specifics, and logging.
 */
public class SMGConfig {
	
	private String model;
	private List<String> tables;
	private String diagramOutput;
	private String schemaOutput;
	private String dataOutput;
	private Map<String, Integer> syntheticGenerate;
	private String encoding;
	private String errorFile;
	private String summaryFile;
	private String mockConfig;
	private String mockApiKey;
	
	public SMGConfig() {
		// Default constructor
	}
	
	// Getters and Setters for all fields
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public List<String> getTables() {
		return tables;
	}
	
	public void setTables(List<String> tables) {
		this.tables = tables;
	}
	
	public String getDiagramOutput() {
		return diagramOutput;
	}
	
	public void setDiagramOutput(String diagramOutput) {
		this.diagramOutput = diagramOutput;
	}
	
	public String getSchemaOutput() {
		return schemaOutput;
	}
	
	public void setSchemaOutput(String schemaOutput) {
		this.schemaOutput = schemaOutput;
	}
	
	public String getDataOutput() {
		return dataOutput;
	}
	
	public void setDataOutput(String dataOutput) {
		this.dataOutput = dataOutput;
	}
	
	public Map<String, Integer> getSyntheticGenerate() {
		return syntheticGenerate;
	}
	
	public void setSyntheticGenerate(Map<String, Integer> syntheticGenerate) {
		this.syntheticGenerate = syntheticGenerate;
	}
	
	public String getEncoding() {
		return encoding;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getErrorFile() {
		return errorFile;
	}
	
	public void setErrorFile(String errorFile) {
		this.errorFile = errorFile;
	}
	
	public String getSummaryFile() {
		return summaryFile;
	}
	
	public void setSummaryFile(String summaryFile) {
		this.summaryFile = summaryFile;
	}
	
	public String getMockConfig() {
		return mockConfig;
	}
	
	public void setMockConfig(String mockConfig) {
		this.mockConfig = mockConfig;
	}
	
	public String getMockApiKey() {
		return mockApiKey;
	}
	
	public void setMockApiKey(String mockApiKey) {
		this.mockApiKey = mockApiKey;
	}
}