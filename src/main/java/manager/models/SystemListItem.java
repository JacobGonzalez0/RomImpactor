package manager.models;

import java.util.List;

public class SystemListItem {
    private String systemName;
    private List<Rom> roms;
    private int romCount;

    public SystemListItem(String systemName, List<Rom> roms) {
        this.systemName = systemName;
        this.roms = roms;
        this.romCount = roms.size();
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public List<Rom> getRoms() {
        return roms;
    }

    public void setRoms(List<Rom> roms) {
        this.roms = roms;
        this.romCount = roms.size();
    }

    public int getRomCount() {
        return romCount;
    }

    public void setRomCount(int romCount) {
        this.romCount = romCount;
    }

}
