package manager.controllers.actionWizard;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import manager.services.ImageService;

public class ImageCropperPanelController {

    @FXML
    private AnchorPane wizardPane;

    @FXML
    private ImageView imageView;

    @FXML
    private Spinner<Integer> xCordSpinner, yCordSpinner, widthSpinner, heightSpinner;

    private RubberBandSelection rubberBandSelection;

    private double ratioX;

    private double ratioY;

    private double offsetX;

    private double offsetY;

    public void initialize() {
        rubberBandSelection = new RubberBandSelection(wizardPane, imageView, this); // pass AnchorPane instead of StackPane
    }

    public void loadImage(File file) {
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);


        if (image.getWidth() > image.getHeight()) {
            // Bind the ImageView's width to the width of the AnchorPane
            imageView.fitWidthProperty().bind(wizardPane.widthProperty());

            // Bind the ImageView's height to the height of the AnchorPane minus 42
            imageView.fitHeightProperty().bind(wizardPane.heightProperty().subtract(42));
        } else {
             // Bind the ImageView's size to the smaller of the AnchorPane's width and height
            DoubleBinding size = (DoubleBinding) Bindings.min(wizardPane.widthProperty(), wizardPane.heightProperty().subtract(42));
            imageView.fitWidthProperty().bind(size);
            imageView.fitHeightProperty().bind(size);
        }

        // Add listeners to the AnchorPane's and ImageView's size properties
        wizardPane.widthProperty().addListener((obs, oldVal, newVal) -> updateImageViewLayout());
        wizardPane.heightProperty().addListener((obs, oldVal, newVal) -> updateImageViewLayout());
        imageView.fitWidthProperty().addListener((obs, oldVal, newVal) -> updateImageViewLayout());
        imageView.fitHeightProperty().addListener((obs, oldVal, newVal) -> updateImageViewLayout());
        updateImageViewLayout();

        setupSpinners();
        setupSpinnerListeners();

        imageView.imageProperty().addListener((obs, oldImage, newImage) -> {
            if (newImage != null) {
                ratioX = newImage.getWidth() / imageView.getBoundsInLocal().getWidth();
                ratioY = newImage.getHeight() / imageView.getBoundsInLocal().getHeight();
            }
        });

        imageView.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
            Point2D topLeft = imageView.localToScene(newBounds.getMinX(), newBounds.getMinY());
            offsetX = (topLeft.getX() - wizardPane.getLayoutX()) /2;
            offsetY = (topLeft.getY() - wizardPane.getLayoutY()) /2;
        });
    }

    private void updateImageViewLayout() {
        // Center the ImageView within the AnchorPane
        imageView.setLayoutX((wizardPane.getWidth() - imageView.getFitWidth()) / 2);
        imageView.setLayoutY(((wizardPane.getHeight() - 42) - imageView.getFitHeight()) / 2);
    }

    public BufferedImage cropImage() {
        Image croppedImage = rubberBandSelection.cropImage(imageView.getImage(), rubberBandSelection.getRectangle());
    
        // Use the convertToBufferedImage method to convert the WritableImage to BufferedImage
        BufferedImage bufferedImage = ImageService.convertToBufferedImage(croppedImage, false);
    
        return bufferedImage;
    }
    
    void updateSpinners() {
        Rectangle rect = rubberBandSelection.getRectangle();
        xCordSpinner.getValueFactory().setValue((int) (rect.getX() ));
        yCordSpinner.getValueFactory().setValue((int) (rect.getY() )) ;
        widthSpinner.getValueFactory().setValue((int) rect.getWidth());
        heightSpinner.getValueFactory().setValue((int) rect.getHeight());
    }

    private void setupSpinners() {
        int imageWidth = (int) imageView.imageProperty().getValue().getWidth();
        int imageHeight = (int) imageView.imageProperty().getValue().getHeight();
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

    private void setupSpinnerListeners() {
        Rectangle rect = rubberBandSelection.getRectangle();
        xCordSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            rect.setX(newValue );
            rubberBandSelection.setRectangle(rect);
        });
        yCordSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            rect.setY(newValue );
            rubberBandSelection.setRectangle(rect);
        });
        widthSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            rect.setWidth(newValue);
            rubberBandSelection.setRectangle(rect);
        });
        heightSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            rect.setHeight(newValue);
            rubberBandSelection.setRectangle(rect);
        });
    }
}

class RubberBandSelection {

    private Rectangle rect;
    private double ratioX;
    private double ratioY;
    private double offsetX;  // adjust these values as necessary
    private double offsetY;
    private double originalX;
    private double originalY;
    private boolean dragging;
    private double previousXClick;
    private double previousYClick;
    private Rectangle nwHandle, neHandle, swHandle, seHandle;  // the four handles
    private static final double HANDLE_SIZE = 10.0;
    private AnchorPane pane;
    private ImageView imageView;


    public RubberBandSelection(AnchorPane pane, ImageView imageView, ImageCropperPanelController parent ) {
        rect = new Rectangle(0, 0, 0, 0);
        rect.setStroke(Color.BLUE);
        rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));
        this.pane = pane;
        this.pane.getChildren().add(rect);

        // Offset from the anchor panel its contained in
        dragging = false;

        nwHandle = new Rectangle(HANDLE_SIZE, HANDLE_SIZE);
        neHandle = new Rectangle(HANDLE_SIZE, HANDLE_SIZE);
        swHandle = new Rectangle(HANDLE_SIZE, HANDLE_SIZE);
        seHandle = new Rectangle(HANDLE_SIZE, HANDLE_SIZE);

        // Set the handles' colors so they're visible against the selection rectangle
        nwHandle.setFill(Color.RED);
        neHandle.setFill(Color.RED);
        swHandle.setFill(Color.RED);
        seHandle.setFill(Color.RED);

        // Add the handles to the anchorPane
        pane.getChildren().addAll(nwHandle, neHandle, swHandle, seHandle);

        

        this.imageView = imageView;
        this.imageView.imageProperty().addListener((obs, oldImage, newImage) -> {
            if (newImage != null) {
                ratioX = newImage.getWidth() / imageView.getBoundsInLocal().getWidth();
                ratioY = newImage.getHeight() / imageView.getBoundsInLocal().getHeight();
            }
        });
    
        imageView.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
            Point2D topLeft = imageView.localToScene(newBounds.getMinX(), newBounds.getMinY());
            offsetX = (topLeft.getX() - pane.getLayoutX()) /2;
            offsetY = (topLeft.getY() - pane.getLayoutY()) /2;
        });

        pane.setOnMousePressed(event -> {
            if(previousXClick == 0 || previousYClick == 0){
                previousXClick = event.getX();
                previousYClick = event.getY();
            }

            Point2D mousePoint = new Point2D(event.getX(), event.getY());
            if (rect.contains(mousePoint)) {
                originalX = rect.getX() - mousePoint.getX();
                originalY = rect.getY() - mousePoint.getY();
                dragging = true;
            } else {
                Point2D imageViewLoc = imageView.sceneToLocal(event.getSceneX() + offsetX, event.getSceneY() + offsetY);
                rect.setX(imageViewLoc.getX());
                rect.setY(imageViewLoc.getY());
                rect.setWidth(0);
                rect.setHeight(0);
            }
            adjustHandles();

            Node target = (Node) event.getTarget();
            if (target == nwHandle) {
                target.setCursor(Cursor.NW_RESIZE);
            } else if (target == neHandle) {
                target.setCursor(Cursor.NE_RESIZE);
            } else if (target == swHandle) {
                target.setCursor(Cursor.SW_RESIZE);
            } else if (target == seHandle) {
                target.setCursor(Cursor.SE_RESIZE);
            }

            parent.updateSpinners();

            previousXClick = event.getX();
            previousYClick = event.getY();
        });

        pane.setOnMouseDragged(event -> {
            if(previousXClick == 0 || previousYClick == 0){
                previousXClick = event.getX();
                previousYClick = event.getY();
            }
        
            Point2D imageViewLoc = imageView.sceneToLocal(event.getSceneX() + offsetX, event.getSceneY() + offsetY);
        
            Node target = (Node) event.getTarget();
            if (target == nwHandle || target == neHandle || target == swHandle || target == seHandle) {
                double newX = imageViewLoc.getX();
                double newY = imageViewLoc.getY();
                double deltaX = newX - previousXClick;
                double deltaY = newY - previousYClick;

                if (target == nwHandle) {
                    System.out.println("nw");
                    double newWidth = rect.getWidth() - deltaX;
                    double newHeight = rect.getHeight() - deltaY;
                    
                    if (newWidth > 0 && newHeight > 0) {
                        rect.setX(rect.getX() + deltaX);
                        rect.setY(rect.getY() + deltaY);
                        rect.setWidth(newWidth);
                        rect.setHeight(newHeight);
                    }
                } else if (target == neHandle) {
                    System.out.println("ne");
                    double newWidth = rect.getWidth() + deltaX;
                    double newHeight = rect.getHeight() - deltaY;
                    if (newWidth > 0 && newHeight > 0) {
                        rect.setY(rect.getY() + deltaY);
                        rect.setWidth(newWidth);
                        rect.setHeight(newHeight);
                    }
                } else if (target == swHandle) {
                    double newWidth = rect.getWidth() - deltaX;
                    double newHeight = rect.getHeight() + deltaY;
                    if (newWidth > 0 && newHeight > 0) {
                        rect.setX(rect.getX() + deltaX);
                        rect.setWidth(newWidth);
                        rect.setHeight(newHeight);
                    }
                } else if (target == seHandle) {
                    double newWidth = rect.getWidth() + deltaX;
                    double newHeight = rect.getHeight() + deltaY;
                    if (newWidth > 0 && newHeight > 0) {
                        rect.setWidth(newWidth);
                        rect.setHeight(newHeight);
                    }
                }
                
            }
        
            if (dragging && !(target == nwHandle || target == neHandle || target == swHandle || target == seHandle)) {
                rect.setX(imageViewLoc.getX() + originalX);
                rect.setY(imageViewLoc.getY() + originalY);
            } else if(!dragging && !(target == nwHandle || target == neHandle || target == swHandle || target == seHandle)) {
                rect.setWidth(Math.abs(imageViewLoc.getX() - rect.getX()));
                rect.setHeight(Math.abs(imageViewLoc.getY() - rect.getY()));
                rect.setX(Math.min(imageViewLoc.getX(), rect.getX()));
                rect.setY(Math.min(imageViewLoc.getY(), rect.getY()));
            }
            adjustHandles();
            parent.updateSpinners();
        
            previousXClick = imageViewLoc.getX();
            previousYClick = imageViewLoc.getY();
        });   

        pane.setOnMouseReleased(event -> {
            dragging = false;  // stop dragging when the mouse is released
            Node target = (Node) event.getTarget();
            if (target == nwHandle || target == neHandle || target == swHandle || target == seHandle) {
                target.setCursor(Cursor.DEFAULT);
            }
            parent.updateSpinners();

            previousXClick = event.getX();
            previousYClick = event.getY();
        });

    }

    public Rectangle getRectangle(){
        return rect;
    }

    public void setRectangle(Rectangle rect){
        this.rect = rect;
        adjustHandles();
    }

    public Image cropImage(Image image, Rectangle rect) {
        PixelReader reader = image.getPixelReader();

        
        ratioX = image.getWidth() / imageView.getBoundsInLocal().getWidth();
        ratioY = image.getHeight() / imageView.getBoundsInLocal().getHeight();
        
       

        int x = (int) (((rect.getX() - offsetX) * ratioX) ) ;
        int y = (int) (((rect.getY() ) * ratioY) );
        int width = (int) (rect.getWidth() * ratioX);
        int height = (int) (rect.getHeight() * ratioY);
    
        System.out.println(x + " " + y + " ");
        System.out.println(width + " " + height + " ");
        WritableImage newImage = new WritableImage(reader, x , y, width, height);
        return newImage;
    }
    

    private void adjustHandles() {
      
        // Adjust the position of the handles based on the position of the main rectangle
        nwHandle.setX(rect.getX());
        nwHandle.setY(rect.getY());
       
        neHandle.setX(rect.getX() + rect.getWidth() - HANDLE_SIZE);
        neHandle.setY(rect.getY());

        swHandle.setX(rect.getX());
        swHandle.setY(rect.getY() + rect.getHeight() - HANDLE_SIZE);

        seHandle.setX(rect.getX() + rect.getWidth() - HANDLE_SIZE);
        seHandle.setY(rect.getY() + rect.getHeight() - HANDLE_SIZE);
    }

   
    
}

