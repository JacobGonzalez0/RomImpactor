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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import manager.enums.OperatingMode;
import manager.enums.devices.DeviceSupport;
import manager.enums.devices.FunkeyDevice;
import manager.models.Rom;
import manager.models.Settings;
import manager.services.ImageService;
import manager.services.SettingsService;

import java.awt.image.BufferedImage;
import javafx.scene.image.Image;

public class RomActionWizard {
    @FXML
    private AnchorPane wizardPane, selectMode, localFileSelect, imageCropper, finalPreview;

    @FXML
    private Label selectedFileLabel;

    @FXML
    private VBox vbox;

    @FXML
    private StackPane imagePane;
    
    @FXML
    private Button backButton, nextButton, selectAllButton;

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

    private OperatingMode operatingMode; 

    private Rom selectedRom;

    @FXML
    public void initialize() {
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

    /*
     * First Step Actions
     */

    @FXML
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        Stage stage = (Stage) wizardPane.getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);
        if (selectedImageFile != null) {
            selectedFileLabel.setText(selectedImageFile.getAbsolutePath());
            nextButton.setDisable(false); // Enable Next button if an image is selected
        }
    }
    
    private void showCurrentStep() {
        selectMode.setVisible(false);
        imageCropper.setVisible(false);
        finalPreview.setVisible(false);
        
        if (operatingMode == null) {
            System.out.println("no Operating mode");
            selectMode.setVisible(true);
            backButton.setDisable(false);
            nextButton.setDisable(false); // Disable Next button if no image is selected
            return;
        }


        switch(operatingMode){
            case LOCAL_IMAGE:
                switch (currentStep) 
                {
                    case 1: //Select Mode
                        selectMode.setVisible(true);
                        localFileSelect.setVisible(false);
                        backButton.setDisable(false);
                        nextButton.setDisable(false); // Disable Next button if no image is selected
                        break;
                    case 2:
                        selectMode.setVisible(false);
                        localFileSelect.setVisible(true);
                        backButton.setDisable(true);
                        nextButton.setDisable(selectedImageFile == null);
                        break;
                    case 3:
                        imageCropper.setVisible(true);
                        backButton.setDisable(false);
                        nextButton.setDisable(false);
                        imageCropperPanelController = loadStep2Panel();
                        imageCropperPanelController.loadImage(selectedImageFile);
                        break;
                    case 4:
                        finalPreview.setVisible(true);
                        loadStep3Image(imageCropperPanelController.cropImage());
                        backButton.setDisable(false);
                        nextButton.setDisable(true);
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
                        imageCropperPanelController = loadStep2Panel();
                        imageCropperPanelController.loadImage(selectedImageFile);
                        break;
                    case 3:
                        finalPreview.setVisible(true);
                        loadStep3Image(imageCropperPanelController.cropImage());
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
     * Second Step Actions
     */

    private ImageCropperPanelController loadStep2Panel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/actionWizard/ImageCropperPanel.fxml"));
            Node step2Panel = loader.load();
            imageCropper.getChildren().setAll(step2Panel);

            // stretch the loaded panel to fit the step2 pane
            AnchorPane.setTopAnchor(step2Panel, 0.0);
            AnchorPane.setBottomAnchor(step2Panel, 0.0);
            AnchorPane.setLeftAnchor(step2Panel, 0.0);
            AnchorPane.setRightAnchor(step2Panel, 0.0);
            
            ImageCropperPanelController controller = loader.getController();
            // You can now access methods or variables of the Step2PanelController
            
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

    private void loadStep3Image(BufferedImage inputImage){
        Image image = ImageService.convertToFxImage(inputImage);
        croppedImage.setImage(image);
    }

    @FXML
    private void saveImage(){
        try {
            Settings settings = SettingsService.loadSettings();
            DeviceSupport deviceName = DeviceSupport.getByName(settings.getGeneral().getDeviceProfile());

            String relativeFilePath = "";
            switch (deviceName) {
                case FUNKEY_S:
                    FunkeyDevice funkeyDevice = FunkeyDevice.valueOf(selectedRom.getSystem());
                    relativeFilePath = funkeyDevice.getImageRegexPattern();
                    break;
            }

            //implement consoles here

            // Get the base name of the Rom file (without extension)
            String romBaseName = selectedRom.getRomFile().getName();
            int pos = romBaseName.lastIndexOf(".");
            if (pos > 0) {
                romBaseName = romBaseName.substring(0, pos);
            }

            // Set the image file name as the base name of the Rom file with .png extension
            String imageName = romBaseName + ".png";
            File imageFile = new File(selectedRom.getRomFile().getParentFile(), imageName);

            ImageService.convertAndResizeImage(
                imageCropperPanelController.cropImage(),
                imageFile.getAbsolutePath(),
                true
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }





}
