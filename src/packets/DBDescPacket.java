package packets;

import constants.PacketType;
import ospf.Router;

public class DBDescPacket extends OSPFPacket{
    public DBDescPacket(short length, int checksum, int packetId, String sourceIp, String destIp, byte[] payload) {
        super(length, checksum, PacketType.DATABASE_DESCRIPTION, packetId, sourceIp, destIp, payload);
    }

    @Override
    public void process(Router router) {

    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
