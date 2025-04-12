package ospf;

import constants.RouterRole;

import java.util.*;

public class Router {
    //fucking hell

    private final String routerId;
    private final List<String> connectedPrefixes;
    private final List<Interface> interfaces;
    private final Map<String, Neighbor> neighbors;
    private final LSDB lsdb;
    private final RoutingTable routingTable;
    private final Set<String> areas;    // Areas this router participates in

    public Router(String routerId) {
        this.routerId = routerId;
        this.connectedPrefixes = new ArrayList<>();
        this.interfaces = new ArrayList<>();
        this.neighbors = new HashMap<>();
        this.lsdb = new LSDB();
        this.routingTable = new RoutingTable();
        this.areas = new HashSet<>();
    }


    public String getRouterId() {
        return routerId;
    }

    public List<String> getConnectedPrefixes() {
        return connectedPrefixes;
    }

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public Map<String, Neighbor> getNeighbors() {
        return neighbors;
    }

    public LSDB getLsdb() {
        return lsdb;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public Set<String> getAreas() {
        return areas;
    }

    public boolean isBackbone() {
        return areas.contains(0);
    }

    public void addPrefix(String prefix) {
        connectedPrefixes.add(prefix);
    }

    public void addInterface(Interface intface) {
        interfaces.add(intface);
        areas.add(intface.getAreaId());
    }

    public Set<RouterRole> getRoles() {
        Set<RouterRole> roles = new HashSet<>();

        if (isBackbone()) {
            roles.add(RouterRole.BACKBONE);
        }

        if (areas.size() == 1) {
            roles.add(RouterRole.INTERNAL);
        }

        if (areas.size() > 1) {
            roles.add(RouterRole.AREA_BORDER);
        }


        return roles;
    }
}
