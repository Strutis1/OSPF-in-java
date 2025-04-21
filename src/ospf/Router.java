package ospf;

import algorithms.Dijkstra;
import constants.LinkType;
import constants.RouterRole;
import handlers.PacketHandler;
import helpers.RoutingEntry;
import helpers.WeightedGraph;
import lsas.*;
import packets.*;

import java.net.*;
import java.io.*;

import java.util.*;

public class Router {
    //fucking hell
    private PacketHandler packetHandler;
    private final String routerId;
    private final List<String> connectedPrefixes;
    private final List<Interface> interfaces;
    private final Map<String, Neighbor> neighbors;
    private final RoutingTable routingTable;
    private final Set<String> areas;    // Areas this router participates in

    private DatagramSocket socket;
    private int port;
    private Thread listenerThread;

    public Router(String routerId) {
        //add packethandler init
        this.routerId = routerId;
        this.connectedPrefixes = new ArrayList<>();
        this.interfaces = new ArrayList<>();
        this.neighbors = new HashMap<>();
        this.routingTable = new RoutingTable();
        this.areas = new HashSet<>();
    }

    public DatagramSocket getSocket(){
        return socket;
    }

    public void initializeSocket(int port) throws SocketException {
        this.port = port;
        this.socket = new DatagramSocket(port);

        listenerThread = new Thread(() -> {
            byte[] buffer = new byte[1024];

            while (true) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());
                    handlePacket(msg, packet.getAddress(), packet.getPort());
                } catch (IOException e) {
                    System.out.println("[" + routerId + "] Error receiving packet: " + e.getMessage());
                }
            }
        });
        listenerThread.start();
    }

    void assignArea(String areaId) {
        this.areas.add(areaId);
    }

    public String getRouterId() {
        return routerId;
    }

    public void floodLSA(LSA lsa) {
        packetHandler.floodLSA(this, lsa);
    }

    public List<String> getConnectedPrefixes() {
        return connectedPrefixes;
    }

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public synchronized Map<String, Neighbor> getNeighbors() {
        return neighbors;
    }


    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public Set<String> getAreas() {
        return areas;
    }

    public boolean isBackbone() {
        return areas.contains("0");
    }

    public void addPrefix(String prefix) {
        connectedPrefixes.add(prefix);
    }

    public synchronized void addInterface(Interface intface) {
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

    private void handlePacket(String msg, InetAddress address, int port) {
        try {
            String[] parts = msg.split(";", 2);
            if (parts.length < 2) {
                System.out.println("[" + routerId + "] Invalid packet: " + msg);
                return;
            }

            String type = parts[0];
            String payload = parts[1];

            OSPFPacket packet = null;

            switch (type) {
                case "HELLO":
                    HelloPacket hello = HelloPacket.deserialize(payload);
                    hello.setSenderPort(port);
                    hello.setSenderIp(address.getHostAddress());
                    packet = hello;
                    break;
                case "DATABASE_DESCRIPTION":
                    DBDescPacket dbd = DBDescPacket.deserialize(payload);
                    dbd.setSenderPort(port);
                    dbd.setSenderIp(address.getHostAddress());
                    packet = dbd;
                    break;
                case "LINK_STATE_REQUEST":
                    LSRequestPacket lsr = LSRequestPacket.deserialize(payload);
                    lsr.setSenderPort(port);
                    lsr.setSenderIp(address.getHostAddress());
                    packet = lsr;
                    break;
                case "LINK_STATE_UPDATE":
                    LSUpdatePacket lsu = LSUpdatePacket.deserialize(payload);
                    lsu.setSenderPort(port);
                    lsu.setSenderIp(address.getHostAddress());
                    packet = lsu;
                    break;
                case "LINK_STATE_ACK":
                    LSAckPacket lack = LSAckPacket.deserialize(payload);
                    lack.setSenderPort(port);
                    lack.setSenderIp(address.getHostAddress());
                    packet = lack;
                    break;
                case "ROUTER_LSA":
                    // TODO: implement later
                    break;
                case "SUMMARY_LSA":
                    // TODO: implement later
                    break;
                case "NETWORK_LSA":
                    // TODO: implement later
                    break;
                default:
                    System.out.println("[" + routerId + "] Unknown packet type: " + type);
            }
            if (packet != null) {
                packet.process(this);
            }

        } catch (Exception e) {
            System.out.println("[" + routerId + "] Error handling packet: " + msg);
            e.printStackTrace();
        }
    }


    public void sendHello(Map<String, Integer> neighborAddresses) {
        System.out.println("[" + routerId + "] Sending Hello packets...");
        List<String> knownNeighborIds = new ArrayList<>(neighbors.keySet());
        HelloPacket hello = new HelloPacket(
                (short) 0, 0, 0, "localhost", "localhost",
                null, routerId, (byte) 1, knownNeighborIds
        );

        byte[] data = ("HELLO;" + new String(hello.serialize())).getBytes();

        for (Map.Entry<String, Integer> entry : neighborAddresses.entrySet()) {
            try {
                if (entry.getKey().equals(routerId)) continue;
                String targetIp = "localhost";
                int targetPort = entry.getValue();

                DatagramPacket packet = new DatagramPacket(
                        data, data.length, InetAddress.getByName(targetIp), targetPort
                );

                socket.send(packet);
                System.out.println("[" + routerId + "] Sent Hello to " + entry.getKey() + " on port " + targetPort);
            } catch (IOException e) {
                System.out.println("[" + routerId + "] Error sending Hello to " + entry.getKey());
            }
        }
    }



    public void originateLSA(LSA lsa) {
        lsa.prepare(this);

        for (Interface iface : interfaces) {
            Area area = iface.getArea();
            if (area != null) {
                area.installLSA(lsa);
            }
        }

        if (packetHandler != null) {
            packetHandler.floodLSA(this, lsa);
        }

        System.out.println("[" + routerId + "] Generated and flooded " + lsa.getType() + " LSA");
    }

    public void generateRouterLSA() {
        System.out.println("[" + routerId + "] Generating Router LSA...");
        RouterLSA routerLSA = new RouterLSA(routerId, routerId, 0, 0);

        for (Interface iface : interfaces) {
            Area area = iface.getArea();
            if (area != null) {
                area.installLSA(routerLSA);
            }
        }

        originateLSA(routerLSA);
    }

    public void generateSummaryLSA() {
        System.out.println("[" + routerId + "] Generating Summary LSA...");
        if (!getRoles().contains(RouterRole.AREA_BORDER)) return;

        for (String prefix : connectedPrefixes) {
            SummaryLSA lsa = new SummaryLSA(routerId, prefix, 0, 0, prefix, 0);
            originateLSA(lsa);
        }
    }

    public void generateNetworkLSA() {
        System.out.println("[" + routerId + "] Generating Network LSA...");
        for (Interface iface : interfaces) {
            if (iface.getLinkType() == LinkType.TRANSIT && iface.isDR(routerId)) {
                NetworkLSA lsa = new NetworkLSA(routerId, iface.getIpAddress(), 0, 0);
                originateLSA(lsa);
            }
        }
    }


    private String getInterfaceTo(String nextHopId) {
        for (Interface iface : interfaces) {
            if (iface.getConnectedRouterId().equals(nextHopId)) {
                return iface.getInterfaceId();
            }
        }
        return null;
    }

    public Collection<LSA> getAllKnownLSAs() {
        Set<LSA> all = new HashSet<>();
        for (Interface iface : interfaces) {
            Area area = iface.getArea();
            if (area != null) {
                all.addAll(area.getLSDB().getAllLSAs().values());
            }
        }
        return all;
    }


    public void recomputeRoutes(WeightedGraph graph, String localRouterId) {
        Map<String, String> previousHop = new HashMap<>();
        Map<String, Integer> distances = Dijkstra.computeShortestPaths(graph, localRouterId, previousHop);

        routingTable.clear();

        for (String destId : distances.keySet()) {
            if (destId.equals(localRouterId)) continue;

            int cost = distances.get(destId);

            String nextHop = Dijkstra.getNextHop(localRouterId, destId, previousHop);
            String outInterface = getInterfaceTo(nextHop);
            RoutingEntry entry = new RoutingEntry(destId, nextHop, outInterface, cost);
            routingTable.addEntry(entry);
        }
    }

    public void sendHelloTo(String neighborId) {
        Neighbor neighbor = neighbors.get(neighborId);
        if (neighbor == null) return;

        List<String> knownNeighborIds = new ArrayList<>(neighbors.keySet());
        HelloPacket hello = new HelloPacket(
                (short) 0, 0, 0, "localhost", "localhost",
                null, routerId, (byte) 1, knownNeighborIds
        );

        byte[] data = ("HELLO;" + new String(hello.serialize())).getBytes();

        try {
            DatagramPacket packet = new DatagramPacket(
                    data, data.length,
                    InetAddress.getByName(neighbor.getIpAddress()),
                    neighbor.getPort()
            );
            socket.send(packet);
            System.out.println("[" + routerId + "] Re-sent Hello to " + neighborId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
