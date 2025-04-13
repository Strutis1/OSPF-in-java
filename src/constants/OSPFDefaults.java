package constants;

public class OSPFDefaults {
    public static final int HELLO_INTERVAL = 10;
    public static final int DEAD_INTERVAL = 40;
    public static final int PACKET_TTL = 255; //time to live
    public static final int PACKET_SIZE = 255;
    public static final int DEFAULT_LSA_SEQ_NUM = 0x80000001;
    public static final int MAX_AGE = 3600; //max age for an lsa before it expires 1 hour
}
