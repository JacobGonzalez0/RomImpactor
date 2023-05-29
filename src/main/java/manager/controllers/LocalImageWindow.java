package manager.controllers;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import manager.services.ImageService;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

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
    private ImageView imageView;

    @FXML
    private TextField xField, yField, widthField, heightField;
    
    private int currentStep = 1; // Tracks the current step of the wizard

    private File selectedImageFile; // Stores the selected image file

    private double aspectRatio;

    // Crop rectangle variables
    private Rectangle cropRectangle;
    private double startX, startY, prevX, prevY;
    private double originalImageWidth;
    private double originalImageHeight;
    private Bounds originalCropBounds;
    // Add another Rectangle object to represent the actual crop dimensions
    private Rectangle actualCropRectangle;
    private Rectangle visibleCropRectangle;

    // Crop rectangle resize handlers
    private Rectangle nwHandle, neHandle, seHandle, swHandle; // North-West, North-East, South-East, South-West handles
    private static final double HANDLE_SIZE = 10.0; // Size of the resize handles



    @FXML
    public void initialize() {
        showCurrentStep();
        // Make second step text fields, number fields
        configureNumericTextField(xField);
        configureNumericTextField(yField);
        configureNumericTextField(widthField);
        configureNumericTextField(heightField);

        
    }
    
    /*
     * Init Helpers
     */

     private void setupVisibleCropRectangle() {
        visibleCropRectangle = new Rectangle();
        visibleCropRectangle.setStroke(Color.RED);
        visibleCropRectangle.setStrokeWidth(2);
        visibleCropRectangle.setFill(Color.rgb(255, 0, 0, 0.2));
        wizardPane.getChildren().add(visibleCropRectangle);
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
                displaySelectedImage();
                setupSecondStepListeners();
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

    private void setupSecondStepListeners(){
        // initialize the actualCropRectangle
        
        setupVisibleCropRectangle();
        setupCropRectangle();
        actualCropRectangle = new Rectangle();

        // Setup Crop Listeners
        setupTextFieldListeners();
        setupResizeListener();
    }


    private void displaySelectedImage() {
        if (selectedImageFile != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(selectedImageFile);
                originalImageWidth = bufferedImage.getWidth();
                originalImageHeight = bufferedImage.getHeight();
                Image image = ImageService.convertToFxImage(bufferedImage);
                imageView.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    

    private void configureNumericTextField(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*")) { // only accept numeric input
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }
    

    /*
     * Image Cropping Methods
     */

    private double calculateHalfDifferenceWidth() {
        double imageViewWidth = imageView.getBoundsInParent().getWidth();
        double vboxWidth = vbox.getWidth();
        double difference = vboxWidth - imageViewWidth;
        return difference / 2.0;
    }

    private double calculateHalfDifferenceHeight() {
        double imageViewHeight = imageView.getBoundsInParent().getHeight();
        double vboxHeight = vbox.getHeight();
        double difference = vboxHeight - imageViewHeight;
        return difference / 2.0;
    }

    private void setupCropRectangle() {
        cropRectangle = new Rectangle();
        cropRectangle.setStroke(Color.RED);
        cropRectangle.setStrokeWidth(2);
        cropRectangle.setFill(Color.rgb(255, 0, 0, 0.2));
        cropRectangle.setVisible(false); // Initially hide the crop rectangle
        wizardPane.getChildren().add(cropRectangle);

        wizardPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                aspectRatio = cropRectangle.getWidth() / cropRectangle.getHeight();
            }
        });

        wizardPane.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                aspectRatio = 0.0;
            }
        });

        imageView.setOnMousePressed(this::handleMousePressed);
        imageView.setOnMouseDragged(this::handleMouseDragged);
        imageView.setOnMouseReleased(this::handleMouseReleased);

        // Setup resize handles
        nwHandle = new Rectangle(HANDLE_SIZE, HANDLE_SIZE);
        neHandle = new Rectangle(HANDLE_SIZE, HANDLE_SIZE);
        seHandle = new Rectangle(HANDLE_SIZE, HANDLE_SIZE);
        swHandle = new Rectangle(HANDLE_SIZE, HANDLE_SIZE);
        Rectangle[] handles = new Rectangle[] { nwHandle, neHandle, seHandle, swHandle };

        for (Rectangle handle : handles) {
            handle.setFill(Color.RED);
            handle.setStroke(Color.WHITE);
            handle.setStrokeWidth(2);
            handle.setVisible(false);
            wizardPane.getChildren().add(handle);
        }

         // Bind handle positions to crop rectangle's properties
         nwHandle.xProperty().bind(visibleCropRectangle.xProperty());
         nwHandle.yProperty().bind(visibleCropRectangle.yProperty());
         neHandle.xProperty().bind(visibleCropRectangle.xProperty().add(visibleCropRectangle.widthProperty()).subtract(HANDLE_SIZE));
         neHandle.yProperty().bind(visibleCropRectangle.yProperty());
         seHandle.xProperty().bind(visibleCropRectangle.xProperty().add(visibleCropRectangle.widthProperty()).subtract(HANDLE_SIZE));
         seHandle.yProperty().bind(visibleCropRectangle.yProperty().add(visibleCropRectangle.heightProperty()).subtract(HANDLE_SIZE));
         swHandle.xProperty().bind(visibleCropRectangle.xProperty());
         swHandle.yProperty().bind(visibleCropRectangle.yProperty().add(visibleCropRectangle.heightProperty()).subtract(HANDLE_SIZE));
        
         // Add drag listeners to handles
        nwHandle.setOnMouseDragged(e -> resizeFromNW(e));
        neHandle.setOnMouseDragged(e -> resizeFromNE(e));
        seHandle.setOnMouseDragged(e -> resizeFromSE(e));
        swHandle.setOnMouseDragged(e -> resizeFromSW(e));
    }

    /*
     * Image Cropping Events
     */

    private void setupResizeListener() {
        imageView.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            updateCropRectangle();
        });
    }
    
    

    private void handleMousePressed(MouseEvent event) {
        nwHandle.setVisible(true);
        neHandle.setVisible(true);
        seHandle.setVisible(true);
        swHandle.setVisible(true);

        // Get the initial mouse press event location relative to the imageView
        startX = event.getX() ;
        startY = event.getY() ;
        
        // Convert to wizardPane's coordinate system
        Point2D pointInScene = imageView.localToParent(startX, startY);

        // Translate to the cropRectangle's coordinate system
        Point2D pointInRectangle = wizardPane.sceneToLocal(pointInScene);

        prevX = pointInRectangle.getX();
        prevY = pointInRectangle.getY();

        imageView.setCursor(Cursor.CROSSHAIR);
        cropRectangle.setX(startX);
        cropRectangle.setY(startY);
        cropRectangle.setWidth(0);
        cropRectangle.setHeight(0);

        // update visibleCropRectangle bounds
        visibleCropRectangle.setX(cropRectangle.getX() + calculateHalfDifferenceWidth());
        visibleCropRectangle.setY(cropRectangle.getY() + calculateHalfDifferenceHeight());
        visibleCropRectangle.setWidth(cropRectangle.getWidth());
        visibleCropRectangle.setHeight(cropRectangle.getHeight());
        visibleCropRectangle.setVisible(false);
    }

    private void handleMouseDragged(MouseEvent event) {
        nwHandle.setVisible(true);
        neHandle.setVisible(true);
        seHandle.setVisible(true);
        swHandle.setVisible(true);

        double currentX = event.getX();
        double currentY = event.getY();

        double minX = Math.min(startX, currentX);
        double minY = Math.min(startY, currentY);
        double maxX = Math.max(startX, currentX);
        double maxY = Math.max(startY, currentY);

        double newWidth = maxX - minX;
        double newHeight = maxY - minY;

        if (aspectRatio > 0) {
            // Shift is pressed
            newHeight = newWidth / aspectRatio;
        }

        if (newWidth > 0 && newHeight > 0) {
            // Check that the new dimensions are within the ImageView bounds
            Bounds imageViewBounds = imageView.getBoundsInParent();
            if (minX + newWidth > imageViewBounds.getMaxX()) {
                newWidth = imageViewBounds.getMaxX() - minX;
                newHeight = newWidth / aspectRatio;
            }
            if (minY + newHeight > imageViewBounds.getMaxY()) {
                newHeight = imageViewBounds.getMaxY() - minY;
                newWidth = newHeight * aspectRatio;
            }
        }

        cropRectangle.setX(minX );
        cropRectangle.setY(minY );
        cropRectangle.setWidth(newWidth);
        cropRectangle.setHeight(newHeight);

        // update visibleCropRectangle bounds
        visibleCropRectangle.setX(cropRectangle.getX() + calculateHalfDifferenceWidth());
        visibleCropRectangle.setY(cropRectangle.getY() + calculateHalfDifferenceHeight());
        visibleCropRectangle.setWidth(cropRectangle.getWidth());
        visibleCropRectangle.setHeight(cropRectangle.getHeight());
        visibleCropRectangle.setVisible(true);

        prevX = currentX;
        prevY = currentY;

        xField.setText(String.valueOf(minX));
        yField.setText(String.valueOf(minY));
        widthField.setText(String.valueOf(newWidth));
        heightField.setText(String.valueOf(newHeight));
    }

    private void handleMouseReleased(MouseEvent event) {
        imageView.setCursor(Cursor.DEFAULT);
        cropRectangle.setVisible(false); // Keep the crop rectangle visible
        visibleCropRectangle.setVisible(true); // Keep the visibleCropRectangle visible

        nwHandle.setVisible(true);
        neHandle.setVisible(true);
        seHandle.setVisible(true);
        swHandle.setVisible(true);
    
        // Get the bounds of the crop rectangle in the image's coordinate system
        Bounds cropBounds = cropRectangle.getBoundsInParent();
        double cropX = cropBounds.getMinX() ;
        double cropY = cropBounds.getMinY() ;
        double cropWidth = cropBounds.getWidth();
        double cropHeight = cropBounds.getHeight();
    
        // Store the crop rectangle's original coordinates and dimensions relative to the ImageView's size
        updateOriginalCropBounds(cropX,cropY,cropWidth,cropHeight);
        
    
        System.out.println("Crop Rectangle Bounds: X=" + cropX + ", Y=" + cropY + ", Width=" + cropWidth + ", Height=" + cropHeight);
    
    }
    

    private void updateOriginalCropBounds(double cropX, double cropY, double cropWidth, double cropHeight){
        Bounds imageViewBounds = imageView.getLayoutBounds();
        originalCropBounds = new BoundingBox(
            cropX / imageViewBounds.getWidth(),
            cropY / imageViewBounds.getHeight(),
            cropWidth / imageViewBounds.getWidth(),
            cropHeight / imageViewBounds.getHeight()
        );
        updateCropRectangle();
    }

    private void setupTextFieldListeners() {
        // Update the actualCropRectangle's dimensions when the text fields change
        xField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double doubleValue = Double.parseDouble(newValue);
                actualCropRectangle.setX(doubleValue);
            } catch (NumberFormatException ignored) {}
        });
    
        yField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double doubleValue = Double.parseDouble(newValue);
                actualCropRectangle.setY(doubleValue);
            } catch (NumberFormatException ignored) {}
        });
    
        widthField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double doubleValue = Double.parseDouble(newValue);
                actualCropRectangle.setWidth(doubleValue);
            } catch (NumberFormatException ignored) {}
        });
    
        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double doubleValue = Double.parseDouble(newValue);
                actualCropRectangle.setHeight(doubleValue);
            } catch (NumberFormatException ignored) {}
        });
    }

    private void updateCropRectangle() {
        // Calculate the new position and dimensions based on the ImageView's new size
        double newX = originalCropBounds.getMinX() * imageView.getBoundsInParent().getWidth();
        double newY = originalCropBounds.getMinY() * imageView.getBoundsInParent().getHeight();
        double newWidth = originalCropBounds.getWidth() * imageView.getBoundsInParent().getWidth();
        double newHeight = originalCropBounds.getHeight() * imageView.getBoundsInParent().getHeight();
    
        // Update the crop rectangle
        cropRectangle.setX(newX);
        cropRectangle.setY(newY);
        cropRectangle.setWidth(newWidth);
        cropRectangle.setHeight(newHeight);
    
        // update visibleCropRectangle bounds
        visibleCropRectangle.setX(cropRectangle.getX() + calculateHalfDifferenceWidth());
        visibleCropRectangle.setY(cropRectangle.getY() + calculateHalfDifferenceHeight());
        visibleCropRectangle.setWidth(cropRectangle.getWidth());
        visibleCropRectangle.setHeight(cropRectangle.getHeight());
        visibleCropRectangle.setVisible(true);
    
        // Update the actualCropRectangle according to the original size of the image
        double actualX = originalCropBounds.getMinX() * originalImageWidth;
        double actualY = originalCropBounds.getMinY() * originalImageHeight;
        double actualWidth = originalCropBounds.getWidth() * originalImageWidth;
        double actualHeight = originalCropBounds.getHeight() * originalImageHeight;
    
        actualCropRectangle.setX(actualX);
        actualCropRectangle.setY(actualY);
        actualCropRectangle.setWidth(actualWidth);
        actualCropRectangle.setHeight(actualHeight);
    }
    
    
    /*
     * Cropping rectangle dragging
     */

     private void resizeFromNW(MouseEvent event) {
        double newX = event.getX() - calculateHalfDifferenceWidth();
        double newY = event.getY() - calculateHalfDifferenceHeight();
        double newWidth = cropRectangle.getX() + cropRectangle.getWidth() - newX;
        double newHeight = cropRectangle.getY() + cropRectangle.getHeight() - newY;
    
        if (newWidth > 0 && newHeight > 0) {
            cropRectangle.setX(newX);
            cropRectangle.setY(newY);
            cropRectangle.setWidth(newWidth);
            cropRectangle.setHeight(newHeight);
        }
        updateVisibleCropRectangle();
    }
    
    private void resizeFromNE(MouseEvent event) {
        double newY = event.getY() - calculateHalfDifferenceHeight();
        double newWidth = event.getX() - cropRectangle.getX() - calculateHalfDifferenceWidth();
        double newHeight = cropRectangle.getY() + cropRectangle.getHeight() - newY;
    
        if (newWidth > 0 && newHeight > 0) {
            cropRectangle.setY(newY);
            cropRectangle.setWidth(newWidth);
            cropRectangle.setHeight(newHeight);
        }
        updateVisibleCropRectangle();
    }
    
    private void resizeFromSE(MouseEvent event) {
        double newWidth = event.getX() - cropRectangle.getX() - calculateHalfDifferenceWidth();
        double newHeight = event.getY() - cropRectangle.getY() - calculateHalfDifferenceHeight();
    
        if (newWidth > 0 && newHeight > 0) {
            cropRectangle.setWidth(newWidth);
            cropRectangle.setHeight(newHeight);
        }
        updateVisibleCropRectangle();
    }
    
    private void resizeFromSW(MouseEvent event) {
        double newX = event.getX() - calculateHalfDifferenceWidth();
        double newWidth = cropRectangle.getX() + cropRectangle.getWidth() - newX;
        double newHeight = event.getY() - cropRectangle.getY() - calculateHalfDifferenceHeight();
    
        if (newWidth > 0 && newHeight > 0) {
            cropRectangle.setX(newX);
            cropRectangle.setWidth(newWidth);
            cropRectangle.setHeight(newHeight);
        }

        updateVisibleCropRectangle();
    }
    
    private void updateVisibleCropRectangle() {
        visibleCropRectangle.setX(cropRectangle.getX() + calculateHalfDifferenceWidth());
        visibleCropRectangle.setY(cropRectangle.getY() + calculateHalfDifferenceHeight());
        visibleCropRectangle.setWidth(cropRectangle.getWidth());
        visibleCropRectangle.setHeight(cropRectangle.getHeight());
    }
    
    
}
