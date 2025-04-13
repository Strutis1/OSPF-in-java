package packets;

import constants.PacketType;
import ospf.Router;

public class HelloPacket extends OSPFPacket {
    private final byte priority;


    public HelloPacket(short length, int checksum, int packetId,
                       String sourceIp, String destIp, byte[] payload, byte priority) {
        super(length, checksum, PacketType.HELLO, packetId,
        sourceIp, destIp, payload);
        this.priority = priority;
    }

    public byte getPriority() {
        return priority;
    }


    @Override
    public void process(Router router) {

    }

    @Override
    public byte[] serialize() {
        return new byte[0];
    }
}
