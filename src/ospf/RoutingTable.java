package ospf;

import helpers.RoutingEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RoutingTable {
    private final Map<String, RoutingEntry> entryMap = new HashMap<String, RoutingEntry>();

    public void addEntry(RoutingEntry entry) {
        entryMap.put(entry.getPrefix(), entry);
    }

    public RoutingEntry getEntry(String destId) {
        return entryMap.get(destId);
    }

    public Collection<RoutingEntry> getAllEntries() {
        return entryMap.values();
    }

    public void clear(){
        entryMap.clear();
    }


}
