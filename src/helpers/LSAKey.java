package helpers;

import constants.LSAType;

import java.util.Objects;

public class LSAKey {
    private final LSAType type;
    private final String linkStateId;
    private final String advertisingRouterId;

    public LSAKey(LSAType type, String linkStateId, String advertisingRouterId) {
        this.type = type;
        this.linkStateId = linkStateId;
        this.advertisingRouterId = advertisingRouterId;
    }

    public String getAdvertisingRouterId() {
        return advertisingRouterId;
    }

    public String getLinkStateId() {
        return linkStateId;
    }

    public LSAType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LSAKey)) return false;
        LSAKey other = (LSAKey) o;
        return Objects.equals(advertisingRouterId, other.advertisingRouterId)
                && Objects.equals(linkStateId, other.linkStateId)
                && type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(advertisingRouterId, linkStateId, type);
    }

    @Override
    public String toString() {
        return type + ":" + linkStateId + " from " + advertisingRouterId;
    }
}
