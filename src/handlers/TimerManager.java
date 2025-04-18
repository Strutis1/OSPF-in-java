package handlers;

public class TimerManager {
    public void start() {
        scheduleHelloPackets();       // e.g., every 10s
        scheduleLSARefresh();         // e.g., every 30m
        scheduleAgeIncrement();       // every 1s
        scheduleSPFCalculation();     // e.g., every few seconds or when LSDB changes
    }

    private void scheduleSPFCalculation() {

    }

    private void scheduleAgeIncrement() {

    }

    private void scheduleLSARefresh() {

    }

    private void scheduleHelloPackets() {

    }
}