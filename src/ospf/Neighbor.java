package ospf;

import constants.NeighborState;

public class Neighbor {

    private final String neighborId;
    private NeighborState state;
    private long lastHello;
    public Neighbor(String neighborId) {
        this.neighborId = neighborId;
        this.state = NeighborState.DOWN;
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
}
