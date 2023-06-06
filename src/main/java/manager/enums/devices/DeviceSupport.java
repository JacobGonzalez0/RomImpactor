package manager.enums.devices;

public enum DeviceSupport {
    FUNKEY_S("Funkey S");

    private final String deviceName;

    DeviceSupport(String deviceName){
        this.deviceName = deviceName;
    }

    public String getConsoleName(){
        return deviceName;
    }
    
    public static DeviceSupport getByName(String deviceName) {
        for (DeviceSupport device : DeviceSupport.values()) {
            if (device.getConsoleName().equals(deviceName)) {
                return device;
            }
        }
        return null;
    }    
}
