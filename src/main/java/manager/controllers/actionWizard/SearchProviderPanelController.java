package manager.controllers.actionWizard;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import manager.enums.SearchProvider;
import manager.models.Rom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SearchProviderPanelController {

    @FXML
    private CheckBox renameRom;

    @FXML
    private TextField gameName;

    @FXML
    private ChoiceBox<SearchProvider> choiceBox;

    @FXML
    private Label name, description;

    private Rom rom;


    @FXML
    public void initialize() {
        ObservableList<SearchProvider> options = FXCollections.observableArrayList(SearchProvider.values());
        choiceBox.setItems(options);
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                name.setText(newValue.getName());
                description.setText(newValue.getDescription());
            }
        });
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
        if (renameRom.isSelected()) {
            rom.setName(gameNameText);
        }
        return new Pair<>(selectedProvider, gameNameText);
    }
}