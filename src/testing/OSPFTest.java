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
        Router r3 = new Router("3.3.3.3"); // Transit router

        LSDB sharedDb = new LSDB();
        Area area0_r1 = new Area("0", AreaType.STUB, sharedDb);
        Area area0_r2 = new Area("0", AreaType.STUB, sharedDb);
        Area area0_r3 = new Area("0", AreaType.STUB, sharedDb);

        Interface i1_r1 = new Interface(r1, LinkType.POINT_TO_POINT, "i1", "10.0.0.1", "10.0.0.0/30", "0", 10);
        Interface i1_r2 = new Interface(r2, LinkType.POINT_TO_POINT, "i1", "10.0.0.2", "10.0.0.0/30", "0", 10);

        Interface i2_r2 = new Interface(r2, LinkType.TRANSIT, "i2", "10.0.1.1", "10.0.1.0/24", "0", 5);
        Interface i1_r3 = new Interface(r3, LinkType.TRANSIT, "i1", "10.0.1.2", "10.0.1.0/24", "0", 5);

        area0_r1.addInterface(i1_r1);
        area0_r2.addInterface(i1_r2);
        area0_r2.addInterface(i2_r2);
        area0_r3.addInterface(i1_r3);

        r1.addInterface(i1_r1);
        r2.addInterface(i1_r2);
        r2.addInterface(i2_r2);
        r3.addInterface(i1_r3);

        area0_r1.getRouters().add(r1);
        area0_r2.getRouters().add(r2);
        area0_r3.getRouters().add(r3);

        i1_r1.connectTo(i1_r2, 1);
        i1_r2.connectTo(i1_r1, 1);
        i2_r2.connectTo(i1_r3, 1);
        i1_r3.connectTo(i2_r2, 1);

        i2_r2.electDR();
        i1_r3.electDR();

        r1.initializeSocket(10001);
        r2.initializeSocket(10002);
        r3.initializeSocket(10003);

        Map<String, Integer> ports = new HashMap<>();
        ports.put("2.2.2.2", 10001);
        ports.put("3.3.3.3", 10002);
        ports.put("1.1.1.1", 10003);

        r1.sendHello(ports);
        r2.sendHello(ports);
        r3.sendHello(ports);

        Thread.sleep(5000);


        r1.generateRouterLSA();
        r2.generateRouterLSA();
        r3.generateRouterLSA();

        Thread.sleep(5000);

        area0_r1.recomputeRoutes();
        area0_r2.recomputeRoutes();
        area0_r3.recomputeRoutes();

        Thread.sleep(5000);

        System.out.println("ROUTING TABLES: ");
        area0_r1.printAllRoutingTables();
        area0_r2.printAllRoutingTables();
        area0_r3.printAllRoutingTables();
    }
}
