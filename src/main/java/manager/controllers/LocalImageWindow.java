package manager.controllers;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import manager.controllers.panels.ImageCropperPanelController;
import manager.services.ImageService;

public class LocalImageWindow {
    @FXML
    private AnchorPane wizardPane, step1, step2, step3;

    @FXML
    private Label selectedFileLabel;

    @FXML
    private VBox vbox;

    @FXML
    private StackPane imagePane;
    
    @FXML
    private Button backButton, nextButton, selectAllButton;

    @FXML
    private TextField xField, yField, widthField, heightField;
    
    private int currentStep = 1; // Tracks the current step of the wizard

    private File selectedImageFile; // Stores the selected image file

    private ImageCropperPanelController imageCropperPanelController;


    @FXML
    public void initialize() {
        showCurrentStep();
        
    }
    
    /*
     * Init Helpers
     */

   

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
        if (currentStep < 3) { // Assuming there are three steps in total
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
        step1.setVisible(false);
        step2.setVisible(false);
        step3.setVisible(false);
        
        switch (currentStep) {
            case 1:
                step1.setVisible(true);
                backButton.setDisable(true);
                nextButton.setDisable(selectedImageFile == null); // Disable Next button if no image is selected
                break;
            case 2:
                step2.setVisible(true);
                backButton.setDisable(false);
                nextButton.setDisable(false);
                imageCropperPanelController = loadStep2Panel();
                imageCropperPanelController.loadImage(selectedImageFile);
                break;
            case 3:
                step3.setVisible(true);
                backButton.setDisable(false);
                nextButton.setDisable(true);
                break;
        }
    }

    /*
     * Second Step Actions
     */

    private ImageCropperPanelController loadStep2Panel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ImageCropperPanel.fxml"));
            Node step2Panel = loader.load();
            step2.getChildren().setAll(step2Panel);

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
    
}
