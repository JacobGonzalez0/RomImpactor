package manager.enums;

public enum OperatingMode {
    LOCAL_IMAGE(0),
    ONLINE_IMAGE(1),
    LOCAL_PATCH(2),
    ONLINE_PATCH(3);

    private final int value;

    private OperatingMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
