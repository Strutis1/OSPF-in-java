package algorithms;

import helpers.GraphNode;
import helpers.WeightedGraph;
import ospf.Link;

import java.util.*;

public class Dijkstra {
    public static Map<String, Integer> computeShortestPaths(
            WeightedGraph graph, String sourceId, Map<String, String> previousHop){
        Map<String, Integer> distances = new HashMap<>();
        PriorityQueue<GraphNode> queue = new PriorityQueue<>();

        for (GraphNode node : graph.getNodes().values()) {
            node.setDistance(Integer.MAX_VALUE);
            node.setPrevious(null);
            node.setVisited(false);
        }

        GraphNode sourceNode = graph.getNodes().get(sourceId);
        sourceNode.setDistance(0);
        queue.add(sourceNode);
        while (!queue.isEmpty()) {
            GraphNode current = queue.poll();

            if (current.isVisited()) continue;
            current.setVisited(true);

            String currentId = current.getRouterId();
            distances.put(currentId, current.getDistance());

            for (Map.Entry<GraphNode, Integer> entry : current.getNeighbors().entrySet()) {
                GraphNode neighborNode = entry.getKey();
                int weight = entry.getValue();

                if (neighborNode.isVisited()) continue;

                int alt = current.getDistance() + weight;
                if (alt < neighborNode.getDistance()) {
                    neighborNode.setDistance(alt);
                    neighborNode.setPrevious(current);
                    previousHop.put(neighborNode.getRouterId(), current.getRouterId());
                    queue.add(neighborNode);
                }
            }
        }

        return distances;
    }


    public static String getNextHop(String sourceId, String destId, Map<String, String> previousHop) {
        String current = destId;
        String prev = previousHop.get(current);

        while (prev != null && !prev.equals(sourceId)) {
            current = prev;
            prev = previousHop.get(current);
        }

        return current.equals(sourceId) ? destId : current;
    }


}
