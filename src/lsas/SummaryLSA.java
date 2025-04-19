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
        return "SUMMARY_LSA:" + advertisingRouterId + ";" + linkStateId + ";" + age + ";" + checksum + ";" + destinationPrefix + ";" + cost;
    }

    public static SummaryLSA fromString(String s) {
        String[] parts = s.split(";");
        return new SummaryLSA(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), parts[4], Integer.parseInt(parts[5]));
    }

}
