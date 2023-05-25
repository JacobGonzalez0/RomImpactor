package manager.services;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import manager.models.Settings;

public class SettingService {
    
    public static Settings loadSettings() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File("settings.json");
            return mapper.readValue(file, Settings.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    

}
