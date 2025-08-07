package com.smg.mockaroo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single field in a Mockaroo schema, used for data generation.
 * This class is a simple POJO for serialization to JSON.
 */
public class MockarooField {
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("options")
	private MockarooOptions options;
	
	public MockarooField(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	// Getters and Setters
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public MockarooOptions getOptions() {
		return options;
	}
	
	public void setOptions(MockarooOptions options) {
		this.options = options;
	}
}