package manager.services;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import manager.enums.Language;
import manager.enums.devices.DeviceSupport;
import manager.models.ApiSettings;
import manager.models.General;
import manager.models.Settings;

public class SettingsService {
    private static final String SETTINGS_FILE = "settings.json";
    
    public static Settings loadSettings() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(SETTINGS_FILE);
            if (file.exists()) {
                return mapper.readValue(file, Settings.class);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void writeSettings(Settings settings) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(SETTINGS_FILE);
            mapper.writeValue(file, settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveSettings(
            String deviceProfile,
            int manualScaleSize,
            boolean manualScale,
            String rootDirectory,
            Language language,
            String steamGridDbKey,
            boolean steamGridDb,
            String igdbClientId,
            String igdbSecret,
            boolean igdb
    ) {
        // Create new settings object
        Settings settings = new Settings();

        General general = new General();
        general.setDeviceProfile(deviceProfile);
        general.setManualScaleSize(manualScaleSize);
        general.setManualScale(manualScale);
        general.setRootDirectory(rootDirectory);
        general.setLanguage(language);
        settings.setGeneral(general);

        ApiSettings apiSettings = new ApiSettings();
        apiSettings.setSteamGridDbKey(steamGridDbKey);
        apiSettings.setSteamGridDb(steamGridDb);
        apiSettings.setIgdbClientId(igdbClientId);
        apiSettings.setIgdbSecret(igdbSecret);
        apiSettings.setIgdb(igdb);
        settings.setApiSettings(apiSettings);

        // Save settings to the JSON file
        writeSettings(settings);
    }

    public static Settings defaultSettings(){
        // Create new settings with default values
        Settings settings = new Settings();
        General general = new General();
        general.setDeviceProfile(DeviceSupport.FUNKEY_S.toString()); //Default FunkeyS
        general.setManualScaleSize(240);
        general.setManualScale(false);
        general.setLanguage(Language.ENGLISH);
        general.setRootDirectory(DirectoryService.getFirstRemovableDrivePath());
        settings.setGeneral(general);

        ApiSettings apiSettings = new ApiSettings();
        apiSettings.setSteamGridDb(false);
        apiSettings.setSteamGridDbKey("");
        apiSettings.setIgdb(false);
        apiSettings.setIgdbClientId("");
        apiSettings.setIgdbSecret("");
        settings.setApiSettings(apiSettings);

        // Save new settings to the JSON file
        SettingsService.writeSettings(settings);

        settings = SettingsService.loadSettings();
        if(settings == null){
            try {
                throw new Exception("Could not write settings.json");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return settings;
    }

}
