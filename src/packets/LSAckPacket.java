package packets;

import constants.PacketType;
import ospf.Router;

public class LSAckPacket extends OSPFPacket{
    public LSAckPacket(short length, int checksum, int packetId, String sourceIp, String destIp, byte[] payload) {
        super(length, checksum, PacketType.LINK_STATE_ACK, packetId, sourceIp, destIp, payload);
    }

    @Override
    public void process(Router router) {

    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
