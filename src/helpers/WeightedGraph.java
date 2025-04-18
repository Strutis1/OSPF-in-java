package helpers;

import constants.LinkType;
import ospf.Link;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedGraph {
    Map<String, List<Link>> adjacencyList;
    Map<String, GraphNode> nodes;


    public WeightedGraph(int n) {
        adjacencyList = new HashMap<>();
        nodes = new HashMap<>();
    }

    public Map<String, List<Link>> getAdjacencyList() {
        return adjacencyList;
    }

    public void setAdjacencyList(Map<String, List<Link>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public Map<String, GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, GraphNode> nodes) {
        this.nodes = nodes;
    }

    public void addEdge(String from, String to, int weight) {
        adjacencyList.putIfAbsent(from, new ArrayList<>());
        adjacencyList.get(from).add(new Link(to, weight, LinkType.POINT_TO_POINT));
    }


    //we get neighbors id's
    public String[] getNeighbors(GraphNode node) {
        return node.getRouter().getNeighbors().keySet().toArray(new String[0]);
    }
}
