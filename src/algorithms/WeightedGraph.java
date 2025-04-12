package algorithms;

import ospf.Router;

import java.util.HashMap;
import java.util.Map;

public class WeightedGraph {
    Map<String, Map<String, Integer>> adjacencyList;
    Map<String, GraphNode> nodes;


    public WeightedGraph(int n) {
        adjacencyList = new HashMap<>();
        nodes = new HashMap<>();
    }

    public Map<String, Map<String, Integer>> getAdjacencyList() {
        return adjacencyList;
    }

    public void setAdjacencyList(Map<String, Map<String, Integer>> matrix) {
        this.adjacencyList = matrix;
    }

    public Map<String, GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, GraphNode> nodes) {
        this.nodes = nodes;
    }

    public void addEdge(String from, String to, int weight) {
        adjacencyList.putIfAbsent(from, new HashMap<>());
        adjacencyList.get(from).put(to, weight);
    }


    //we get neighbors id's
    public String[] getNeighbors(GraphNode node) {
        return node.getRouter().getNeighbors().keySet().toArray(new String[0]);
    }
}
