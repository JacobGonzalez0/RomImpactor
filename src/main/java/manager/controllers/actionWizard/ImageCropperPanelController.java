package manager.controllers.actionWizard;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import manager.services.ImageService;

public class ImageCropperPanelController {

    @FXML
    private AnchorPane wizardPane;

    @FXML
    private StackPane imagePane;

    @FXML
    private ImageView imageView;

    @FXML
    private Spinner<Integer> xCordSpinner;

    @FXML
    private Spinner<Integer> yCordSpinner;

    @FXML
    private Spinner<Integer> widthSpinner;

    @FXML
    private Spinner<Integer> heightSpinner;

    private RubberBandSelection rubberBandSelection;

    public void initialize() {
        rubberBandSelection = new RubberBandSelection(wizardPane, imageView); // pass AnchorPane instead of StackPane
    }

    public void loadImage(File file) {
        Image image = new Image(file.toURI().toString());
        imageView.setImage(image);
    }

    public BufferedImage cropImage() {
        Bounds bounds = rubberBandSelection.getBounds();
    
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
    
        WritableImage wi = new WritableImage((int) bounds.getWidth(), (int) bounds.getHeight());
        WritableImage croppedImage = imageView.snapshot(parameters, wi);
    
        // Use the convertToBufferedImage method to convert the WritableImage to BufferedImage
        BufferedImage bufferedImage = ImageService.convertToBufferedImage(croppedImage, false);
    
        return bufferedImage;
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


    public RubberBandSelection(AnchorPane anchorPane, ImageView imageView) {
        rect = new Rectangle(0, 0, 0, 0);
        rect.setStroke(Color.BLUE);
        rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));
        anchorPane.getChildren().add(rect);

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
        anchorPane.getChildren().addAll(nwHandle, neHandle, swHandle, seHandle);

        

        imageView.imageProperty().addListener((obs, oldImage, newImage) -> {
            if (newImage != null) {
                ratioX = newImage.getWidth() / imageView.getBoundsInLocal().getWidth();
                ratioY = newImage.getHeight() / imageView.getBoundsInLocal().getHeight();
            }
        });
    
        imageView.boundsInParentProperty().addListener((obs, oldBounds, newBounds) -> {
            Point2D topLeft = imageView.localToScene(newBounds.getMinX(), newBounds.getMinY());
            offsetX = (topLeft.getX() - anchorPane.getLayoutX()) /2;
            offsetY = (topLeft.getY() - anchorPane.getLayoutY()) /2;
        });

        anchorPane.setOnMousePressed(event -> {
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

            previousXClick = event.getX();
            previousYClick = event.getY();
        });

        anchorPane.setOnMouseDragged(event -> {
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
        
            previousXClick = imageViewLoc.getX();
            previousYClick = imageViewLoc.getY();
        });
        
        

        anchorPane.setOnMouseReleased(event -> {
            dragging = false;  // stop dragging when the mouse is released
            Node target = (Node) event.getTarget();
            if (target == nwHandle || target == neHandle || target == swHandle || target == seHandle) {
                target.setCursor(Cursor.DEFAULT);
            }

            previousXClick = event.getX();
            previousYClick = event.getY();
        });

        
        
    }

    public Bounds getBounds() {
        return new BoundingBox(rect.getX() * ratioX, rect.getY() * ratioY, rect.getWidth() * ratioX, rect.getHeight() * ratioY);
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

