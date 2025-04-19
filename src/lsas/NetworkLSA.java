package lsas;

import constants.LSAType;
import constants.LinkType;
import ospf.Interface;
import ospf.Neighbor;
import ospf.Router;

import java.util.ArrayList;
import java.util.List;

public class NetworkLSA extends LSA{
    private List<String> attachedRouters;

    public NetworkLSA(String advertisingRouterId, String linkStateId, int age, int checksum) {
        super(advertisingRouterId, LSAType.NETWORK, linkStateId, age, checksum);
        attachedRouters = new ArrayList<String>();
    }

    @Override
    public void prepare(Router owner) {
        for (Interface iface : owner.getInterfaces()) {
            if (iface.getLinkType() == LinkType.TRANSIT && iface.isDR(owner.getRouterId())) {
                for (Neighbor neighbor : iface.getNeighbors()) {
                    this.addAttachedRouter(neighbor.getNeighborId());
                }
                this.addAttachedRouter(owner.getRouterId());
            }
        }
    }

    @Override
    public String toString() {
        return "NETWORK_LSA:" + advertisingRouterId + ";" + linkStateId + ";" + age + ";" + checksum;
    }

    public static NetworkLSA fromString(String s) {
        String[] parts = s.split(";");
        return new NetworkLSA(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }




    public void addAttachedRouter(String neighborId) {
        attachedRouters.add(neighborId);
    }
}
