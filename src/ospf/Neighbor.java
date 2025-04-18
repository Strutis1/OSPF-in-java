package ospf;

import constants.NeighborState;

public class Neighbor {

    private final String neighborId;
    private NeighborState state;
    private int priority;
    private long lastHello;

    public Neighbor(String neighborId, int priority) {
        this.neighborId = neighborId;
        this.state = NeighborState.DOWN;
        this.priority = priority;
        this.lastHello = System.currentTimeMillis();
    }

    public String getNeighborId() {
        return neighborId;
    }

    public NeighborState getState() {
        return state;
    }

    public void setState(NeighborState state) {
        this.state = state;
    }

    public void updateHelloTimestamp() {
        this.lastHello = System.currentTimeMillis();
    }

    public boolean isAlive(long currentTimeMillis, int deadIntervalSeconds) {
        return (currentTimeMillis - lastHello) <= deadIntervalSeconds * 1000;
    }

    public void setPriority(int priority) {
        if(priority > 255 || priority < 0){
            System.out.println("Priority cannot exceed 255 or be lower than 0");
            return;
        }
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Neighbor ID: " + neighborId + ", State: " + state;
    }

    public int getPriority() {
        return priority;
    }
}
