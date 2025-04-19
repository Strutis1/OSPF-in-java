package packets;
//could additionally contain error correction info
//could additionally contain protocol identifier(we use only ospf so no need)


import constants.OSPFDefaults;
import constants.PacketType;
import ospf.Router;

public abstract class OSPFPacket {
    protected final byte version = 2;
    protected final short length;
    protected final int checksum; //validate packet
    protected final PacketType type;
    protected final int packetId;
    protected final String sourceIp;
    protected final String destIp;
    protected final int timeToLive; //failsafe for closed circuits.
    // limited hop count basically and if reaches zero then BAD
    protected final byte[] payload;

    protected int senderPort;
    protected String senderIp;



    OSPFPacket(short length, int checksum, PacketType type, int packetId,
               String sourceIp, String destIp, byte[] payload) {
        this.length = length;
        this.checksum = checksum;
        this.type = type;
        this.packetId = packetId;
        this.sourceIp = sourceIp;
        this.destIp = destIp;
        this.payload = payload;
        this.timeToLive = OSPFDefaults.PACKET_TTL;
    }


    public abstract void process(Router router);
    public abstract byte[] serialize ();




    public void setSenderPort(int port) {
        this.senderPort = port;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public void setSenderIp(String ip) {
        this.senderIp = ip;
    }

    public String getSenderIp() {
        return senderIp;
    }


    public String getSourceIp() {
        return sourceIp;
    }

    public String getDestIp() {
        return destIp;
    }


    public int getTimeToLive() {
        return timeToLive;
    }

    public byte[] getPayload() {
        return payload;
    }

    public int getPacketId() {
        return packetId;
    }

    public byte getVersion() {
        return version;
    }

    public short getLength() {
        return length;
    }

    public int getChecksum() {
        return checksum;
    }

    public PacketType getType() {
        return type;
    }
}
