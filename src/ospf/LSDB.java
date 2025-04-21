package ospf;

import constants.LSAType;
import constants.OSPFDefaults;
import helpers.LSAKey;
import lsas.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//need to add aging
public class LSDB {
    private long timestamp; // time of creation or last update
    private final Map<LSAKey, RouterLSA> routerLSAs = new HashMap<>();
    private final Map<LSAKey, NetworkLSA> networkLSAs = new HashMap<>();
    private final Map<LSAKey, SummaryLSA> summaryLSAs = new HashMap<>();
    private final Map<LSAKey, LSA> database;

    public LSDB() {
        this.database = new HashMap<>();
    }

    public synchronized void addOrUpdateLSA(LSA lsa) {
        LSAKey key = new LSAKey(lsa.getType(), lsa.getLinkStateId(), lsa.getAdvertisingRouterId());
        LSA existing = database.get(key);

        if (existing == null || lsa.isNewerThan(existing)) {
            database.put(key, lsa);

            if (lsa instanceof RouterLSA) {
                routerLSAs.put(key, (RouterLSA) lsa);
            } else if (lsa instanceof NetworkLSA) {
                networkLSAs.put(key, (NetworkLSA) lsa);
            } else if (lsa instanceof SummaryLSA) {
                summaryLSAs.put(key, (SummaryLSA) lsa);
            }
        }
    }


    public synchronized void removeLSA(LSA lsa) {
        LSAKey key = new LSAKey(lsa.getType(), lsa.getLinkStateId(), lsa.getAdvertisingRouterId());
        database.remove(key);
        routerLSAs.remove(key);
        networkLSAs.remove(key);
        summaryLSAs.remove(key);
    }

    public synchronized List<RouterLSA> getAllRouterLSAs() {
        return new ArrayList<>(routerLSAs.values());
    }

    public synchronized List<NetworkLSA> getAllNetworkLSAs() {
        return new ArrayList<>(networkLSAs.values());
    }

    public synchronized List<SummaryLSA> getAllSummaryLSAs() {
        return new ArrayList<>(summaryLSAs.values());
    }


    public void updateTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    public long getAgeMillis() {
        return System.currentTimeMillis() - timestamp;
    }


    public void printDatabase() {
        for (Map.Entry<LSAKey, LSA> entry : database.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }

    public synchronized LSA getLSA(LSAType type, String linkStateId, String advRouterId) {
        return database.get(new LSAKey(type, linkStateId, advRouterId));
    }

    public Map<LSAKey, LSA> getAllLSAs() {
        return database;
    }

    public List<LSAHeader> getHeaders() {
        return database.values().stream()
                .map(LSAHeader::new)
                .toList();
    }


    public boolean containsLSA(LSA lsa) {
        LSAKey key = new LSAKey(lsa.getType(), lsa.getLinkStateId(), lsa.getAdvertisingRouterId());
        return database.containsKey(key);
    }


    public synchronized void ageOutOldLSAs() {
        List<LSAKey> toRemove = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (Map.Entry<LSAKey, LSA> entry : database.entrySet()) {
            if (entry.getValue().getAge() >= OSPFDefaults.MAX_AGE) {
                toRemove.add(entry.getKey());
            }
        }

        for (LSAKey key : toRemove) {
            database.remove(key);
            routerLSAs.remove(key);
            networkLSAs.remove(key);
            summaryLSAs.remove(key);
            System.out.println("LSDB: Removed expired LSA " + key);
        }
    }
}
