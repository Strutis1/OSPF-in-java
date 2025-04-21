package packets;

import constants.PacketType;
import lsas.LSA;
import lsas.LSAHeader;
import ospf.Neighbor;
import ospf.Router;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelloPacket extends OSPFPacket {
    private final String senderId;
    private final List<String> knownNeighbors;
    private final byte priority;



    public HelloPacket(short length, int checksum, int packetId,
                       String sourceIp, String destIp, byte[] payload,
                       String senderId, byte priority, List<String> knownNeighbors) {
        super(length, checksum, PacketType.HELLO, packetId, sourceIp, destIp, payload);
        this.knownNeighbors = knownNeighbors;
        this.priority = priority;
        this.senderId = senderId;
    }

    public static HelloPacket deserialize(String payload) {
        String[] parts = payload.split(";", -1);
        if (parts.length < 3) throw new IllegalArgumentException("Invalid Hello packet");

        String senderId = parts[0];
        byte priority = Byte.parseByte(parts[1]);
        List<String> neighbors = parts[2].isBlank() ? List.of() : Arrays.asList(parts[2].split(","));

        return new HelloPacket((short) 0, 0, 0, "", "", payload.getBytes(), senderId, priority, neighbors);
    }


    public byte getPriority() {
        return priority;
    }

    public String getSenderId() {
        return senderId;
    }

    public List<String> getKnownNeighbors() {
        return knownNeighbors;
    }


    @Override
    public void process(Router router) {
        if (senderId.equals(router.getRouterId())) return;

        Neighbor neighbor = router.getNeighbors().get(senderId);
        if (neighbor == null) {
            neighbor = new Neighbor(senderId, priority);
            router.getNeighbors().put(senderId, neighbor);
            neighbor.setAddress(getSourceIp(), getSenderPort());
            System.out.println("[" + router.getRouterId() + "] Discovered new neighbor: " + senderId);
            router.sendHelloTo(senderId);
        } else {
            neighbor.setPriority(priority);
            System.out.println("[" + router.getRouterId() + "] Updated priority for neighbor " + senderId);
        }

        neighbor.updateHelloTimestamp();
        if (!neighbor.getState().equals(constants.NeighborState.TWO_WAY)) {
            if (knownNeighbors.contains(router.getRouterId())) {
                neighbor.setState(constants.NeighborState.TWO_WAY);
                System.out.println("[" + router.getRouterId() + "] TWO_WAY with " + senderId);

                router.generateRouterLSA();

                List<LSAHeader> headers = new ArrayList<>();
                for (LSA lsa : router.getLsdb().getAllLSAs().values()) {
                    headers.add(new LSAHeader(lsa));
                }

                DBDescPacket dbDesc = new DBDescPacket(
                        (short) 0, 0, 0,
                        router.getRouterId(), senderId,
                        null, headers
                );

                byte[] data = ("DATABASE_DESCRIPTION;" + new String(dbDesc.serialize())).getBytes();

                try {
                    DatagramPacket packet = new DatagramPacket(
                            data, data.length,
                            InetAddress.getByName(getSourceIp()),
                            getSenderPort()
                    );
                    router.getSocket().send(packet);
                    System.out.println("[" + router.getRouterId() + "] Sent DBDesc to " + senderId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                neighbor.setState(constants.NeighborState.INIT);
                System.out.println("[" + router.getRouterId() + "] INIT with " + senderId);
            }
        }
    }

    @Override
    public byte[] serialize() {
        String payloadStr = senderId + ";" + priority + ";" + String.join(",", knownNeighbors);
        return payloadStr.getBytes();
    }
}
