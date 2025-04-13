package ospf;

import constants.LinkType;

public class LinkDescription {
    private LinkType linkType;
    private String targetId;   // neighbor router ID or network ID
    private int cost;
}