package helpers;

import ospf.Router;

import java.util.HashMap;
import java.util.Map;

public class GraphNode implements Comparable<GraphNode> {
    private final String routerId;
    private int distance;
    private GraphNode previous;
    private boolean visited;

    private final Map<GraphNode, Integer> neighbors = new HashMap<>();


    public GraphNode(String routerId) {
        this.routerId = routerId;
        this.distance = Integer.MAX_VALUE; //initially infinity
        this.previous = null;
        this.visited = false;
    }

    public String getRouterId() {
        return routerId;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public GraphNode getPrevious() {
        return previous;
    }

    public void setPrevious(GraphNode previous) {
        this.previous = previous;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void addNeighbor(GraphNode neighbor, int cost) {
        neighbors.put(neighbor, cost);
    }

    public Map<GraphNode, Integer> getNeighbors() {
        return neighbors;
    }



    @Override
    public int compareTo(GraphNode o) {
        return Integer.compare(this.distance, o.distance);
    }

    @Override
    public String toString() {
        return "Node(" + routerId + ", dist=" + distance + ")";
    }
}
