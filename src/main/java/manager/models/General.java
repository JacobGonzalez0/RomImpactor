package manager.models;

import manager.enums.Language;

public class General {

    private String deviceProfile;
    private int manualScaleSize;
    private boolean manualScale;
    private Language language;
    private String rootDirectory;

    public General(String deviceProfile, int manualScaleSize, boolean manualScale, Language language) {
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
    public Language getLanguage() {
        return language;
    }
    public void setLanguage(Language language) {
        this.language = language;
    }
    public String getRootDirectory() {
        return rootDirectory;
    }
    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

  
}