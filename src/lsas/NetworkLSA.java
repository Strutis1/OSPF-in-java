package lsas;

import constants.LSAType;

import java.util.ArrayList;
import java.util.List;

public class NetworkLSA extends LSA{
    private List<String> attachedRouters;

    public NetworkLSA(String advertisingRouterId, String linkStateId, int age, int checksum) {
        super(advertisingRouterId, LSAType.NETWORK, linkStateId, age, checksum);
        attachedRouters = new ArrayList<String>();
    }

    @Override
    public String toString() {
        return "NetworkLSA: linkStateId=" + linkStateId + ", advertisedBy=" + advertisingRouterId;
    }

}
