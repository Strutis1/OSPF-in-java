package helpers;

public class PortAllocator {
    private static int currentPort = 5000;

    public static int getNextPort() {
        return currentPort++;
    }
}