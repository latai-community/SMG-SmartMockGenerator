package com.smg.graph;

import java.util.*;
import com.smg.graph.domain.*;

/**
 * Main class providing utilities for building graphs,
 * finding paths, and identifying extreme nodes within connected components.
 * This class demonstrates the use of breadth-first search (BFS)
 * to compute shortest paths, connected components, and the farthest
 * extremes (diameter) within subsets of selected nodes.
 * The sample {@code main} method shows how to build a disconnected graph,
 * select specific nodes, and compute paths through those nodes.
 */
public class Main {
	
	/**
	 * Builds a sample graph where all nodes are connected in one component.
	 *
	 * @return a {@link Graph} instance with connected nodes
	 */
	public static Graph buildDisconnectedGraph() {
		Graph graph = new Graph();
		
		// Create nodes
		Node r = new Node("R");
		Node c = new Node("C");
		Node l = new Node("L");
		Node d = new Node("D");
		Node jh = new Node("JH");
		Node e = new Node("E");
		Node j = new Node("J");
		
		// Define adjacency relationships
		graph.addNode(r, List.of(c));
		graph.addNode(c, List.of(r, l));
		graph.addNode(l, List.of(c, d));
		graph.addNode(d, List.of(l, jh, e));
		graph.addNode(jh, List.of(e, j, d));
		graph.addNode(e, List.of(jh, j, d));
		graph.addNode(j, List.of(e, jh));
		
		return graph;
	}
	
	/**
	 * Builds a sample graph where nodes are split into two disconnected components.
	 *
	 * @return a {@link Graph} instance with two separate components
	 */
	public static Graph buildGraph() {
		Graph graph = new Graph();
		
		// Create nodes
		Node r = new Node("R");
		Node c = new Node("C");
		Node l = new Node("L");
		Node jh = new Node("JH");
		Node e = new Node("E");
		Node j = new Node("J");
		
		// Define adjacency relationships
		graph.addNode(r, List.of(c));
		graph.addNode(c, List.of(r, l));
		graph.addNode(l, List.of(c));
		graph.addNode(jh, List.of(e, j));
		graph.addNode(e, List.of(jh, j));
		graph.addNode(j, List.of(e, jh));
		
		return graph;
	}
	
	/**
	 * Finds a path between two extreme nodes, ensuring that all selected nodes
	 * are included in the path if possible.
	 *
	 * @param graph         the input graph
	 * @param extremes      the set of extreme nodes (usually two nodes)
	 * @param selectedNodes nodes that must be included in the path
	 * @return the path as a list of nodes, or {@code null} if no path exists
	 */
	public static List<Node> findPathThroughSelected(Graph graph, Set<Node> extremes, Set<Node> selectedNodes) {
		if (extremes.size() < 2) return new ArrayList<>(extremes);
		
		Iterator<Node> it = extremes.iterator();
		Node start = it.next();
		Node end = it.next();
		
		List<Node> path = bfsShortestPath(graph, start, end);
		
		for (Node sel : selectedNodes) {
			assert path != null;
			if (!path.contains(sel)) {
				List<Node> extraPath1 = bfsShortestPath(graph, start, sel);
				List<Node> extraPath2 = bfsShortestPath(graph, sel, end);
				
				if (extraPath1 != null && extraPath2 != null) {
					// Merge two sub-paths to ensure 'sel' is included
					List<Node> combined = new ArrayList<>(extraPath1);
					combined.addAll(extraPath2.subList(1, extraPath2.size()));
					path = combined;
				}
			}
		}
		
		return path;
	}
	
	/**
	 * Computes the shortest path between two nodes using BFS.
	 *
	 * @param graph the input graph
	 * @param start the starting node
	 * @param end   the target node
	 * @return the shortest path as a list of nodes, or {@code null} if no path exists
	 */
	public static List<Node> bfsShortestPath(Graph graph, Node start, Node end) {
		Map<Node, Node> parent = new HashMap<>();
		Queue<Node> queue = new LinkedList<>();
		Set<Node> visited = new HashSet<>();
		
		queue.add(start);
		visited.add(start);
		parent.put(start, null);
		
		while (!queue.isEmpty()) {
			Node curr = queue.poll();
			if (curr.equals(end)) {
				// Reconstruct path from end to start
				List<Node> path = new LinkedList<>();
				for (Node at = end; at != null; at = parent.get(at)) {
					path.add(0, at);
				}
				return path;
			}
			for (Node neigh : graph.getAdjacencyList().getOrDefault(curr, List.of())) {
				if (!visited.contains(neigh)) {
					visited.add(neigh);
					parent.put(neigh, curr);
					queue.add(neigh);
				}
			}
		}
		return null;
	}
	
	/**
	 * Identifies the farthest extremes within each connected component
	 * that contains selected nodes.
	 *
	 * @param graph         the input graph
	 * @param selectedNodes subset of nodes of interest
	 * @return a list of sets, where each set contains one or two extreme nodes
	 *         for a connected component
	 */
	public static List<Set<Node>> findFarthestExtremesByComponent(Graph graph, Set<Node> selectedNodes) {
		List<Set<Node>> result = new ArrayList<>();
		Set<Node> visited = new HashSet<>();
		Map<Node, List<Node>> adj = graph.getAdjacencyList();
		
		for (Node start : selectedNodes) {
			if (!visited.contains(start)) {
				Set<Node> component = new HashSet<>();
				Queue<Node> queue = new LinkedList<>();
				queue.add(start);
				visited.add(start);
				
				// BFS to gather connected component
				while (!queue.isEmpty()) {
					Node curr = queue.poll();
					if (selectedNodes.contains(curr)) {
						component.add(curr);
					}
					for (Node neigh : adj.getOrDefault(curr, List.of())) {
						if (!visited.contains(neigh)) {
							visited.add(neigh);
							queue.add(neigh);
						}
					}
				}
				
				// For each component, identify extreme nodes
				if (component.size() == 1) {
					result.add(component);
				} else if (!component.isEmpty()) {
					result.add(findFarthestExtremes(graph, component));
				}
			}
		}
		return result;
	}
	
	/**
	 * Finds the two farthest nodes (in terms of BFS distance) among the selected nodes.
	 *
	 * @param graph         the input graph
	 * @param selectedNodes subset of nodes to analyze
	 * @return a set containing the two farthest nodes, or empty if none found
	 */
	public static Set<Node> findFarthestExtremes(Graph graph, Set<Node> selectedNodes) {
		Map<Node, List<Node>> adj = graph.getAdjacencyList();
		Node farthestA = null;
		Node farthestB = null;
		int maxDistance = -1;
		
		for (Node start : selectedNodes) {
			Map<Node, Integer> dist = new HashMap<>();
			Queue<Node> queue = new LinkedList<>();
			dist.put(start, 0);
			queue.add(start);
			
			// BFS from 'start'
			while (!queue.isEmpty()) {
				Node curr = queue.poll();
				for (Node neigh : adj.getOrDefault(curr, List.of())) {
					if (!dist.containsKey(neigh)) {
						dist.put(neigh, dist.get(curr) + 1);
						queue.add(neigh);
					}
				}
			}
			
			// Update farthest pair if needed
			for (Node target : selectedNodes) {
				if (!target.equals(start) && dist.containsKey(target)) {
					int d = dist.get(target);
					if (d > maxDistance) {
						maxDistance = d;
						farthestA = start;
						farthestB = target;
					}
				}
			}
		}
		
		Set<Node> extremes = new HashSet<>();
		if (farthestA != null) {
			extremes.add(farthestA);
			extremes.add(farthestB);
		}
		return extremes;
	}
	
	/**
	 * Demonstrates building a disconnected graph,
	 * selecting nodes, and finding path components through them.
	 *
	 * @param args program arguments (not used)
	 */
	public static void main(String[] args) {
		Graph graph = buildDisconnectedGraph();
		
		Node r = graph.getNodeByName("R");
		Node l = graph.getNodeByName("L");
		Node e = graph.getNodeByName("E");
		Node jh = graph.getNodeByName("JH");
		
		Set<Node> selected = Set.of(r, l, e, jh);
		
		List<Set<Node>> extremesByComponent = findFarthestExtremesByComponent(graph, selected);
		
		for (Set<Node> extremes : extremesByComponent) {
			List<Node> pathComponent = findPathThroughSelected(graph, extremes, selected);
			pathComponent.forEach(node -> System.out.print(node.getName() + " "));
		}
	}
}
