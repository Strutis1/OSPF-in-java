package ospf;

import constants.LinkType;
import constants.NeighborState;
import constants.OSPFDefaults;
import helpers.Candidate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Interface {
    private final Router owner;
    private final LinkType type;

    private final String interfaceId;
    private final String ipAddress;
    private final String prefix;
    private final String areaId;
    private final int cost;
    private final int helloInterval;//in Seconds
    private final int deadInterval;//in Seconds
    private final List<Neighbor> neighbors;

    private String drId = null;
    private String bdrId = null;


    public Interface(Router owner, LinkType type, String interfaceId, String ipAddress, String prefix, String areaId, int cost) {
        this.owner = owner;
        this.type = type;
        this.interfaceId = interfaceId;
        this.ipAddress = ipAddress;
        this.prefix = prefix;
        this.areaId = areaId;
        this.cost = cost;
        this.helloInterval = OSPFDefaults.HELLO_INTERVAL;
        this.deadInterval = OSPFDefaults.DEAD_INTERVAL;
        this.neighbors = new ArrayList<>();
    }


    public void connectTo(Interface other, int priority) {
        Neighbor neighbor = new Neighbor(other.owner.getRouterId(), priority);
        this.neighbors.add(neighbor);
        System.out.println("[" + this.interfaceId + "] Connected to neighbor " + other.owner.getRouterId());
    }

    public Router getOwner() {
        return owner;
    }

    public void electDR() {
        if (type != LinkType.TRANSIT) return; // skip if not broadcast
        List<Candidate> candidates = new ArrayList<>();

        int selfPriority = 1;
        candidates.add(new Candidate(owner.getRouterId(), selfPriority));

        for (Neighbor neighbor : neighbors) {
            if (neighbor.getPriority() > 0) {
                candidates.add(new Candidate(neighbor.getNeighborId(), neighbor.getPriority()));
            }
        }

        candidates.sort((a, b) -> {
            if (b.priority != a.priority)
                return Integer.compare(b.priority, a.priority);
            return b.routerId.compareTo(a.routerId);
        });

        drId = candidates.isEmpty() ? null : candidates.get(0).routerId;
        bdrId = candidates.size() > 1 ? candidates.get(1).routerId : null;

        System.out.println("[" + interfaceId + "] DR elected: " + drId + ", BDR elected: " + bdrId);
    }

    public boolean isDR(String routerId) {
        return routerId.equals(drId);
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getCost() {
        return cost;
    }

    public LinkType getLinkType() {
        return type;
    }

    public String getAreaId() {
        return areaId;
    }

    public List<Neighbor> getNeighbors() {
        return neighbors;
    }

    public void sendHelloBasedOnLinkType(Map<String, Integer> neighborPorts) {
        if (type == LinkType.POINT_TO_POINT) {
            owner.sendHello(neighborPorts);
        } else if (type == LinkType.TRANSIT) {
            broadcastHello(neighborPorts);
        }
    }

    public void broadcastHello(Map<String, Integer> neighborPorts) {
        System.out.println("[" + interfaceId + "] Broadcasting Hello on IP " + ipAddress);

        List<String> knownNeighborIds = new ArrayList<>();
        for (Neighbor neighbor : neighbors) {
            knownNeighborIds.add(neighbor.getNeighborId());
            neighbor.setState(NeighborState.INIT);
            neighbor.updateHelloTimestamp();
            System.out.println(" -> INIT with neighbor " + neighbor.getNeighborId());
        }

        String payload = owner.getRouterId() + ";1;" + String.join(",", knownNeighborIds);
        String msg = "HELLO;" + payload;

        for (Map.Entry<String, Integer> entry : neighborPorts.entrySet()) {
            String targetId = entry.getKey();
            int targetPort = entry.getValue();

            try {
                byte[] data = msg.getBytes();
                DatagramPacket packet = new DatagramPacket(
                        data, data.length,
                        InetAddress.getByName("localhost"), targetPort
                );
                owner.getSocket().send(packet);
                System.out.println("[" + interfaceId + "] Sent Hello to " + targetId + " on port " + targetPort);
            } catch (IOException e) {
                System.out.println("[" + interfaceId + "] Error sending Hello to " + targetId);
            }
        }

        // Elect DR after broadcast
        electDR();
    }

}
