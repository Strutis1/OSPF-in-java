package lsas;

import constants.LSAType;
import constants.LinkType;
import helpers.LinkDescription;
import ospf.Interface;
import ospf.Neighbor;
import ospf.Router;

import java.util.ArrayList;
import java.util.List;

public class RouterLSA extends LSA {
    private List<LinkDescription> links;

    public RouterLSA(String advertisingRouterId, String linkStateId, int age, int checksum) {
        super(advertisingRouterId, LSAType.ROUTER, linkStateId, age, checksum);
        links = new ArrayList<LinkDescription>();
    }

    public void addLink(LinkDescription link) {
        links.add(link);
    }

    public List<LinkDescription> getLinks() {
        return links;
    }

    @Override
    public void prepare(Router owner) {
        for (Interface iface : owner.getInterfaces()) {
            List<Neighbor> neighbors = iface.getNeighbors();

            if (neighbors.isEmpty()) {
                this.addLink(new LinkDescription(
                        LinkType.STUB,
                        iface.getIpAddress(),
                        iface.getCost()
                ));
            } else {
                for (Neighbor neighbor : neighbors) {
                    this.addLink(new LinkDescription(
                            iface.getLinkType(),
                            neighbor.getNeighborId(),
                            iface.getCost()
                    ));
                }
            }
        }
    }


    @Override
    public String toString() {
        return "ROUTER_LSA:" + advertisingRouterId + ";" + linkStateId + ";" + age + ";" + checksum;
    }

    public static RouterLSA fromString(String s) {
        String[] parts = s.split(";");
        return new RouterLSA(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }


}
