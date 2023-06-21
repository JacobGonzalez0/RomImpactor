package manager.controllers;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import manager.enums.Language;
import manager.enums.devices.DeviceSupport;
import manager.models.ApiSettings;
import manager.models.General;
import manager.models.Settings;
import manager.services.SettingsService;

public class OptionsWindowController {
    @FXML
    private TabPane tabPane;

    @FXML
    private ComboBox<String> deviceProfileComboBox;
    
    @FXML
    private ComboBox<Language> languageComboBox;

    @FXML
    private Label deviceProfileLabel, manualScaleSizeLabel, steamGridDBLabel, igdbLabel;

    @FXML
    private TextField manualScaleSizeTextField;

    @FXML
    private TextField deviceDirectoryTextField,steamGridDBApiKeyTextField, igdbSecretTextField, igdbClientIdTextField;

    @FXML
    private CheckBox steamGridDBEnabledCheckBox, igdbEnabledCheckBox, manualScaleEnableCheckBox;


    // Add your controller logic here
    // ...

    // Optional: You can add initialization code using @FXML initialize method
    @FXML
    private void initialize() {
        
        Settings settings = SettingsService.loadSettings();

        if (settings != null) {
            populateSettingsUI(settings);
        }
        else 
        {
            // Create new settings with default values
            settings = new Settings();
            General general = new General();
            general.setDeviceProfile(DeviceSupport.FUNKEY_S.toString()); //Default FunkeyS
            general.setManualScaleSize(240);
            general.setManualScale(false);
            general.setLanguage(Language.ENGLISH);
            general.setRootDirectory(null);
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
            populateSettingsUI(settings);
        }
        
        
    }

    @FXML
    public void saveSettings() {
        String deviceProfile = deviceProfileComboBox.getValue();
        int manualScaleSize = Integer.parseInt(manualScaleSizeTextField.getText());
        boolean manualScale = manualScaleEnableCheckBox.isSelected();
        String steamGridDbKey = steamGridDBApiKeyTextField.getText();
        boolean steamGridDb = steamGridDBEnabledCheckBox.isSelected();
        String igdbClientId = igdbClientIdTextField.getText();
        String igdbSecret = igdbSecretTextField.getText();
        String rootDirectory = deviceDirectoryTextField.getText();
        Language language = languageComboBox.getSelectionModel().getSelectedItem();
        boolean igdb = igdbEnabledCheckBox.isSelected();

        SettingsService.saveSettings(deviceProfile, manualScaleSize, manualScale, rootDirectory, language,
                steamGridDbKey, steamGridDb, igdbClientId, igdbSecret, igdb);
    }

    private static List<String> getDeviceList() {
        List<String> deviceList = new ArrayList<>();

        for (DeviceSupport device : DeviceSupport.values()) {
            deviceList.add(device.getConsoleName());
        }

        return deviceList;
    }

    public void populateSettingsUI(Settings settings) {
        if (settings != null) {
            // Populate device profile combo box
            General general = settings.getGeneral();
            List<String> deviceProfiles = getDeviceList();
            deviceProfileComboBox.getItems().addAll(deviceProfiles);
            deviceProfileComboBox.setValue(general.getDeviceProfile());

            languageComboBox.getItems().addAll(Language.values());
            languageComboBox.setValue(general.getLanguage());

            // Populate other options fields
            manualScaleSizeTextField.setText(String.valueOf(general.getManualScaleSize()));
            manualScaleEnableCheckBox.setSelected(general.isManualScale());

            ApiSettings apiSettings = settings.getApiSettings();
            steamGridDBEnabledCheckBox.setSelected(apiSettings.isSteamGridDb());
            steamGridDBApiKeyTextField.setText(apiSettings.getSteamGridDbKey());
            igdbEnabledCheckBox.setSelected(apiSettings.isIgdb());
            igdbClientIdTextField.setText(apiSettings.getIgdbClientId());
            igdbSecretTextField.setText(apiSettings.getIgdbSecret());
        }
    }

    
}