package com.smg.graph.domain;


import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Graph {
	
	private Map<Node, List<Node>> adjacencyList = new HashMap<>();
	
	public List<Node> getNeighbors(Node node) {
		return adjacencyList.get(node);
	}
	
	public void addNode(Node node, List<Node> neighbors) {
		adjacencyList.putIfAbsent(node, new ArrayList<>());
		
		for (Node neighbor : neighbors) {
			adjacencyList.putIfAbsent(neighbor, new ArrayList<>());
			
			if (!adjacencyList.get(node).contains(neighbor)) {
				adjacencyList.get(node).add(neighbor);
			}
			if (!adjacencyList.get(neighbor).contains(node)) {
				adjacencyList.get(neighbor).add(node);
			}
		}
	}
	
	public Node getNodeByName(String name) {
		for (Node node : adjacencyList.keySet()) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}
	
	public void printGraph() {
		for (Node node : adjacencyList.keySet()) {
			
			System.out.print(node.getName() + " ---> ");
			List<Node> neighbors = adjacencyList.get(node);
			
			for (Node neighbor : neighbors)
				System.out.print(neighbor.getName() + ",");
			System.out.println();
		}
	}
	
}
