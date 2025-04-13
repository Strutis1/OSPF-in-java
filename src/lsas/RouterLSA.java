package lsas;

import constants.LSAType;
import ospf.LinkDescription;

import java.util.ArrayList;
import java.util.List;

public class RouterLSA extends LSA {
    private List<LinkDescription> links;

    public RouterLSA(String advertisingRouterId, String linkStateId, int age, int checksum) {
        super(advertisingRouterId, LSAType.ROUTER, linkStateId, age, checksum);
        links = new ArrayList<LinkDescription>();
    }


    @Override
    public String toString() {
        return "RouterLSA: linkStateId=" + linkStateId + ", advertisedBy=" + advertisingRouterId;
    }

}
