package constants;

public enum PacketType {
    HELLO(1),
    DATABASE_DESCRIPTION(2),
    LINK_STATE_REQUEST(3),
    LINK_STATE_UPDATE(4),
    LINK_STATE_ACK(5);

    private final int value;

    PacketType(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }

    public static PacketType fromValue(int value) {
        for (PacketType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid PacketType value: " + value);
    }
}
