package constants;

public enum LinkType {
    POINT_TO_POINT(1),
    TRANSIT(2),
    STUB(3),
    VIRTUAL(4);

    private final int value;

    LinkType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static LinkType fromValue(int value) {
        for (LinkType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid LinkType value: " + value);
    }
}
