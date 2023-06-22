package manager.models;

import java.util.List;
import java.awt.image.BufferedImage;

public class SystemListItem {
    private String systemName;
    private String enumName;
    private List<Rom> roms;
    private int romCount;
    private BufferedImage image;

    public SystemListItem(String systemName,String enumName, List<Rom> roms, BufferedImage image) {
        this.systemName = systemName;
        this.enumName = enumName;
        this.roms = roms;
        this.romCount = roms.size();
        this.image = image;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
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

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

}
