package ospf;

import java.util.ArrayList;
import java.util.List;

public class Interface {
    private final String interfaceId;
    private final String ipAddress;
    private final String areaId;
    private final int cost;
    private final int helloInterval;//in Seconds
    private final int deadInterval;//in Seconds
    private final List<Neighbor> neighbors;


    public Interface(String interfaceId, String ipAddress, String areaId, int cost) {
        this.interfaceId = interfaceId;
        this.ipAddress = ipAddress;
        this.areaId = areaId;
        this.cost = cost;
        this.helloInterval = 10;
        this.deadInterval = 40;
        this.neighbors = new ArrayList<>();
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getCost() {
        return cost;
    }

    public String getAreaId() {
        return areaId;
    }

    public List<Neighbor> getNeighbors() {
        return neighbors;
    }

}
