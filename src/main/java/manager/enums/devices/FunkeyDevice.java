package manager.enums.devices;


import java.util.Arrays;
import java.util.List;

import manager.interfaces.DeviceRomType;

/*
 * This enum is for finding the right folder names for different devices,
 * and the file extentions for them, along with how to find the associated png for cover art
 */
import java.util.Arrays;
import java.util.List;

public class FunkeyDevice implements DeviceRomType {
    public static final FunkeyDevice GAME_BOY_COLOR = new FunkeyDevice("Game Boy Color",
            Arrays.asList(".gbc"),
            ".*\\.png");
    public static final FunkeyDevice GAME_BOY_ADVANCE = new FunkeyDevice("Game Boy Advance",
            Arrays.asList(".gba"),
            ".*\\.png");
    public static final FunkeyDevice GAME_BOY = new FunkeyDevice("Game Boy",
            Arrays.asList(".gb"),
            ".*\\.png");
    public static final FunkeyDevice SEGA_GAME_GEAR = new FunkeyDevice("Game Gear",
            Arrays.asList(".gg"),
            ".*\\.png");
    public static final FunkeyDevice SEGA_MASTER_SYSTEM = new FunkeyDevice("Sega Master System",
            Arrays.asList(".sms"),
            ".*\\.png");
    public static final FunkeyDevice SEGA_GENESIS = new FunkeyDevice("Sega Genesis",
            Arrays.asList(".gen", ".md", ".smd"),
            ".*\\.png");
    public static final FunkeyDevice NINTENDO_ENTERTAINMENT_SYSTEM = new FunkeyDevice("NES",
            Arrays.asList(".nes"),
            ".*\\.png");
    public static final FunkeyDevice SUPER_NINTENDO = new FunkeyDevice("SNES",
            Arrays.asList(".smc", ".sfc"),
            ".*\\.png");
    public static final FunkeyDevice PLAYSTATION = new FunkeyDevice("PS1",
            Arrays.asList(".bin", ".iso"),
            ".*\\.png");
    public static final FunkeyDevice PC_ENGINE = new FunkeyDevice("PCE-TurboGrafx",
            Arrays.asList(".pce"),
            ".*\\.png");
    public static final FunkeyDevice ATARI_LYNX = new FunkeyDevice("Atari Lynx",
            Arrays.asList(".lnx"),
            ".*\\.png");
    public static final FunkeyDevice NEO_GEO_POCKET = new FunkeyDevice("Neo Geo Pocket",
            Arrays.asList(".ngp"),
            ".*\\.png");
    public static final FunkeyDevice WONDERSWAN = new FunkeyDevice("WonderSwan",
            Arrays.asList(".ws"),
            ".*\\.png");
    public static final FunkeyDevice VECTREX = new FunkeyDevice("Vectrex",
            Arrays.asList(),
            "");
    public static final FunkeyDevice SCALING = new FunkeyDevice("240",
            Arrays.asList(),
            "");

    private final String deviceName;
    private final List<String> fileExtensions;
    private final String imageRegexPattern;

    private FunkeyDevice(String deviceName, List<String> fileExtensions, String imageRegexPattern) {
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

    public static FunkeyDevice[] values() {
        return new FunkeyDevice[] {
                GAME_BOY_COLOR, GAME_BOY_ADVANCE, GAME_BOY, SEGA_GAME_GEAR, SEGA_MASTER_SYSTEM, SEGA_GENESIS,
                NINTENDO_ENTERTAINMENT_SYSTEM, SUPER_NINTENDO, PLAYSTATION, PC_ENGINE, ATARI_LYNX, NEO_GEO_POCKET,
                WONDERSWAN, VECTREX, SCALING
        };
    }
}


