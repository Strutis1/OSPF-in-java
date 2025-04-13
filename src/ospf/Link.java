package ospf;

import constants.LinkType;

public class Link {
    private final String destinationId;
    private final int cost;
    private final LinkType type; // optional

    public Link(String destinationId, int cost, LinkType type) {
        this.destinationId = destinationId;
        this.cost = cost;
        this.type = type;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public int getCost() {
        return cost;
    }

    public LinkType getType() {
        return type;
    }
}
