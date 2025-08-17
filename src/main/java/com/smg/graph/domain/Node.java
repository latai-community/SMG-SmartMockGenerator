package com.smg.graph.domain;

import lombok.Data;

@Data
public class Node {
	private String name;
	
	public void addEdge(String name) {
		this.name = name;
	}
	
	public Node (String name){
		this.name = name;
	}
}

