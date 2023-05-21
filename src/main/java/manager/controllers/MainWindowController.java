package manager.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import manager.elements.SystemListCell;
import manager.models.SystemListItem;
import manager.services.DirectoryService;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.util.List;

import org.kordamp.ikonli.javafx.FontIcon;

public class MainWindowController {
    @FXML
    private MenuItem optionsMenuItem, closeMenuItem;
    @FXML
    private Button closeButton, maximizeButton, minimizeButton;
    @FXML
    private Button changeDirButton, addRomButton, localImageButton, onlineSearchButton;
    @FXML
    private Label directoryLabel, leftStatus, rightStatus;
    @FXML
    private ListView<String> romListView;
    @FXML
    private ListView<SystemListItem>systemListView;
    @FXML
    private ImageView imagePreview;
    @FXML
    private HBox topBar;

    private Stage primaryStage;

    private double xOffset;
    private double yOffset;

    // Initialize method, called after all @FXML annotated members have been injected
    @FXML
    public void initialize() {
        // Add event listeners for drag functionality
        topBar.setOnMousePressed(this::handleMousePressed);
        topBar.setOnMouseDragged(this::handleMouseDragged);

        // Create an ObservableList to hold the data
        ObservableList<SystemListItem> systemList = FXCollections.observableArrayList();
        
        List<SystemListItem> systemListItems = DirectoryService.loadDevice();

        for(SystemListItem i : systemListItems){
            systemList.add(i);
        }

        // Set the custom cell factory for the ListView
        systemListView.setCellFactory(listView -> new SystemListCell());

        // Set the ObservableList as the data source for the ListView
        systemListView.setItems(systemList);


    }

    private void handleMousePressed(javafx.scene.input.MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void handleMouseDragged(javafx.scene.input.MouseEvent event) {
        primaryStage.setX(event.getScreenX() - xOffset);
        primaryStage.setY(event.getScreenY() - yOffset);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public MainWindowController() {
  
    }
    
    @FXML
    public void handleMinimizeButton() {
        primaryStage.setIconified(true);  // Minimize the window
    }

    @FXML
    public void handleCloseMenuItem() {
        primaryStage.close();  // Close the window
    }

    @FXML
    public void handleCloseButton() {
        primaryStage.close();  // Close the window
    }

    
    @FXML
    public void handleOptionsMenuItem() {
        // Handle optionsMenuItem action here
    }


    // Similar methods for all your buttons...

    @FXML
    public void handleChangeDirButton() {
        // Handle changeDirButton action here
    }

    
}