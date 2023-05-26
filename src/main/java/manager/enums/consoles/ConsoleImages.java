package manager.enums.consoles;

public enum ConsoleImages {
    _3DO("/images/systems/small/3DO.png"),
    ATARI_2600("/images/systems/small/Atari-2600.png"),
    ATARI_5200("/images/systems/small/Atari-5200.png"),
    ATARI_7800("/images/systems/small/Atari-7800.png"),
    ATARI_JAGUAR_CD("/images/systems/small/Atari-Jaguar-CD.png"),
    ATARI_JAGUAR("/images/systems/small/Atari-Jaguar.png"),
    ATARI_LYNX("/images/systems/small/Atari-Lynx.png"),
    DREAMCAST("/images/systems/small/Dreamcast.png"),
    NEC_PC_ENGINE("/images/systems/small/NEC-PC-Engine.png"),
    NEO_GEO_CD("/images/systems/small/Neo-Geo-CD.png"),
    NEO_GEO_POCKET_COLOR("/images/systems/small/Neo-Geo-Pocket-Color.png"),
    NEO_GEO_POCKET("/images/systems/small/Neo-Geo-Pocket.png"),
    NINTENDO_3DS("/images/systems/small/Nintendo-3DS.png"),
    NINTENDO_64("/images/systems/small/Nintendo-64.png"),
    NINTENDO_DS_LITE("/images/systems/small/Nintendo-DS-Lite.png"),
    NINTENDO_DSI("/images/systems/small/Nintendo-DSi.png"),
    NINTENDO_ENTERTAINMENT_SYSTEM("/images/systems/small/Nintendo-Entertainment-System.png"),
    NINTENDO_FAMICOM("/images/systems/small/Nintendo-Famicom.png"),
    NINTENDO_GAME_BOY_ADVANCE("/images/systems/small/Nintendo-Game-Boy-Advance.png"),
    NINTENDO_GAME_BOY_ADVANCE_SP("/images/systems/small/Game-Boy-Advance-SP.png"),
    NINTENDO_GAME_BOY_COLOR("/images/systems/small/Game-Boy-Color.png"),
    NINTENDO_GAME_BOY_ORIGINAL("/images/systems/small/Game-Boy-Original.png"),
    NINTENDO_GAME_BOY_CARTRIDGE_BLACK("/images/systems/small/Nintendo-Game-Boy-Cartridge-Black.png"),
    NINTENDO_GAME_BOY_CARTRIDGE("/images/systems/small/Nintendo-Game-Boy-Cartridge.png"),
    NINTENDO_GAME_BOY_COLOR2("/images/systems/small/Nintendo-Game-Boy-Color2.png"),
    NINTENDO_GAMECUBE("/images/systems/small/Nintendo-GameCube.png"),
    NINTENDO_SUPER_FAMICOM("/images/systems/small/Nintendo-Super-Famicom.png"),
    NINTENDO_SUPER_NINTENDO("/images/systems/small/Nintendo-Super-NES.png"),
    NINTENDO_SWITCH("/images/systems/small/Nintendo-Switch.png"),
    NINTENDO_WII_MINI("/images/systems/small/Nintendo-Wii-Mini.png"),
    NINTENDO_WII_U("/images/systems/small/Nintendo-Wii-U.png"),
    NOKIA_NGAGE("/images/systems/small/Nokia-NGage.png"),
    PLAYSTATION_VITA("/images/systems/small/PlayStation-Vita.png"),
    PS2("/images/systems/small/PS2.png"),
    PS3("/images/systems/small/PS3.png"),
    PSP_2000("/images/systems/small/PSP-2000.png"),
    SEGA_CDX("/images/systems/small/Sega-CDX.png"),
    SEGA_GAME_GEAR("/images/systems/small/Game-Gear.png"),
    SEGA_GENESIS_3("/images/systems/small/Sega-Genesis-3.png"),
    SEGA_GENESIS("/images/systems/small/Sega-Genesis-Mk2.png"),
    SEGA_MASTER_SYSTEM("/images/systems/small/Sega-Master-System.png"),
    SEGA_MEGA_DRIVE("/images/systems/small/Sega-Mega-Drive.png"),
    SEGA_SATURN_JP("/images/systems/small/Sega-Saturn-JP.png"),
    SEGA_SATURN_US("/images/systems/small/Sega-Saturn-US.png"),
    SONY_PLAYSTATION_4("/images/systems/small/Sony-PlayStation-4.png"),
    SONY_PLAYSTATION("/images/systems/small/Sony-PlayStation.png"),
    SONY_PSONE("/images/systems/small/Sony-PSone.png"),
    TAPWAVE_ZODIAC2("/images/systems/small/Tapwave-Zodiac2.png"),
    VIRTUAL_BOY("/images/systems/small/Virtual-Boy.png"),
    XBOX_360("/images/systems/small/Xbox-360.png"),
    XBOX_ONE("/images/systems/small/Xbox-One.png");

    private final String path;

    ConsoleImages(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
