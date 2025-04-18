package helpers;

import constants.LinkType;

public class LinkDescription {
    private LinkType linkType;
    private String targetId;   // neighbor router ID or network ID
    private int cost;

    public LinkDescription(LinkType linkType, String targetId, int cost) {
        this.linkType = linkType;
        this.targetId = targetId;
        this.cost = cost;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public String getTargetId() {
        return targetId;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Link to " + targetId + " (" + linkType + ", cost = " + cost + ")";
    }
}