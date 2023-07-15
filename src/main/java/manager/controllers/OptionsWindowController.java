package manager.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import manager.enums.Language;
import manager.enums.devices.DeviceSupport;
import manager.models.ApiSettings;
import manager.models.General;
import manager.models.Settings;
import manager.services.DirectoryService;
import manager.services.SettingsService;

public class OptionsWindowController {
    @FXML
    private TabPane tabPane;

    @FXML
    private ComboBox<String> deviceProfileComboBox;
    
    @FXML
    private ComboBox<Language> languageComboBox;

    @FXML
    private Label steamGridDBLabel, igdbLabel,  deviceProfileLabel, manualScaleSizeLabel, deviceDirectoryLabel, languageLabel;

    @FXML
    private TextField manualScaleSizeTextField;

    @FXML
    private TextField deviceDirectoryTextField,steamGridDBApiKeyTextField, igdbSecretTextField, igdbClientIdTextField;

    @FXML
    private CheckBox steamGridDBEnabledCheckBox, igdbEnabledCheckBox, manualScaleEnableCheckBox;

    @FXML
    private Button changeDirectoryButton;


    // Optional: You can add initialization code using @FXML initialize method
    @FXML
    private void initialize() {
        
        Settings settings = SettingsService.loadSettings();

        changeDirectoryButton.setOnAction(e -> chooseDirectory());

        if (settings != null) {
            populateSettingsUI(settings);
            setLanguage(settings.getGeneral().getLanguage());
        }
        else 
        {
            setLanguage(settings.getGeneral().getLanguage());
            populateSettingsUI(SettingsService.defaultSettings());
        }
        
        languageComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Language selectedLanguage = languageComboBox.getSelectionModel().getSelectedItem();
            
            // Call the setLanguage() method with the selected language to load the new language
            setLanguage(selectedLanguage);
        });
        
    }

    private void setLanguage(Language language) {
        if (language == null) {
            language = Language.ENGLISH;
        }

        ResourceBundle bundle = ResourceBundle.getBundle("localization/mainWindow", new Locale(language.getCode()));
    
        // Retrieve translations for each UI element from the resource bundle
        deviceProfileComboBox.setPromptText(bundle.getString("deviceProfilePromptText"));
        languageComboBox.setPromptText(bundle.getString("languagePromptText"));
        steamGridDBLabel.setText(bundle.getString("steamGridDBLabel"));
        igdbLabel.setText(bundle.getString("igdbLabel"));
        deviceProfileLabel.setText(bundle.getString("deviceProfileLabel"));
        manualScaleSizeLabel.setText(bundle.getString("manualScaleSizeLabel"));
        deviceDirectoryLabel.setText(bundle.getString("deviceDirectoryLabel"));
        languageLabel.setText(bundle.getString("languageLabel"));
    
        manualScaleSizeTextField.setPromptText(bundle.getString("manualScaleSizePromptText"));
        deviceDirectoryTextField.setPromptText(bundle.getString("deviceDirectoryPromptText"));
        steamGridDBApiKeyTextField.setPromptText(bundle.getString("steamGridDBApiKeyPromptText"));
        igdbSecretTextField.setPromptText(bundle.getString("igdbSecretPromptText"));
        igdbClientIdTextField.setPromptText(bundle.getString("igdbClientIdPromptText"));
    
        steamGridDBEnabledCheckBox.setText(bundle.getString("steamGridDBEnabledCheckBox"));
        igdbEnabledCheckBox.setText(bundle.getString("igdbEnabledCheckBox"));
        manualScaleEnableCheckBox.setText(bundle.getString("manualScaleEnableCheckBox"));
    
        changeDirectoryButton.setText(bundle.getString("changeDirectoryButtonText"));
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

            deviceDirectoryTextField.setText(general.getRootDirectory());
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

    private void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String initialDirectoryPath = deviceDirectoryTextField.getText();
        File initialDirectory = new File(initialDirectoryPath);
        if (!initialDirectory.isDirectory()) {
            String removableDrivePath = DirectoryService.getFirstRemovableDrivePath();
            if (removableDrivePath != null) {
                initialDirectory = new File(removableDrivePath);
            }
        }
        directoryChooser.setInitialDirectory(initialDirectory);
        File selectedDirectory = directoryChooser.showDialog(changeDirectoryButton.getScene().getWindow());
        if (selectedDirectory != null) {
            deviceDirectoryTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }
    
}