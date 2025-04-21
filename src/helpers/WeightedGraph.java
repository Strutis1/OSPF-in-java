package helpers;

import constants.LinkType;
import ospf.Link;
import ospf.Router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightedGraph {
    Map<String, List<Link>> adjacencyList;
    Map<String, GraphNode> nodes;


    public WeightedGraph() {
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

        addNode(from);
        addNode(to);

        GraphNode fromNode = nodes.get(from);
        GraphNode toNode = nodes.get(to);
        fromNode.addNeighbor(toNode, weight);
    }

    public GraphNode getNodeById(String id) {
        return nodes.get(id);
    }

    public void addNode(String id) {
        nodes.putIfAbsent(id, new GraphNode(id));
    }
}
