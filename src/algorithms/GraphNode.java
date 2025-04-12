package algorithms;

import ospf.Router;

public class GraphNode implements Comparable<GraphNode> {
    private final Router router;
    private int distance;
    private GraphNode previous;
    private boolean visited;


    GraphNode(Router router) {
        this.router = router;
        this.distance = Integer.MAX_VALUE; //initially infinity
        this.previous = null;
        this.visited = false;
    }

    public Router getRouter(){
        return router;
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





    @Override
    public int compareTo(GraphNode o) {
        return Integer.compare(this.distance, o.distance);
    }
}
