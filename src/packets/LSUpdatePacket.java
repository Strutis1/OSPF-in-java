package packets;

import constants.PacketType;
import lsas.LSA;
import lsas.LSAHeader;
import ospf.Router;

import java.util.ArrayList;
import java.util.List;

public class LSUpdatePacket extends OSPFPacket{

    private final List<LSA> lsas;
    public LSUpdatePacket(short length, int checksum, int packetId, String sourceIp, String destIp, byte[] payload, List<LSA> lsas) {
        super(length, checksum, PacketType.LINK_STATE_UPDATE, packetId, sourceIp, destIp, payload);
        this.lsas = lsas;
    }

    public List<LSA> getLsas() {
        return lsas;
    }

    @Override
    public void process(Router router) {
        System.out.println("[" + router.getRouterId() + "] Received LSUpdate with LSAs:");
        List<LSAHeader> ackHeaders = new ArrayList<>();
        for (LSA lsa : lsas) {
            router.getLsdb().addOrUpdateLSA(lsa);
            System.out.println(" -> Installed: " + lsa.getLinkStateId());
            ackHeaders.add(new LSAHeader(lsa));
        }
        if (!ackHeaders.isEmpty()) {
            LSAckPacket.sendAck(router, getSenderIp(), getSenderPort(), ackHeaders);
        }
    }

    @Override
    public byte[] serialize() {
        List<String> lines = new ArrayList<>();
        for (LSA lsa : lsas) {
            lines.add(lsa.toString());
        }
        return String.join("#", lines).getBytes();
    }

    public static LSUpdatePacket deserialize(String payload) {
        List<LSA> lsas = new ArrayList<>();
        String[] parts = payload.split("#");
        for (String p : parts) {
            LSA lsa = LSA.fromString(p);
            if (lsa != null) lsas.add(lsa);
        }
        return new LSUpdatePacket((short) 0, 0, 0, "", "", payload.getBytes(), lsas);
    }
}
