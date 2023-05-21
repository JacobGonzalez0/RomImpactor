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
    
}
