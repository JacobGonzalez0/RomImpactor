package manager.controllers.actionWizard;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import manager.services.ImageService;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

public class SelectLocalImagePanelController {

    @FXML
    private AnchorPane wizardPane;

    @FXML
    private Label filenameLabel;

    @FXML
    private ImageView imagePreview;

    @FXML
    private Button fileSelectButton;

    @FXML
    private Pane dragPane;

    private RomActionWizard parentController;

    private static final List<String> SUPPORTED_IMAGE_EXTENSIONS = Arrays.asList(
            ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".webp"
    );

    private static final List<String> SUPPORTED_IMAGE_EXTENSIONS_SELECT = Arrays.asList(
        "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.webp"
);



    @FXML
    private void initialize() {
        // Set up event handlers for file selection and drag-and-drop

        

        dragPane.setOnDragOver(this::handleDragOver);
        dragPane.setOnDragDropped(this::handleDragDropped);
        dragPane.setOnMouseClicked(this::handleMouseClicked);

        dragPane.setFocusTraversable(true);
        dragPane.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasImage()) {
                    setFilenameLabel("Clipboard Image");
                    imagePreview.setImage(clipboard.getImage());
                    parentController.setSelectedImageFile(sendImage(clipboard.getImage()));
                }
            }
        });
        
        wizardPane.setFocusTraversable(true);

        wizardPane.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.V) {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                if (clipboard.hasImage()) {
                    setFilenameLabel("Clipboard Image");
                    imagePreview.setImage(clipboard.getImage());
                    parentController.setSelectedImageFile(sendImage(clipboard.getImage()));
                }
            }
        });
        
    }

    public void setParentController(RomActionWizard controller){
        parentController = controller;
    }

    private void handleDragOver(DragEvent event) {
        System.out.println("handleDragOver called");
        if (event.getDragboard().hasFiles()) {
            for (File file : event.getDragboard().getFiles()) {
                if (isImageFile(file)) {
                    event.acceptTransferModes(TransferMode.COPY);
                    event.consume();
                    return;
                }
            }
        } else if (event.getDragboard().hasImage()) {
            event.acceptTransferModes(TransferMode.COPY);
            event.consume();
            return;
        }

        event.consume();
    }

    @FXML
    private void handleFileSelect() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", SUPPORTED_IMAGE_EXTENSIONS_SELECT)
        );

        File selectedFile = fileChooser.showOpenDialog(fileSelectButton.getScene().getWindow());
        if (selectedFile != null) {
            setFilenameLabel(selectedFile.getName());
            loadImage(selectedFile);
        }
    }

    public File sendImage(Image image) {
    
        if (image != null) {
            try {
                BufferedImage bufferedImage = ImageService.convertToBufferedImage(image, true);
    
                // Create a temporary file
                File outputFile = File.createTempFile("image", ".png");
    
                // Write the BufferedImage to the temporary file
                ImageIO.write(bufferedImage, "png", outputFile);

                return outputFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        return null;
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        boolean success = false;

        if (dragboard.hasFiles()) {
            for (File file : dragboard.getFiles()) {
                if (isImageFile(file)) {
                    setFilenameLabel("Dragged File");
                    loadImage(file);
                    success = true;
                    break;
                }
            }
        } else if (dragboard.hasImage()) {
            setFilenameLabel("Clipboard Image");
            imagePreview.setImage(dragboard.getImage());
            success = true;
        }

        event.setDropCompleted(success);
        event.consume();
    }

    private void handleMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasImage()) {
                setFilenameLabel("Clipboard Image");
                imagePreview.setImage(clipboard.getImage());
            }
        }
    }

    private boolean isImageFile(File file) {
        String extension = getFileExtension(file);
        return extension != null && SUPPORTED_IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    }
    

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex).toLowerCase();
        }
        return null;
    }

    private void setFilenameLabel(String text) {
        filenameLabel.setText(text);
    }

    private void loadImage(File file) {
        try {
            Image image = new Image(new FileInputStream(file));
            imagePreview.setImage(image);
            parentController.setSelectedImageFile(file);
        } catch (IOException e) {
            // Handle the exception if unable to load the image
            e.printStackTrace();
        }
    }

}
