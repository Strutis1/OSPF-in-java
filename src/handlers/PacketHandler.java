package handlers;

import constants.NeighborState;
import lsas.LSA;
import ospf.Neighbor;
import ospf.Router;
import packets.LSUpdatePacket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.List;

public class PacketHandler {



    public void floodLSA(Router router, LSA lsa) {
        for (Neighbor neighbor : router.getNeighbors().values()) {
            if (neighbor.getState() != NeighborState.TWO_WAY) continue;

            LSUpdatePacket lsu = new LSUpdatePacket(
                    (short) 0,
                    0,
                    0,
                    router.getRouterId(),
                    neighbor.getNeighborId(),
                    null,
                    List.of(lsa)
            );

            byte[] data = ("LINK_STATE_UPDATE;" + new String(lsu.serialize())).getBytes();

            try {
                DatagramPacket packet = new DatagramPacket(
                        data,
                        data.length,
                        InetAddress.getByName(neighbor.getIpAddress()),
                        neighbor.getPort()
                );
                router.getSocket().send(packet);

                System.out.println("[" + router.getRouterId() + "] Sent LSU to " + neighbor.getNeighborId());
            } catch (Exception e) {
                System.out.println("[" + router.getRouterId() + "] Failed to send LSU to " + neighbor.getNeighborId());
                e.printStackTrace();
            }
        }
    }
}
