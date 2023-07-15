package manager.controllers.actionWizard;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import manager.enums.Language;
import manager.enums.SearchProvider;
import manager.models.Rom;
import manager.models.Settings;
import manager.services.SettingsService;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SearchProviderPanelController {

    @FXML
    private CheckBox renameRomEnable;

    @FXML
    private TextField gameName;

    @FXML
    private ChoiceBox<SearchProvider> choiceBox;

    @FXML
    private Label name, description;

    private Rom rom;

    private RomActionWizard parentController;


    @FXML
    public void initialize() {

        Settings settings = SettingsService.loadSettings();
        boolean igdbEnable = settings.getApiSettings().isIgdb();
        boolean sgdbEnable = settings.getApiSettings().isSteamGridDb();
        
        ObservableList<SearchProvider> options = FXCollections.observableArrayList(SearchProvider.values());
    
        choiceBox.setItems(options);    
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if(newValue == SearchProvider.IGDB){
                    parentController.getNextButton().setDisable(!igdbEnable);
                }
                if(newValue == SearchProvider.SGDB){
                    parentController.getNextButton().setDisable(!sgdbEnable);
                }
                name.setText(newValue.getName());
                description.setText(newValue.getDescription());
            }
        });
        choiceBox.getSelectionModel().selectFirst();

        setLanguage(settings.getGeneral().getLanguage());
    }

    private void setLanguage(Language language) {
        if (language == null) {
            language = Language.ENGLISH;
        }

        ResourceBundle bundle = ResourceBundle.getBundle("localization/mainWindow", new Locale(language.getCode()));
        renameRomEnable.setText(bundle.getString("renameRomEnable"));

    }

    public void setParentController(RomActionWizard controller){
        parentController = controller;
    }
 
    public void receiveRom(Rom rom) {
        this.rom = rom;
        gameName.setText(rom.getName());
    }

    public Rom getRom(){
        return rom;
    }

    public Pair<SearchProvider, String> sendQuery() {
        String gameNameText = gameName.getText();
        SearchProvider selectedProvider = choiceBox.getSelectionModel().getSelectedItem();
        if (renameRomEnable.isSelected()) {
            rom.setName(gameNameText);
        }
        return new Pair<>(selectedProvider, gameNameText);
    }
}