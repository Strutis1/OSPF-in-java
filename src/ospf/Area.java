package ospf;

import constants.AreaType;

import java.util.HashSet;
import java.util.Set;

public class Area {
    private final int areaId;
    private final Set<Interface> assignedInterfaces;
    private final AreaType type;
    private final LSDB lsdb;

    public Area(int areaId, AreaType type, LSDB lsdb) {
        this.areaId = areaId;
        this.assignedInterfaces = new HashSet<>();
        this.type = type;
        this.lsdb = lsdb;
    }

    public void addInterface(Interface iface) {
        assignedInterfaces.add(iface);
    }


    public void generateLSAs() {

        switch (type) {
            case STUB:
                break;
            case NSSA:
                break;
            case TOTALLY_STUBBY:
                break;
            case TOTALLY_NSSA:
                break;
            default:
                break;
        }
    }

}
