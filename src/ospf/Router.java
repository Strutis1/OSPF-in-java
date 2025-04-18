package ospf;

import constants.LinkType;
import constants.NeighborState;
import constants.RouterRole;
import handlers.PacketHandler;
import lsas.*;
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
    private final LSDB lsdb;
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
        this.lsdb = new LSDB();
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
                    handlePacket(msg);
                } catch (IOException e) {
                    System.out.println("[" + routerId + "] Error receiving packet: " + e.getMessage());
                }
            }
        });
        listenerThread.start();
    }

    private void handleHelloPacket(String msg) {
        try {
            String[] parts = msg.split(";", -1);

            if (parts.length < 3) {
                System.out.println("[" + routerId + "] Invalid Hello format: " + msg);
                return;
            }

            String senderId = parts[0];
            int priority = Integer.parseInt(parts[1]);

            List<String> theirNeighbors = new ArrayList<>();
            if (parts.length > 2 && !parts[2].isBlank()) {
                theirNeighbors = Arrays.asList(parts[2].split(","));
            }

            System.out.println("[" + routerId + "] Received Hello from " + senderId + " with neighbors: " + theirNeighbors);

            receiveHello(senderId, priority, theirNeighbors);

        } catch (Exception e) {
            System.out.println("[" + routerId + "] Failed to parse Hello: " + msg);
            e.printStackTrace();
        }
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
        return areas.contains("0");
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

    private void handlePacket(String msg) {
        try {
            String[] parts = msg.split(";", 2);
            if (parts.length < 2) {
                System.out.println("[" + routerId + "] Invalid packet: " + msg);
                return;
            }

            String type = parts[0];
            String payload = parts[1];

            switch (type) {
                case "HELLO":
                    handleHelloPacket(payload);
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

        } catch (Exception e) {
            System.out.println("[" + routerId + "] Error handling packet: " + msg);
            e.printStackTrace();
        }
    }


    public void sendHello(Map<String, Integer> neighborAddresses) {
        System.out.println("[" + routerId + "] Sending Hello packets...");
        List<String> knownNeighborIds = new ArrayList<>(neighbors.keySet());
        String payload = routerId + ";1;" + String.join(",", knownNeighborIds);
        String msg = "HELLO;" + payload;

        for (Map.Entry<String, Integer> entry : neighborAddresses.entrySet()) {
            try {
                String targetIp = "localhost";
                int targetPort = entry.getValue();

                byte[] data = msg.getBytes();
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

    public void receiveHello(String neighborId, int priority, List<String> theirKnownNeighbors) {
        Neighbor neighbor = neighbors.get(neighborId);

        if(neighborId.equals(this.routerId)) return;

        if (neighbor == null) {
            neighbor = new Neighbor(neighborId, priority);
            neighbors.put(neighborId, neighbor);
            System.out.println("[" + routerId + "] Discovered new neighbor: " + neighborId);
        } else {
            neighbor.setPriority(priority);
            System.out.println("[" + routerId + "] Updated priority for neighbor " + neighborId);
        }

        neighbor.updateHelloTimestamp();

        if (theirKnownNeighbors.contains(this.routerId)) {
            neighbor.setState(NeighborState.TWO_WAY);
            System.out.println("[" + routerId + "] TWO_WAY with " + neighborId);
        } else {
            neighbor.setState(NeighborState.INIT);
            System.out.println("[" + routerId + "] INIT with " + neighborId);
        }
    }



    public void originateLSA(LSA lsa) {
        lsa.prepare(this);

        lsdb.addOrUpdateLSA(lsa);

        if (packetHandler != null) {
            packetHandler.floodLSA(this, lsa);
        }

        System.out.println("[" + routerId + "] Generated and flooded " + lsa.getType() + " LSA");
    }

    public void generateRouterLSA() {
        System.out.println("[" + routerId + "] Generating Router LSA...");
        RouterLSA routerLSA = new RouterLSA(routerId, routerId, 0, 0);
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
}
