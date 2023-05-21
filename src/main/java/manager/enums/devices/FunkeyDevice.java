package manager.enums.devices;


import java.util.Arrays;
import java.util.List;

import manager.interfaces.DeviceRomType;

/*
 * This enum is for finding the right folder names for different devices,
 * and the file extentions for them, along with how to find the associated png for cover art
 */
public enum FunkeyDevice implements DeviceRomType {
    GAME_BOY_COLOR("Game Boy Color", 
        Arrays.asList(".gbc"), 
        ".*\\.png"),
    GAME_BOY_ADVANCE("Game Boy Advance", 
        Arrays.asList(".gba"),
         ".*\\.png"),
    GAME_BOY("Game Boy", 
        Arrays.asList(".gb"), 
        ".*\\.png"),
    SEGA_GAME_GEAR("Game Gear", 
        Arrays.asList(".gg"),
        ".*\\.png"),
    SEGA_MASTER_SYSTEM("Sega Master System", 
        Arrays.asList(".sms"), 
        ".*\\.png"),
    SEGA_GENESIS("Sega Genesis",
        Arrays.asList(".gen", ".md", ".smd"), 
        ".*\\.png"),
    NINTENDO_ENTERTAINMENT_SYSTEM("NES",
        Arrays.asList(".nes"), 
        ".*\\.png"),
    SUPER_NINTENDO("SNES",
        Arrays.asList(".smc", ".sfc"), 
        ".*\\.png"),
    PLAYSTATION("PS1", 
        Arrays.asList(".bin", ".iso"), 
        ".*\\.png"),
    PC_ENGINE("PCE-TurboGrafx", 
        Arrays.asList(".pce"),
         ".*\\.png"),
    ATARI_LYNX("Atari Lynx", 
        Arrays.asList(".lnx"),
         ".*\\.png"),
    NEO_GEO_POCKET("Neo Geo Pocket", 
        Arrays.asList(".ngp"),
         ".*\\.png"),
    WONDERSWAN("WonderSwan", 
        Arrays.asList(".ws"),
         ".*\\.png"),
    VECTREX("Vectrex", 
        Arrays.asList(), 
        "");

    private final String deviceName;
    private final List<String> fileExtensions;
    private final String imageRegexPattern;

    FunkeyDevice(String deviceName, List<String> fileExtensions, String imageRegexPattern) {
        this.deviceName = deviceName;
        this.fileExtensions = fileExtensions;
        this.imageRegexPattern = imageRegexPattern;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public String getImageRegexPattern() {
        return imageRegexPattern;
    }
}

