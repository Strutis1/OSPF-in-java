package ospf;
//could additionally contain error correction info
//could additionally contain length of the packet
//could additionally contain protocol identifier(we use only ospf so no need)
//could additionally contain priority

public class OSPFPacket {
    private final String sourceIp;
    private final String destIp;
    private final int timeToLive; //failsafe for closed circuits. limited hop count basically and if reaches zero then BAD
    private final byte[] payload; //the data being transferred



    OSPFPacket(String sourceIp, String destIp, byte[] payload) {
        this.sourceIp = sourceIp;
        this.destIp = destIp;
        this.payload = payload;
        this.timeToLive = 255;
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
}
