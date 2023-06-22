package manager.enums.devices;


import java.util.Arrays;
import java.util.List;

import manager.interfaces.DeviceRomType;

/*
 * This enum is for finding the right folder names for different devices,
 * and the file extentions for them, along with how to find the associated png for cover art
 */
public enum FunkeyDevice implements DeviceRomType {
    NINTENDO_GAME_BOY_COLOR(
            "Game Boy Color",
            Arrays.asList(".gbc"),
            ".*\\.png"
    ),
    NINTENDO_GAME_BOY_ADVANCE(
            "Game Boy Advance",
            Arrays.asList(".gba"),
            ".*\\.png"
    ),
    NINTENDO_GAME_BOY_ORIGINAL(
            "Game Boy",
            Arrays.asList(".gb"),
            ".*\\.png"
    ),
    SEGA_GAME_GEAR(
            "Game Gear",
            Arrays.asList(".gg"),
            ".*\\.png"
    ),
    SEGA_MASTER_SYSTEM(
            "Sega Master System",
            Arrays.asList(".sms"),
            ".*\\.png"
    ),
    SEGA_GENESIS(
            "Sega Genesis",
            Arrays.asList(".gen", ".md", ".smd"),
            ".*\\.png"
    ),
    NINTENDO_ENTERTAINMENT_SYSTEM(
            "NES",
            Arrays.asList(".nes"),
            ".*\\.png"
    ),
    NINTENDO_SUPER_NINTENDO(
            "SNES",
            Arrays.asList(".smc", ".sfc"),
            ".*\\.png"
    ),
    SONY_PLAYSTATION(
            "PS1",
            Arrays.asList(".bin", ".iso"),
            ".*\\.png"
    ),
    NEC_PC_ENGINE(
            "PCE-TurboGrafx",
            Arrays.asList(".pce"),
            ".*\\.png"
    ),
    ATARI_LYNX(
            "Atari Lynx",
            Arrays.asList(".lnx"),
            ".*\\.png"
    ),
    NEO_GEO_POCKET(
            "Neo Geo Pocket",
            Arrays.asList(".ngp"),
            ".*\\.png"
    ),
    WONDERSWAN(
            "WonderSwan",
            Arrays.asList(".ws"),
            ".*\\.png"
    ),
    SCALING(
            "240",
            Arrays.asList(),
            ""
    );

    private final String folderPath;
    private final List<String> fileExtensions;
    private final String imageRegexPattern;

    FunkeyDevice(String deviceName, List<String> fileExtensions, String imageRegexPattern) {
        this.folderPath = deviceName;
        this.fileExtensions = fileExtensions;
        this.imageRegexPattern = imageRegexPattern;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public String getImageRegexPattern() {
        return imageRegexPattern;
    }

    public static FunkeyDevice getDeviceByEnumName(String name) {
        try {
            return FunkeyDevice.valueOf(name);
        } catch (IllegalArgumentException e) {
            System.out.println("No device found with enum name: " + name);
            return null; // return null if no matching device is found
        }
    }

    public static FunkeyDevice getDeviceByFolderPath(String path) {
        for (FunkeyDevice device : FunkeyDevice.values()) {
            if (device.getFolderPath().equals(path)) {
                return device;
            }
        }
        System.out.println("nothing found");
        return null; // return null if no matching device is found
    }

    public static List<String> getFileExtensionsByFolderPath(String path) {
        for (FunkeyDevice device : FunkeyDevice.values()) {
            if (device.getFolderPath().equals(path)) {
                return device.getFileExtensions();
            }
        }
        return null; // return null if no matching device is found
    }
}



