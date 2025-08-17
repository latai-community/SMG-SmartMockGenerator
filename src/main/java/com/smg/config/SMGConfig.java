package com.smg.config;

import lombok.Data;

import java.util.Map;
import java.util.Set;

/**
 * SMGConfig class holds all configuration parameters for the SMG application.
 * This includes settings for the database model, tables, output files,
 * data generation specifics, and logging.
 */
@Data
public class SMGConfig {
	private String model;
	private Set<String> tables;
	private String diagramOutput;
	private String schemaOutput;
	private String dataOutput;
	private Map<String, Integer> syntheticGenerate;
	private String encoding;
	private String errorFile;
	private String summaryFile;
	private String mockConfig;
	private String mockApiKey;
}