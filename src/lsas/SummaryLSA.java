package lsas;

import constants.LSAType;
import ospf.Interface;
import ospf.Router;

public class SummaryLSA extends LSA{
    private String destinationPrefix;
    private int cost;

    public SummaryLSA(String advertisingRouterId, String linkStateId, int age, int checksum, String destinationPrefix, int cost) {
        super(advertisingRouterId, LSAType.SUMMARY, linkStateId, age, checksum);
        this.destinationPrefix = destinationPrefix;
        this.cost = cost;
    }

    public String getDestinationPrefix() {
        return destinationPrefix;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public void prepare(Router owner) {
        for (Interface iface : owner.getInterfaces()) {
            if (iface.getPrefix().equals(destinationPrefix)) {
                this.setCost(iface.getCost());
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "SummaryLSA [prefix=" + destinationPrefix + ", cost=" + cost +
                ", advertisedBy=" + advertisingRouterId + "]";
    }
}
