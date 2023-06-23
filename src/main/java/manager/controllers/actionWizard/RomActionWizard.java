package manager.controllers.actionWizard;

import java.io.File;
import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import manager.enums.OperatingMode;
import manager.enums.SearchProvider;
import manager.enums.devices.FunkeyDevice;
import manager.models.GameSearchResult;
import manager.models.Rom;
import manager.models.Settings;
import manager.services.ImageService;
import manager.services.SettingsService;
import manager.controllers.MainWindowController;
import manager.controllers.actionWizard.FinalImagePreviewPanel;

import java.awt.image.BufferedImage;
import javafx.scene.image.Image;

public class RomActionWizard {
    @FXML
    private AnchorPane wizardPane, selectMode, localFileSelect, imageCropper, finalPreview, WindowActionButtons, gameSearch, coverSearch, providerSelect;

    @FXML
    private Label selectedFileLabel;

    @FXML
    private VBox vbox;

    @FXML
    private StackPane imagePane;
    
    @FXML
    private Button backButton, nextButton, saveButton;

    @FXML
    private ImageView croppedImage;

    private ToggleGroup operatingModeSelection;

    @FXML
    private RadioButton localImageSelect, onlineImageSearch, localRomPatcher, onlineRomPatcher;

    @FXML
    private TextField xField, yField, widthField, heightField;
    
    private int currentStep = 1; // Tracks the current step of the wizard

    private File selectedImageFile; // Stores the selected image file

    private ImageCropperPanelController imageCropperPanelController;
    private SelectLocalImagePanelController selectLocalImagePanelController;

    private OperatingMode operatingMode; 

    private Rom selectedRom;

    private FinalImagePreviewPanel finalImagePreviewPanel;

    private double xOffset;
    private double yOffset;

    private Stage primaryStage;

    private SearchGamePanelController searchGamePanelController;

    private SearchCoverPanelController coverGamePanelController;

    private SearchProviderPanelController searchProviderPanelController;

    private MainWindowController parentController;

    @FXML
    public void initialize() {
        // Add event listeners for drag functionality
        WindowActionButtons.setOnMousePressed(this::handleMousePressed);
        WindowActionButtons.setOnMouseDragged(this::handleMouseDragged);

        operatingModeSelection = new ToggleGroup();

        localImageSelect.setToggleGroup(operatingModeSelection);
        onlineImageSearch.setToggleGroup(operatingModeSelection);

       
        operatingModeSelection.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov,
                    Toggle old_toggle, Toggle new_toggle) {
                if (operatingModeSelection.getSelectedToggle() != null) {
                    RadioButton selectedButton = (RadioButton) operatingModeSelection.getSelectedToggle();
                    if (selectedButton.equals(localImageSelect)) {
                        // Code for localImageSelect
                        selectOperatingMode(OperatingMode.LOCAL_IMAGE);
                    } else if (selectedButton.equals(onlineImageSearch)) {
                        // Code for onlineImageSearch
                        selectOperatingMode(OperatingMode.ONLINE_IMAGE);
                    } else if (selectedButton.equals(localRomPatcher)) {
                        // Code for localRomPatcher
                        selectOperatingMode(OperatingMode.LOCAL_PATCH);
                    } else if (selectedButton.equals(onlineRomPatcher)) {
                        // Code for onlineRomPatcher
                        selectOperatingMode(OperatingMode.ONLINE_PATCH);
                    }
                    showCurrentStep();
                }
            }
        });
        showCurrentStep();

        backButton.setVisible(false);
        saveButton.setVisible(false);
    }
    
    /*
     * Init Helpers
     */

    public OperatingMode selectOperatingMode(OperatingMode input){
        this.operatingMode = input;
        return input;
    }

    public void receiveRom(Rom rom){
        this.selectedRom = rom;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /*
     * Window Action buttons
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

    private void handleMousePressed(MouseEvent event) {
        if (event.getY() > 6 && !parentController.isDragging()) { // Ignore events within the first 6 pixels from the top
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        }
    }
    
    private void handleMouseDragged(MouseEvent event) {
        if (event.getY() > 6 && !parentController.isDragging()) {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        }
    }
    
    /*
     * Wizard Buttons
     */
    
    @FXML
    public void goBack(ActionEvent event) {
        if (currentStep > 1) {
            currentStep--;
            showCurrentStep();
        }
    }
    
    @FXML
    public void goNext(ActionEvent event) {
        if (currentStep < 10) { 
            currentStep++;
            showCurrentStep();
        }
    }

    private void setVisible(String panel) {
        saveButton.setVisible(false);
        switch (panel) {
            case "selectMode":
                selectMode.setVisible(true);
                providerSelect.setVisible(false);
                gameSearch.setVisible(false);
                coverSearch.setVisible(false);
                localFileSelect.setVisible(false);
                imageCropper.setVisible(false);
                finalPreview.setVisible(false);
                break;
            case "localFileSelect":
                loadFileSelector();
                selectMode.setVisible(false);
                providerSelect.setVisible(false);
                gameSearch.setVisible(false);
                coverSearch.setVisible(false);
                localFileSelect.setVisible(true);
                imageCropper.setVisible(false);
                finalPreview.setVisible(false);
                break;
            case "imageCropper":
                loadImageCropper().loadImage(selectedImageFile);
                selectMode.setVisible(false);
                providerSelect.setVisible(false);
                gameSearch.setVisible(false);
                coverSearch.setVisible(false);
                localFileSelect.setVisible(false);
                imageCropper.setVisible(true);
                finalPreview.setVisible(false);
                break;
            case "wizardPane":
                selectMode.setVisible(false);
                localFileSelect.setVisible(false);
                imageCropper.setVisible(false);
                finalPreview.setVisible(false);
                break;
            case "finalPreview":
                loadFinalImagePreview().receiveData(imageCropperPanelController.cropImage(), selectedRom);
                selectMode.setVisible(false);
                providerSelect.setVisible(false);
                gameSearch.setVisible(false);
                coverSearch.setVisible(false);
                localFileSelect.setVisible(false);
                imageCropper.setVisible(false);
                finalPreview.setVisible(true);
                saveButton.setVisible(true);
                break;
            case "gameSearch":
                try {
                    Pair<SearchProvider, String> pair = searchProviderPanelController.sendQuery();

                    selectedRom = searchProviderPanelController.getRom();

                    loadSearchGame().receiveQuery(pair);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                };
                selectMode.setVisible(false);
                providerSelect.setVisible(false);
                gameSearch.setVisible(true);
                coverSearch.setVisible(false);
                localFileSelect.setVisible(false);
                imageCropper.setVisible(false);
                finalPreview.setVisible(false);
                saveButton.setVisible(true);
                break;
            case "coverSearch":
                try {                  
                    loadSearchCover().receiveQuery(searchGamePanelController.sendQuery());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                };
                selectMode.setVisible(false);
                providerSelect.setVisible(false);
                gameSearch.setVisible(false);
                coverSearch.setVisible(true);
                localFileSelect.setVisible(false);
                imageCropper.setVisible(false);
                finalPreview.setVisible(false);
                saveButton.setVisible(true);
                break;
            case "providerSelect":
                loadProviderSelect().receiveRom(selectedRom);
                selectMode.setVisible(false);
                providerSelect.setVisible(true);
                gameSearch.setVisible(false);
                coverSearch.setVisible(false);
                localFileSelect.setVisible(false);
                imageCropper.setVisible(false);
                finalPreview.setVisible(false);
                saveButton.setVisible(true);
                break;
            default:
                // Handle unrecognized panel
                break;
        }
    }    
    
    private void showCurrentStep() {

        if (operatingMode == null) {
            setVisible("selectMode");
            backButton.setDisable(false);
            nextButton.setDisable(false); // Disable Next button if no image is selected
            return;
        }


        switch(operatingMode){
            case LOCAL_IMAGE:
                switch (currentStep) 
                {
                    case 1: //Select Mode
                        setVisible("selectMode");
                        backButton.setDisable(false);
                        backButton.setVisible(false);
                        nextButton.setDisable(false); // Disable Next button if no image is selected
                        break;
                    case 2: // Local file select
                        setVisible("localFileSelect");
                        backButton.setDisable(false);
                        backButton.setVisible(true);
                        nextButton.setDisable(true);  
                        break;
                    case 3:
                        setVisible("imageCropper");
                        backButton.setDisable(false);
                        nextButton.setDisable(false);  
                        saveButton.setVisible(false);
                        nextButton.setVisible(true);
                        break;
                    case 4:
                        setVisible("finalPreview");
                        backButton.setDisable(false);
                        nextButton.setDisable(false);
                        saveButton.setVisible(true);
                        nextButton.setVisible(false);
                        break;
                }
                break;
            case ONLINE_IMAGE:
                switch (currentStep) 
                {
                    case 1: 
                        setVisible("selectMode");
                        backButton.setDisable(false);
                        backButton.setVisible(false);
                        nextButton.setDisable(false);  
                        saveButton.setVisible(false);
                        nextButton.setVisible(true); 
                        break;
                    case 2: 
                        setVisible("providerSelect");
                        backButton.setDisable(false);
                        backButton.setVisible(true);
                        nextButton.setDisable(false);  
                        saveButton.setVisible(false);
                        nextButton.setVisible(true); 
                        break;
                    case 3:
                        setVisible("gameSearch");
                        backButton.setDisable(false);
                        backButton.setVisible(true);
                        nextButton.setDisable(false);  
                        saveButton.setVisible(false);
                        nextButton.setVisible(true);
                        break;
                    case 4:
                        setVisible("coverSearch");
                        backButton.setDisable(false);
                        nextButton.setDisable(false);  
                        saveButton.setVisible(false);
                        nextButton.setVisible(true);
                        break;
                    case 5:
                        setVisible("imageCropper");
                        backButton.setDisable(false);
                        nextButton.setDisable(false);  
                        saveButton.setVisible(false);
                        nextButton.setVisible(true);
                        break;
                    case 6:
                        setVisible("finalPreview");
                        backButton.setDisable(false);
                        nextButton.setDisable(false);
                        saveButton.setVisible(true);
                        nextButton.setVisible(false);
                        break;
                }
            default:
                switch (currentStep) 
                {
                    case 1: 
                        selectMode.setVisible(true);
                        backButton.setDisable(false);
                        nextButton.setDisable(false); // Disable Next button if no image is selected
                        break;
                }
                break;
                
        }
        
    }

    /*
     * File Select Actions
     */

     private SelectLocalImagePanelController loadFileSelector() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/actionWizard/SelectLocalImagePanel.fxml"));
            Node node = loader.load();
            localFileSelect.getChildren().setAll(node);
    
            // Stretch the loaded panel to fit the step2 pane
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
    
            SelectLocalImagePanelController controller = loader.getController();
            this.selectLocalImagePanelController = controller;
            controller.setParentController(this);

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return null;
        }
    }
    
    public void setSelectedImageFile(File file){
        this.selectedImageFile = file;
        nextButton.setDisable(false);
    }

    /*
     * Image Cropper Actions
     */

    private ImageCropperPanelController loadImageCropper() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/actionWizard/ImageCropperPanel.fxml"));
            Node node = loader.load();
            imageCropper.getChildren().setAll(node);

            // stretch the loaded panel to fit the step2 pane
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            
            
            ImageCropperPanelController controller = loader.getController();
            this.imageCropperPanelController = controller;
           
            
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return null;
        }
    }  
    
    /*
     * Third Step Actions
     */

     private FinalImagePreviewPanel loadFinalImagePreview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/actionWizard/FinalImagePreviewPanel.fxml"));
            Node node = loader.load();
            finalPreview.getChildren().setAll(node);

            // stretch the loaded panel to fit the step2 pane
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            
            FinalImagePreviewPanel controller = loader.getController();
            this.finalImagePreviewPanel = controller;
            // You can now access methods or variables of the Step2PanelController
            
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return null;
        }
    } 

    /*
     * Game Search Actions
     */

    private SearchGamePanelController loadSearchGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/actionWizard/SearchGamePanel.fxml"));
            Node node = loader.load();
            gameSearch.getChildren().setAll(node);

            // stretch the loaded panel to fit the step2 pane
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            
            
            SearchGamePanelController controller = loader.getController();
            this.searchGamePanelController = controller;
           
            
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return null;
        }
    } 
    /*
     * Cover Search Actions
     */

     private SearchCoverPanelController loadSearchCover() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/actionWizard/SearchCoverPanel.fxml"));
            Node node = loader.load();
            coverSearch.getChildren().setAll(node);

            // stretch the loaded panel to fit the step2 pane
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            
            
            SearchCoverPanelController controller = loader.getController();
            this.coverGamePanelController = controller;
            controller.setParentController(this);
            
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return null;
        }
    } 

    /*
     * Provider Select Actions
     */

     private SearchProviderPanelController loadProviderSelect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/actionWizard/SearchProviderPanel.fxml"));
            Node node = loader.load();
            providerSelect.getChildren().setAll(node);

            // stretch the loaded panel to fit the step2 pane
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
            
            
            SearchProviderPanelController controller = loader.getController();
            this.searchProviderPanelController = controller;
            controller.setParentController(this);

            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
            return null;
        }
    } 

    @FXML
    private void saveImage(){
        selectedRom = finalImagePreviewPanel.saveImage();
        if(selectedRom != null){   
            parentController.setSelectedRom(selectedRom);
            Stage stage = (Stage) wizardPane.getScene().getWindow();
            stage.close();
        }

    }

     /*
     * Setters/Getters
     */
    
     public Button getBackButton() {
        return backButton;
    }


    public void setBackButton(Button backButton) {
        this.backButton = backButton;
    }


    public Button getNextButton() {
        return nextButton;
    }


    public void setNextButton(Button nextButton) {
        this.nextButton = nextButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }


    public void setSaveButton(Button saveButton) {
        this.saveButton = saveButton;
    }

    public void setParentController(MainWindowController controller){
        parentController = controller;
    }
}
