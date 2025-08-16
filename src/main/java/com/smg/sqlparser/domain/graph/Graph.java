package com.smg.sqlparser.domain.graph;

import lombok.Getter;
import lombok.Setter;

import java.util.*;


@Getter
@Setter
public class Graph {


    private Map<Node, List<Node>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(Node from, Node to) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.get(from).add(to);

        adjacencyList.putIfAbsent(to, new ArrayList<>());
        adjacencyList.get(to).add(from);
    }

    public List<Node> getNeighbors(Node node) {
        return adjacencyList.getOrDefault(node, Collections.emptyList());
    }

    public Node findNodeByName(String tableName) {
        for (Node node : adjacencyList.keySet()) {
            if (node.getTable().getName().equalsIgnoreCase(tableName)) {
                return node;
            }
        }
        return null;
    }

    public Set<Node> getNodes() {
        return adjacencyList.keySet();
    }
}
