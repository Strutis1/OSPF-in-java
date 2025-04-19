package testing;

import constants.LinkType;
import ospf.Interface;
import ospf.Router;
import lsas.RouterLSA;

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

        r1.generateRouterLSA();
        r2.generateRouterLSA();
        r3.generateRouterLSA();

        Map<String, Integer> ports = new HashMap<>();
        ports.put("1.1.1.1", 5001);
        ports.put("2.2.2.2", 5002);
        ports.put("3.3.3.3", 5003);

        System.out.println(" Starting Hello exchange...");
        r1.sendHello(ports);
        r2.sendHello(ports);
        r3.sendHello(ports);

        Thread.sleep(2000);

        System.out.println("DBDesc packets will auto-fire after TWO_WAY from inside HelloPacket.process()");
        System.out.println(" LSRequest + LSUpdate should follow if any LSAs are missing.");

        Thread.sleep(10000);

        System.out.println("✅ Simulation done.");
        System.exit(0);
    }
}
