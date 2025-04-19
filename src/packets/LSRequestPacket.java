package packets;

import constants.PacketType;
import lsas.LSA;
import lsas.LSAHeader;
import ospf.LSDB;
import ospf.Router;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class LSRequestPacket extends OSPFPacket{
    private final List<LSAHeader> requestedLSAs;
    public LSRequestPacket(short length, int checksum, int packetId, String sourceIp, String destIp, byte[] payload, List<LSAHeader> requestedLSAs) {
        super(length, checksum, PacketType.LINK_STATE_REQUEST, packetId, sourceIp, destIp, payload);
        this.requestedLSAs = requestedLSAs;
    }

    public List<LSAHeader> getRequestedLSAs() {
        return requestedLSAs;
    }

    @Override
    public void process(Router router) {
        System.out.println("[" + router.getRouterId() + "] Received LSA REQUEST:");
        List<LSA> toSend = new ArrayList<>();

        for (LSAHeader header : requestedLSAs) {
            for (LSA lsa : router.getLsdb().getAllLSAs().values()) {
                if (header.matches(lsa)) {
                    toSend.add(lsa);
                    break;
                }
            }
        }

        if (!toSend.isEmpty()) {
            LSUpdatePacket update = new LSUpdatePacket(
                    (short) 0, 0, 0, router.getRouterId(), getSenderIp(), null, toSend
            );

            byte[] data = ("LINK_STATE_UPDATE;" + new String(update.serialize())).getBytes();

            try {
                DatagramPacket dp = new DatagramPacket(
                        data, data.length,
                        InetAddress.getByName(getSenderIp()), getSenderPort()
                );
                router.getSocket().send(dp);
                System.out.println("[" + router.getRouterId() + "] Sent LSUpdate to " + getSenderIp());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public byte[] serialize() {
        List<String> headerStrings = new ArrayList<>();
        for (LSAHeader header : requestedLSAs) {
            headerStrings.add(header.toString());
        }
        return String.join(",", headerStrings).getBytes();
    }

    public static LSRequestPacket deserialize(String payload) {
        List<LSAHeader> headers = new ArrayList<>();
        if (!payload.isBlank()) {
            for (String s : payload.split(",")) {
                LSAHeader header = LSAHeader.fromString(s);
                if (header != null) {
                    headers.add(header);
                }
            }
        }

        return new LSRequestPacket((short) 0, 0, 0, "", "", payload.getBytes(), headers);
    }
}
