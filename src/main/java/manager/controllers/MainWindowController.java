package manager.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import manager.elements.RomListCell;
import manager.elements.SystemListCell;
import manager.models.Rom;
import manager.models.SystemListItem;
import manager.services.DirectoryService;
import manager.services.ImageService;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class MainWindowController {
    @FXML
    private MenuItem optionsMenuItem, closeMenuItem;
    @FXML
    private Button closeButton, maximizeButton, minimizeButton;
    @FXML
    private Button changeDirButton, addRomButton, localImageButton, onlineSearchButton;
    @FXML
    private Label directoryLabel, leftStatus, rightStatus, romInfoTitle, romInfoSubTitle;
    @FXML
    private ListView<Rom> romListView;
    @FXML
    private ListView<SystemListItem>systemListView;
    @FXML
    private ImageView imagePreview;
    @FXML
    private HBox topBar;
    @FXML
    private Pane romInfo;
    
    private Stage primaryStage;

    private double xOffset;
    private double yOffset;

    // Initialize method, called after all @FXML annotated members have been injected
    @FXML
    public void initialize() {
        // Add event listeners for drag functionality
        topBar.setOnMousePressed(this::handleMousePressed);
        topBar.setOnMouseDragged(this::handleMouseDragged);

        // Load the resource bundle for the desired language
        ResourceBundle bundle = ResourceBundle.getBundle("mainWindow", new Locale("en"));

        // Retrieve translations for each UI element from the resource bundle
        optionsMenuItem.setText(bundle.getString("optionsMenuItem"));
        closeMenuItem.setText(bundle.getString("closeMenuItem"));

        changeDirButton.setText(bundle.getString("changeDirButton"));
        addRomButton.setText(bundle.getString("addRomButton"));
        localImageButton.setText(bundle.getString("localImageButton"));
        onlineSearchButton.setText(bundle.getString("searchOnlineButton"));

        directoryLabel.setText(bundle.getString("currentDirectoryLabel"));
        leftStatus.setText(bundle.getString("leftStatusLabel"));
        rightStatus.setText(bundle.getString("rightStatusLabel"));


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

    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        primaryStage.setX(event.getScreenX() - xOffset);
        primaryStage.setY(event.getScreenY() - yOffset);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public MainWindowController() {
  
    }

    @FXML
    public void handleSystemListViewClick(MouseEvent event){
        // Get the selected item from the ListView
        SystemListItem selectedItem = systemListView.getSelectionModel().getSelectedItem();

        // Create an ObservableList to hold the data
        ObservableList<Rom> romList = FXCollections.observableArrayList();
        
        List<Rom> romListItems = selectedItem.getRoms();

        for(Rom rom : romListItems){
            romList.add(rom);
        }

        // Set the custom cell factory for the ListView
        romListView.setCellFactory(listView -> new RomListCell());

        // Set the ObservableList as the data source for the ListView
        romListView.setItems(romList);
    }

    /*
     *  RomView Events
     */

     @FXML
     public void handleRomListViewClick(MouseEvent event) {

        updateRomPreview(romListView.getSelectionModel().getSelectedItem());

        if (event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
            // Only trigger the action on a right click event
            
        }
     }
     

    @FXML
    public void handleRomListViewKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN ||
            event.getCode() == KeyCode.PAGE_UP || event.getCode() == KeyCode.PAGE_DOWN
        ) {

            updateRomPreview(romListView.getSelectionModel().getSelectedItem());

        }
    }

    public void updateRomPreview(Rom rom){


        romInfoTitle.setText("Name: " + rom.getName());

        if(rom.getReleaseDate() == null || rom.getReleaseDate().isEmpty()){
            romInfoSubTitle.setText("Release Date: None" );
        }else{
            romInfoSubTitle.setText("Release Date: " + rom.getReleaseDate());
        }

        if(rom.getImageFile() == null || !rom.getImageFile().exists()){
            imagePreview.setImage(new Image(getClass().getResourceAsStream("/images/noimage.png")));
        }else{
            imagePreview.setImage(ImageService.convertToFxImage(rom.getImageAsBufferedImage()));
        }
    }

    /*
     *  Titlebar Events
     */
    
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

    /*
     * Top Menu Events
     */
    
    @FXML
    public void handleOptionsMenuItem() {
        try {
            // Load the OptionsWindow.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OptionsWindow.fxml"));
            Parent root = loader.load();
            
            // Create a new stage for the options window
            Stage optionsStage = new Stage();
            optionsStage.setTitle("Options");
            optionsStage.setScene(new Scene(root));
            
            // Show the options window
            optionsStage.show();

            // Get the controller of the options window
            OptionsWindowController optionsController = loader.getController();

            // Set listener for options window closing event
            optionsStage.setOnHidden(event -> {
                // Perform saveSettings() operation when the options window is closed
                optionsController.saveSettings();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Similar methods for all your buttons...

    @FXML
    public void handleChangeDirButton() {
        // Handle changeDirButton action here
    }

    @FXML
    public void handlelocalImageButton() {
        try {
            // Load the OptionsWindow.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocalImageWindow.fxml"));
            Parent root = loader.load();

            // Create a new stage for the options window
            Stage localImageStage = new Stage();
            localImageStage.setTitle("Local Image Wizard");
            localImageStage.setScene(new Scene(root));

            // Show the options window
            localImageStage.show();

            // Get the controller of the options window
            LocalImageWindow localImageController = loader.getController();

            // Set listener for options window closing event
            localImageStage.setOnHidden(event -> {
                // Perform saveSettings() operation when the options window is closed
                // localImageController.saveSettings();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}