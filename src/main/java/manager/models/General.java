package manager.models;

public class General {

    private String deviceProfile;
    private int manualScaleSize;
    private boolean manualScale;
    private String language;

    public General(String deviceProfile, int manualScaleSize, boolean manualScale, String language) {
        this.deviceProfile = deviceProfile;
        this.manualScaleSize = manualScaleSize;
        this.manualScale = manualScale;
        this.language = language;
    }

    public General() {
    }
    
    public String getDeviceProfile() {
        return deviceProfile;
    }
    public void setDeviceProfile(String deviceProfile) {
        this.deviceProfile = deviceProfile;
    }
    public int getManualScaleSize() {
        return manualScaleSize;
    }
    public void setManualScaleSize(int manualScaleSize) {
        this.manualScaleSize = manualScaleSize;
    }
    public boolean isManualScale() {
        return manualScale;
    }
    public void setManualScale(boolean manualScale) {
        this.manualScale = manualScale;
    }
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

  
}