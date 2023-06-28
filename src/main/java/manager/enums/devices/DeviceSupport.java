package manager.enums.devices;

public enum DeviceSupport {
    FUNKEY_S("Funkey S"),
    POWKIDDY_V90("PowKiddy v90");

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

    public static DeviceSupport getByEnumName(String enumName) {
        try {
            return DeviceSupport.valueOf(enumName);
        } catch (IllegalArgumentException e) {
            System.out.println("No device found with enum name: " + enumName);
            return null; // return null if no matching device is found
        }
    }
}
