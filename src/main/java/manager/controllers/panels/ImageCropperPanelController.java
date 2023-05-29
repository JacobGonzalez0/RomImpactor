package manager.controllers.panels;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
import javafx.util.converter.IntegerStringConverter;
import manager.services.ImageService;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class ImageCropperPanelController {
    @FXML
    private AnchorPane wizardPane;

    @FXML
    private VBox vbox;

    @FXML
    private StackPane imagePane;
    
    @FXML
    private Button selectAllButton;

    @FXML
    private ImageView imageView;

    @FXML
    private Spinner<Integer> xCordSpinner, yCordSpinner, widthSpinner, heightSpinner;

    private File selectedImageFile; // Stores the selected image file
    private int imageWidth;
    private int imageHeight;

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

    private double initX;
    private double initY;

    @FXML
    public void initialize() {
         // bind the fitWidth and fitHeight properties of the imageView
        // to the width and height of the vbox
        imageView.fitWidthProperty().bind(imagePane.widthProperty());
        imageView.fitHeightProperty().bind(imagePane.heightProperty());

    }

    public void loadImage(File inputFile){
        selectedImageFile = inputFile;
        displaySelectedImage();
        setSelectedImageFile(selectedImageFile);
        setupListeners();
        setupSpinners();
        setupSpinnerListeners();
    }

    /*
     * Init Helpers
     */

    private void updateSpinners() {
        xCordSpinner.getValueFactory().setValue((int) cropRectangle.getX());
        yCordSpinner.getValueFactory().setValue((int) cropRectangle.getY());
        widthSpinner.getValueFactory().setValue((int) cropRectangle.getWidth());
        heightSpinner.getValueFactory().setValue((int) cropRectangle.getHeight());
    }

    private void setupSpinnerListeners() {
        xCordSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            cropRectangle.setX(newValue);
            updateVisibleCropRectangle();
        });
        yCordSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            cropRectangle.setY(newValue);
            updateVisibleCropRectangle();
        });
        widthSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            cropRectangle.setWidth(newValue);
            updateVisibleCropRectangle();
        });
        heightSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            cropRectangle.setHeight(newValue);
            updateVisibleCropRectangle();
        });
    }

    public void setSelectedImageFile(File selectedImageFile) {
        this.selectedImageFile = selectedImageFile;
        try {
            imageWidth = ImageIO.read(selectedImageFile).getWidth();
            imageHeight = ImageIO.read(selectedImageFile).getHeight();
            setupSpinners();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
    }

    private void setupSpinners() {
        configureSpinner(xCordSpinner, 0, 0, imageWidth);
        configureInverseSpinner(yCordSpinner, 0, 0, imageHeight);
        configureSpinner(widthSpinner, 0, 0, imageWidth);
        configureInverseSpinner(heightSpinner, 0, 0, imageHeight);
    }

    private void configureSpinner(Spinner<Integer> spinner, int initialValue, int min, int max) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue);
        spinner.setValueFactory(valueFactory);
    }

    private void configureInverseSpinner(Spinner<Integer> spinner, int initialValue, int min, int max) {
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue) {
                @Override
                public void increment(int steps) {
                    super.decrement(steps);
                }
    
                @Override
                public void decrement(int steps) {
                    super.increment(steps);
                }
            };
        
        spinner.setValueFactory(valueFactory);
    }
    

    private void setupVisibleCropRectangle() {
        visibleCropRectangle = new Rectangle();
        visibleCropRectangle.setStroke(Color.RED);
        visibleCropRectangle.setStrokeWidth(2);
        visibleCropRectangle.setFill(Color.rgb(255, 0, 0, 0.2));
        wizardPane.getChildren().add(visibleCropRectangle);
    }

    private void setupListeners(){
        // initialize the actualCropRectangle
        
        setupVisibleCropRectangle();
        setupCropRectangle();
        actualCropRectangle = new Rectangle();

        // Setup Crop Listeners
        setupResizeListener();
        moveCropHandler();
    }
    
    private void setupResizeListener() {
        imageView.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            updateCropRectangle();
        });
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
     * Helper Methods
     */

    private double calculateHalfDifferenceWidth() {
        double imageViewWidth = imageView.getBoundsInParent().getWidth();
        double vboxWidth = imagePane.getWidth();
        double difference = vboxWidth - imageViewWidth;
        return difference / 2.0;
    }

    private double calculateHalfDifferenceHeight() {
        double imageViewHeight = imageView.getBoundsInParent().getHeight();
        double vboxHeight = imagePane.getHeight();
        double difference = vboxHeight - imageViewHeight;
        return difference / 2.0;
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

    private boolean isWithinImageView(double x, double y, double width, double height) {
        Bounds imageViewBounds = imageView.getBoundsInParent();
        return x >= 0 && y >= 0 && (x + width) <= imageViewBounds.getWidth() && (y + height) <= imageViewBounds.getHeight();
    }
    
    /*
     * Text Field Listeners
     */

    

    /*
     * Click and Drag Crop Events
     */

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

        updateSpinners();
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

        if (isWithinImageView(minX, minY, newWidth, newHeight)) {
            cropRectangle.setX(minX);
            cropRectangle.setY(minY);
            cropRectangle.setWidth(newWidth);
            cropRectangle.setHeight(newHeight);
    
            
        }

        // update visibleCropRectangle bounds
        visibleCropRectangle.setX(cropRectangle.getX() + calculateHalfDifferenceWidth());
        visibleCropRectangle.setY(cropRectangle.getY() + calculateHalfDifferenceHeight());
        visibleCropRectangle.setWidth(cropRectangle.getWidth());
        visibleCropRectangle.setHeight(cropRectangle.getHeight());
        visibleCropRectangle.setVisible(true);

        prevX = currentX;
        prevY = currentY;
        updateSpinners();
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
        updateSpinners();
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

    private void updateCropRectangle() {

        if(originalCropBounds == null){
            return;
        }
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
     * Crop Resize Events
     */

    private void resizeFromNW(MouseEvent event) {
        double newX = event.getX() - calculateHalfDifferenceWidth();
        double newY = event.getY() - calculateHalfDifferenceHeight();
        double newWidth = cropRectangle.getX() + cropRectangle.getWidth() - newX;
        double newHeight = cropRectangle.getY() + cropRectangle.getHeight() - newY;
    
        if (newWidth > 0 && newHeight > 0 && isWithinImageView(newX, newY, newWidth, newHeight)) {
            cropRectangle.setX(newX);
            cropRectangle.setY(newY);
            cropRectangle.setWidth(newWidth);
            cropRectangle.setHeight(newHeight);
            updateOriginalCropBounds(newX, newY, newWidth, newHeight);
        }
        updateVisibleCropRectangle();
    }
    
    private void resizeFromNE(MouseEvent event) {
        double newY = event.getY() - calculateHalfDifferenceHeight();
        double newWidth = event.getX() - cropRectangle.getX() - calculateHalfDifferenceWidth();
        double newHeight = cropRectangle.getY() + cropRectangle.getHeight() - newY;
    
        if (newWidth > 0 && newHeight > 0 && isWithinImageView(cropRectangle.getX(), newY, newWidth, newHeight)) {
            cropRectangle.setY(newY);
            cropRectangle.setWidth(newWidth);
            cropRectangle.setHeight(newHeight);
            updateOriginalCropBounds(cropRectangle.getX(), newY, newWidth, newHeight);
            updateSpinners();
        }
        updateVisibleCropRectangle();
    }
    
    private void resizeFromSE(MouseEvent event) {
        double newWidth = event.getX() - cropRectangle.getX() - calculateHalfDifferenceWidth();
        double newHeight = event.getY() - cropRectangle.getY() - calculateHalfDifferenceHeight();
    
        if (newWidth > 0 && newHeight > 0 && isWithinImageView(cropRectangle.getX(), cropRectangle.getY(), newWidth, newHeight)) {
            cropRectangle.setWidth(newWidth);
            cropRectangle.setHeight(newHeight);
            updateOriginalCropBounds(cropRectangle.getX(), cropRectangle.getY(), newWidth, newHeight);
            updateSpinners();
        }
        updateVisibleCropRectangle();
    }
    
    private void resizeFromSW(MouseEvent event) {
        double newX = event.getX() - calculateHalfDifferenceWidth();
        double newWidth = cropRectangle.getX() + cropRectangle.getWidth() - newX;
        double newHeight = event.getY() - cropRectangle.getY() - calculateHalfDifferenceHeight();
    
        if (newWidth > 0 && newHeight > 0 && isWithinImageView(newX, cropRectangle.getY(), newWidth, newHeight)) {
            cropRectangle.setX(newX);
            cropRectangle.setWidth(newWidth);
            cropRectangle.setHeight(newHeight);
            updateOriginalCropBounds(newX, cropRectangle.getY(), newWidth, newHeight);
            updateSpinners();
        }
        updateVisibleCropRectangle();
    }    
    
    private void updateVisibleCropRectangle() {
        visibleCropRectangle.setX(cropRectangle.getX() + calculateHalfDifferenceWidth());
        visibleCropRectangle.setY(cropRectangle.getY() + calculateHalfDifferenceHeight());
        visibleCropRectangle.setWidth(cropRectangle.getWidth());
        visibleCropRectangle.setHeight(cropRectangle.getHeight());
        updateSpinners();
    }

    /*
     * Drag Cropping Rectangle Events
     */

    private void move(MouseEvent event) {
        // Calculate the difference between the initial click and the current event
        double deltaX = event.getX() - initX;
        double deltaY = event.getY() - initY;
    
        // Calculate the new position of the crop rectangle
        double newX = cropRectangle.getX() + deltaX;
        double newY = cropRectangle.getY() + deltaY;
    
        // Calculate the maximum allowed position based on the image boundaries
        double maxPosX = imageView.getBoundsInParent().getWidth() - cropRectangle.getWidth();
        double maxPosY = imageView.getBoundsInParent().getHeight() - cropRectangle.getHeight();
    
        // Check if the new position exceeds the image boundaries
        if (newX >= 0 && newY >= 0 && newX <= maxPosX && newY <= maxPosY) {
            // Update the position of the crop rectangle
            cropRectangle.setX(newX);
            cropRectangle.setY(newY);
    
            // Update the position of the visible crop rectangle
            visibleCropRectangle.setX(visibleCropRectangle.getX() + deltaX);
            visibleCropRectangle.setY(visibleCropRectangle.getY() + deltaY);
    
            // Update the initial positions
            initX = event.getX();
            initY = event.getY();
    
            updateOriginalCropBounds(newX, newY, cropRectangle.getWidth(), cropRectangle.getHeight());
            updateSpinners();
        }
        // Update the visible rectangle
        updateVisibleCropRectangle();
    }
     

    private void moveCropHandler(){
        visibleCropRectangle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Record the initial positions during a click
                initX = event.getX();
                initY = event.getY();
            }
        });
        
        visibleCropRectangle.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                move(event);
            }
        });
        
        visibleCropRectangle.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                
            }
        });
    }
}
