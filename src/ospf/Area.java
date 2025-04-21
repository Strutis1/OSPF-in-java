package ospf;

import constants.AreaType;
import constants.OSPFDefaults;
import helpers.*;
import lsas.LSA;
import lsas.LSAHeader;
import lsas.RouterLSA;

import java.util.*;

public class Area {
    private final String areaId;
    private final Set<Interface> assignedInterfaces;
    private final AreaType type;
    private final LSDB lsdb;
    private final List<Router> routers = new ArrayList<>();

    public Area(String areaId, AreaType type, LSDB lsdb) {
        this.areaId = areaId;
        this.assignedInterfaces = new HashSet<>();
        this.type = type;
        this.lsdb = lsdb;
    }



    public void addInterface(Interface iface) {
        assignedInterfaces.add(iface);
        iface.setArea(this);

        Router router = iface.getOwner();
        if (!routers.contains(router)) {
            routers.add(router);
        }
    }

    public List<Router> getRouters() {
        return routers;
    }

    public void installLSA(LSA lsa) {
        lsdb.addOrUpdateLSA(lsa);
    }

    public void printLSDB() {
        System.out.println("LSAs in Area " + areaId + ":");
        lsdb.printDatabase();
    }

    public LSDB getLSDB() {
        return lsdb;
    }

    public void ageLSAs() {
        lsdb.ageOutOldLSAs();
    }

    public void recomputeRoutes() {
        WeightedGraph graph = buildGraph();

        for (Router r : routers) {
            r.getRoutingTable().clear();
            System.out.println("Graph contains nodes:");
            for (GraphNode node : graph.getNodes().values()) {
                System.out.println(" - " + node.getRouterId());
            }

            r.recomputeRoutes(graph, r.getRouterId());
        }
    }

    private WeightedGraph buildGraph() {
        WeightedGraph graph = new WeightedGraph();


        System.out.println("LSAs in LSDB: " + lsdb.getAllRouterLSAs().size());
        for (RouterLSA lsa : lsdb.getAllRouterLSAs()) {
            String routerId = lsa.getAdvertisingRouterId();
            graph.addNode(routerId);
            System.out.println(" - Added node: " + routerId);

            for (LinkDescription link : lsa.getLinks()) {
                graph.addEdge(routerId, link.getTargetId(), link.getCost());
            }
        }
        System.out.println("Built the graph!");

        return graph;
    }

    public Router getRouterById(String routerId) {
        for (Router r : routers) {
            if (r.getRouterId().equals(routerId)) {
                return r;
            }
        }
        return null;
    }

    public void printAllRoutingTables() {
        Set<String> printed = new HashSet<>();

        for (Router r : routers) {
            if (printed.contains(r.getRouterId())) continue;

            printed.add(r.getRouterId());

            System.out.println("Routing table for " + r.getRouterId() + ":");
            for (RoutingEntry entry : r.getRoutingTable().getAllEntries()) {
                System.out.printf("  Dest: %-10s  Next Hop: %-10s  Iface: %-6s  Cost: %d\n",
                        entry.getPrefix(), entry.getNextHopId(), entry.getOutgoingInterfaceId(), entry.getCost());
            }
            System.out.println();
        }
    }


    public void generateLSAs() {

        switch (type) {
            case STUB:
                break;
            case NSSA:
                break;
            case TOTALLY_STUBBY:
                break;
            case TOTALLY_NSSA:
                break;
            default:
                break;
        }
    }

}
