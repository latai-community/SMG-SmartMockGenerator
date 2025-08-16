package com.smg.mockaroo.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Represents advanced configuration options for a Mockaroo field, such as
 * weighted probabilities or custom value lists.
 */
public class MockarooOptions {
	
	@JsonProperty("weighted_probabilities")
	private Map<String, Integer> weightedProbabilities;
	
	@JsonProperty("values")
	private List<String> values;
	
	// Getters and Setters
	
	public Map<String, Integer> getWeightedProbabilities() {
		return weightedProbabilities;
	}
	
	public void setWeightedProbabilities(Map<String, Integer> weightedProbabilities) {
		this.weightedProbabilities = weightedProbabilities;
	}
	
	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}
}