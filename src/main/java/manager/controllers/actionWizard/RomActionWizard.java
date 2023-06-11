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
import manager.enums.OperatingMode;
import manager.enums.devices.FunkeyDevice;
import manager.models.Rom;
import manager.models.Settings;
import manager.services.ImageService;
import manager.services.SettingsService;
import manager.controllers.actionWizard.FinalImagePreviewPanel;

import java.awt.image.BufferedImage;
import javafx.scene.image.Image;

public class RomActionWizard {
    @FXML
    private AnchorPane wizardPane, selectMode, localFileSelect, imageCropper, finalPreview, WindowActionButtons;

    @FXML
    private Label selectedFileLabel;

    @FXML
    private VBox vbox;

    @FXML
    private StackPane imagePane;
    
    @FXML
    private Button backButton, nextButton, selectAllButton, saveButton;

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

    @FXML
    public void initialize() {
        // Add event listeners for drag functionality
        WindowActionButtons.setOnMousePressed(this::handleMousePressed);
        WindowActionButtons.setOnMouseDragged(this::handleMouseDragged);

        operatingModeSelection = new ToggleGroup();

        localImageSelect.setToggleGroup(operatingModeSelection);
        onlineImageSearch.setToggleGroup(operatingModeSelection);
        localRomPatcher.setToggleGroup(operatingModeSelection);
        onlineRomPatcher.setToggleGroup(operatingModeSelection);

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
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        primaryStage.setX(event.getScreenX() - xOffset);
        primaryStage.setY(event.getScreenY() - yOffset);
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
        if (currentStep < 4) { // Assuming there are three steps in total
            currentStep++;
            showCurrentStep();
        }
    }

    private void setVisible(String panel) {
        saveButton.setVisible(false);
        switch (panel) {
            case "selectMode":
                selectMode.setVisible(true);
                localFileSelect.setVisible(false);
                imageCropper.setVisible(false);
                finalPreview.setVisible(false);
                break;
            case "localFileSelect":
                loadFileSelector();
                selectMode.setVisible(false);
                localFileSelect.setVisible(true);
                imageCropper.setVisible(false);
                finalPreview.setVisible(false);
                break;
            case "imageCropper":
                loadImageCropper().loadImage(selectedImageFile);
                selectMode.setVisible(false);
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
                // loadFinalImagePreview().receiveData(imageCropperPanelController.cropImage(), selectedRom);;
                selectMode.setVisible(false);
                localFileSelect.setVisible(false);
                imageCropper.setVisible(false);
                finalPreview.setVisible(true);
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
                        nextButton.setVisible(false);
                        break;
                }
                break;
            case ONLINE_IMAGE:
                switch (currentStep) 
                {
                    case 1: 
                        selectMode.setVisible(true);
                        backButton.setDisable(true);
                        nextButton.setDisable(selectedImageFile == null); // Disable Next button if no image is selected
                        break;
                    case 2:
                        imageCropper.setVisible(true);
                        backButton.setDisable(false);
                        nextButton.setDisable(false);
                        imageCropperPanelController = loadImageCropper();
                        imageCropperPanelController.loadImage(selectedImageFile);
                        break;
                    case 3:
                        finalPreview.setVisible(true);
                        backButton.setDisable(false);
                        nextButton.setDisable(true);
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

    @FXML
    private void saveImage(){
        if(finalImagePreviewPanel.saveImage()){
            Stage stage = (Stage) wizardPane.getScene().getWindow();
            stage.close();
        }
    }


}
