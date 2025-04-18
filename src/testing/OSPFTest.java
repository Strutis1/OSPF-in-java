package testing;

import constants.AreaType;
import constants.LinkType;
import ospf.Interface;
import ospf.*;

import java.util.HashMap;
import java.util.Map;

public class OSPFTest {
    public static void main(String[] args) throws Exception {
        Router r1 = new Router("1.1.1.1");
        Router r2 = new Router("2.2.2.2");
        Router r3 = new Router("3.3.3.3");

        r1.initializeSocket(5001);
        r2.initializeSocket(5002);
        r3.initializeSocket(5003);

        Interface i1 = new Interface(r1, LinkType.TRANSIT, "eth0", "10.0.0.1", "10.0.0.0/24", "0", 10);
        Interface i2 = new Interface(r2, LinkType.TRANSIT, "eth0", "10.0.0.2", "10.0.0.0/24", "0", 10);
        Interface i3 = new Interface(r3, LinkType.TRANSIT, "eth0", "10.0.0.3", "10.0.0.0/24", "0", 10);

        r1.addInterface(i1);
        r2.addInterface(i2);
        r3.addInterface(i3);

        i1.connectTo(i2, 1);
        i1.connectTo(i3, 1);
        i2.connectTo(i1, 1);
        i2.connectTo(i3, 1);
        i3.connectTo(i1, 1);
        i3.connectTo(i2, 1);

        Map<String, Integer> allPorts = new HashMap<>();
        allPorts.put("1.1.1.1", 5001);
        allPorts.put("2.2.2.2", 5002);
        allPorts.put("3.3.3.3", 5003);

        Thread.sleep(1000);

        i1.broadcastHello(allPorts);
        i2.broadcastHello(allPorts);
        i3.broadcastHello(allPorts);
    }
}
