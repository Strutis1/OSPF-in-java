package packets;

import constants.PacketType;
import lsas.LSAHeader;
import ospf.Router;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class LSAckPacket extends OSPFPacket{
    private final List<LSAHeader> ackHeaders;

    public LSAckPacket(short length, int checksum, int packetId, String sourceIp, String destIp, byte[] payload, List<LSAHeader> ackHeaders) {
        super(length, checksum, PacketType.LINK_STATE_ACK, packetId, sourceIp, destIp, payload);
        this.ackHeaders = ackHeaders;
    }

    public List<LSAHeader> getAckHeaders() {
        return ackHeaders;
    }

    @Override
    public void process(Router router) {
        System.out.println("[" + router.getRouterId() + "] Received LSAck for:");
        for (LSAHeader header : ackHeaders) {
            System.out.println(" -> " + header);
        }
    }

    @Override
    public byte[] serialize() {
        List<String> headerStrings = new ArrayList<>();
        for (LSAHeader header : ackHeaders) {
            headerStrings.add(header.toString());
        }
        return String.join(",", headerStrings).getBytes();
    }

    public static LSAckPacket deserialize(String payload) {
        List<LSAHeader> headers = new ArrayList<>();
        if (!payload.isBlank()) {
            for (String s : payload.split(",")) {
                LSAHeader header = LSAHeader.fromString(s);
                if (header != null) {
                    headers.add(header);
                }
            }
        }
        return new LSAckPacket((short) 0, 0, 0, "", "", payload.getBytes(), headers);
    }

    public static void sendAck(Router router, String destIp, int destPort, List<LSAHeader> headers) {
        try {
            LSAckPacket ack = new LSAckPacket(
                    (short) 0, 0, 0,
                    router.getRouterId(), destIp,
                    null, headers
            );
            byte[] data = ("LINK_STATE_ACK;" + new String(ack.serialize())).getBytes();
            DatagramPacket dp = new DatagramPacket(
                    data, data.length, InetAddress.getByName(destIp), destPort
            );
            router.getSocket().send(dp);
            System.out.println("[" + router.getRouterId() + "] Sent LSAck to " + destIp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
