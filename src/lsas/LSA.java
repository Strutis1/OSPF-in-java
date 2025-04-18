package lsas;

import constants.LSAType;
import constants.OSPFDefaults;
import ospf.Router;

public abstract class LSA {
    protected final String advertisingRouterId; //who created lsa
    protected final LSAType type;
    protected final String linkStateId;
    protected int sequenceNumber;
    protected int age; //seconds
    protected int checksum; //used to see if corrupted

    public LSA(String advertisingRouterId, LSAType type, String linkStateId, int age, int checksum) {
        this.advertisingRouterId = advertisingRouterId;
        this.type = type;
        this.linkStateId = linkStateId;
        this.sequenceNumber = OSPFDefaults.DEFAULT_LSA_SEQ_NUM;
        this.age = age;
        this.checksum = checksum;
    }

    public String getLinkStateId() { return linkStateId; }
    public LSAType getType() { return type; }
    public int getAge() { return age; }
    public int getSequenceNumber() { return sequenceNumber; }
    public int getChecksum() { return checksum; }
    public String getAdvertisingRouterId() { return advertisingRouterId; }

    public boolean isNewerThan(LSA other){
        return this.sequenceNumber > other.getSequenceNumber();
    }

    public abstract void prepare(Router owner);

    public void incrementSequenceNumber() {
        this.sequenceNumber++;
    }


    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }
}
