package lsas;

import constants.LSAType;

import java.util.Objects;

public class LSAHeader {
    private final String advertisingRouter;
    private final String lsaId;
    private final int sequenceNumber;
    private final int checksum;
    private final LSAType lsaType;

    public LSAHeader(String advertisingRouter, String lsaId, int sequenceNumber, int checksum, LSAType lsaType) {
        this.advertisingRouter = advertisingRouter;
        this.lsaId = lsaId;
        this.sequenceNumber = sequenceNumber;
        this.checksum = checksum;
        this.lsaType = lsaType;
    }

    public LSAHeader(LSA lsa) {
        this.advertisingRouter = lsa.getAdvertisingRouterId();
        this.lsaId = lsa.getLinkStateId();
        this.sequenceNumber = lsa.getSequenceNumber();
        this.checksum = lsa.getChecksum();
        this.lsaType = lsa.getType();
    }

    public String getAdvertisingRouter() {
        return advertisingRouter;
    }

    public String getLsaId() {
        return lsaId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getChecksum() {
        return checksum;
    }

    public LSAType getLsaType() {
        return lsaType;
    }

    public boolean matches(LSA lsa) {
        return lsa.getLinkStateId().equals(lsaId)
                && lsa.getAdvertisingRouterId().equals(advertisingRouter)
                && lsa.getType().equals(lsaType);
    }

    @Override
    public String toString() {
        return lsaType + ":" + lsaId + ":" + advertisingRouter + ":" + sequenceNumber + ":" + checksum;
    }

    public static LSAHeader fromString(String s) {
        String[] parts = s.split(":");
        try {
            return new LSAHeader(
                    parts[2],
                    parts[1],
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    LSAType.valueOf(parts[0])
            );
        } catch (Exception e) {
            System.out.println("Failed to parse LSAHeader from string: " + s);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LSAHeader)) return false;
        LSAHeader that = (LSAHeader) o;
        return Objects.equals(lsaId, that.lsaId) &&
                Objects.equals(advertisingRouter, that.advertisingRouter) &&
                Objects.equals(lsaType, that.lsaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lsaId, advertisingRouter, lsaType);
    }
}
