package packets;

import constants.PacketType;
import lsas.LSA;
import lsas.LSAHeader;
import ospf.Router;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DBDescPacket extends OSPFPacket{
    private final List<LSAHeader> lsaHeaders;

    public DBDescPacket(short length, int checksum, int packetId, String sourceIp, String destIp, byte[] payload, List<LSAHeader> lsaHeaders) {
        super(length, checksum, PacketType.DATABASE_DESCRIPTION, packetId, sourceIp, destIp, payload);
        this.lsaHeaders = lsaHeaders;
    }

    public List<LSAHeader> getLsaHeaders() {
        return lsaHeaders;
    }

    @Override
    public void process(Router router) {
        System.out.println("[" + router.getRouterId() + "] Received DATABASE_DESCRIPTION with headers:");
        for (LSAHeader header : lsaHeaders) {
            System.out.println(" -> " + header);
        }

        List<LSAHeader> missing = new ArrayList<>();

        for (LSAHeader receivedHeader : lsaHeaders) {
            boolean found = false;

            for (LSA lsa : router.getAllKnownLSAs()) {
                if (receivedHeader.matches(lsa) &&
                        receivedHeader.getSequenceNumber() <= lsa.getSequenceNumber()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("[" + router.getRouterId() + "] Sending LSR for missing LSA: " + receivedHeader);
                missing.add(receivedHeader);
            }
        }
        if (!missing.isEmpty()) {
            LSRequestPacket lsr = new LSRequestPacket(
                    (short) 0, 0, 0, "localhost", "localhost", null, missing
            );

            byte[] data = ("LINK_STATE_REQUEST;" + new String(lsr.serialize())).getBytes();
            DatagramPacket packet = null;
            try {
                packet = new DatagramPacket(
                        data, data.length,
                        InetAddress.getByName(getSourceIp()),
                        getSenderPort()
                );
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

            try {
                router.getSocket().send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        }

    @Override
    public byte[] serialize() {
        List<String> headerStrings = new ArrayList<>();
        for (LSAHeader header : lsaHeaders) {
            headerStrings.add(header.toString());
        }
        String payload = String.join(",", headerStrings);
        return payload.getBytes();
    }

    public static DBDescPacket deserialize(String payload) {
        List<LSAHeader> headers = new ArrayList<>();

        if (!payload.isBlank()) {
            String[] parts = payload.split(",");
            for (String s : parts) {
                LSAHeader header = LSAHeader.fromString(s);
                if (header != null) {
                    headers.add(header);
                }
            }
        }

        return new DBDescPacket((short) 0, 0, 0, "", "", payload.getBytes(), headers);
    }
}
