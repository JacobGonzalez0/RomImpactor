package manager.services;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}
