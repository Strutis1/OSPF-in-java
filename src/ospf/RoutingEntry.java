package ospf;

public class RoutingEntry {
    private final String prefix;
    private final String nextHopId;
    private final String outgoingInterfaceId;
    private final int cost;

    public RoutingEntry(String prefix, String nextHopId, String interfaceId, int cost) {
        this.prefix = prefix;
        this.nextHopId = nextHopId;
        this.outgoingInterfaceId = interfaceId;
        this.cost = cost;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNextHopId() {
        return nextHopId;
    }

    public String getOutgoingInterfaceId() {
        return outgoingInterfaceId;
    }

    public int getCost() {
        return cost;
    }



}
