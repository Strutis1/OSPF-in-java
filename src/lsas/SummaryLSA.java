package lsas;

import constants.LSAType;

public class SummaryLSA extends LSA{
    private String destinationPrefix;
    private int cost;

    public SummaryLSA(String advertisingRouterId, String linkStateId, int age, int checksum, String destinationPrefix, int cost) {
        super(advertisingRouterId, LSAType.SUMMARY, linkStateId, age, checksum);
        this.destinationPrefix = destinationPrefix;
        this.cost = cost;
    }


    @Override
    public String toString() {
        return "SummaryLSA: linkStateId=" + linkStateId + ", advertisedBy=" + advertisingRouterId;
    }

}
