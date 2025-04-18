package helpers;

import constants.LSAType;

public class LSAKey {
    private final LSAType type;
    private final String linkStateId;
    private final String advertisingRouterId;

    public LSAKey(LSAType type, String linkStateId, String advertisingRouterId) {
        this.type = type;
        this.linkStateId = linkStateId;
        this.advertisingRouterId = advertisingRouterId;
    }
}
