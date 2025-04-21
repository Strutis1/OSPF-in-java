package testing;

import constants.AreaType;
import constants.LinkType;
import helpers.RoutingEntry;
import ospf.Area;
import ospf.Interface;
import ospf.LSDB;
import ospf.Router;
import lsas.RouterLSA;

import java.util.HashMap;
import java.util.Map;

public class OSPFTest {
    public static void main(String[] args) throws Exception {
        Router r1 = new Router("1.1.1.1");
        Router r2 = new Router("2.2.2.2");
        Router r3 = new Router("3.3.3.3");

        LSDB lsdb0 = new LSDB(); // Area 0
        LSDB lsdb1 = new LSDB(); // Area 1

        Area area0 = new Area("0", AreaType.STUB, lsdb0);
        Area area1 = new Area("1", AreaType.STUB, lsdb1);

        Interface i1 = new Interface(r1, LinkType.POINT_TO_POINT, "i1", "10.0.0.1", "10.0.0.0/30", "0", 10);
        Interface i2 = new Interface(r2, LinkType.POINT_TO_POINT, "i1", "10.0.0.2", "10.0.0.0/30", "0", 10);

        Interface i3 = new Interface(r2, LinkType.POINT_TO_POINT, "i2", "10.0.1.1", "10.0.1.0/30", "1", 10);
        Interface i4 = new Interface(r3, LinkType.POINT_TO_POINT, "i1", "10.0.1.2", "10.0.1.0/30", "1", 10);

        area0.addInterface(i1);
        area0.addInterface(i2);
        area1.addInterface(i3);
        area1.addInterface(i4);

        area0.getRouters().add(r1);
        area0.getRouters().add(r2);
        area1.getRouters().add(r2);
        area1.getRouters().add(r3);

        r1.addInterface(i1);
        r2.addInterface(i2);
        r2.addInterface(i3);
        r3.addInterface(i4);

        i1.connectTo(i2, 1);
        i2.connectTo(i1, 1);

        i3.connectTo(i4, 1);
        i4.connectTo(i3, 1);

        r1.initializeSocket(10001);
        r2.initializeSocket(10002);
        r3.initializeSocket(10003);

        Map<String, Integer> ports = new HashMap<>();
        ports.put("1.1.1.1", 10001);
        ports.put("2.2.2.2", 10002);
        ports.put("3.3.3.3", 10003);

        r1.sendHello(ports);
        r2.sendHello(ports);
        r3.sendHello(ports);

        Thread.sleep(5000);

        r1.generateRouterLSA();
        r2.generateRouterLSA();
        r3.generateRouterLSA();

        Thread.sleep(5000);
        area0.recomputeRoutes();
        area1.recomputeRoutes();

        System.out.println("=== ROUTING TABLES ===");
        area0.printAllRoutingTables();
        area1.printAllRoutingTables();
    }

}
