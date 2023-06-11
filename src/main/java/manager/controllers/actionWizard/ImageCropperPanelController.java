package manager.controllers.actionWizard;

import java.io.File;

import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
}

class RubberBandSelection {

    private Rectangle rect;
    private double ratioX;
    private double ratioY;
    private double offsetX;  // adjust these values as necessary
    private double offsetY;

    public RubberBandSelection(AnchorPane anchorPane, ImageView imageView) {
        rect = new Rectangle(0, 0, 0, 0);
        rect.setStroke(Color.BLUE);
        rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));
        anchorPane.getChildren().add(rect);

        // Offset from the anchor panel its contained in
        offsetX = 58;
        offsetY = -86;

        imageView.imageProperty().addListener((obs, oldImage, newImage) -> {
            if (newImage != null) {
                ratioX = newImage.getWidth() / imageView.getBoundsInLocal().getWidth();
                ratioY = newImage.getHeight() / imageView.getBoundsInLocal().getHeight();
            }
        });

        anchorPane.setOnMousePressed(event -> {
            Point2D imageViewLoc = imageView.sceneToLocal(event.getSceneX() + offsetX, event.getSceneY() + offsetY);
            rect.setX(imageViewLoc.getX());
            rect.setY(imageViewLoc.getY());
            rect.setWidth(0);
            rect.setHeight(0);
        });
        
        anchorPane.setOnMouseDragged(event -> {
            Point2D imageViewLoc = imageView.sceneToLocal(event.getSceneX() + offsetX, event.getSceneY() + offsetY);
            rect.setWidth(Math.abs(imageViewLoc.getX() - rect.getX()));
            rect.setHeight(Math.abs(imageViewLoc.getY() - rect.getY()));
            rect.setX(Math.min(imageViewLoc.getX(), rect.getX()));
            rect.setY(Math.min(imageViewLoc.getY(), rect.getY()));
        });
        
    }

    public Bounds getBounds() {
        return new BoundingBox(rect.getX() * ratioX, rect.getY() * ratioY, rect.getWidth() * ratioX, rect.getHeight() * ratioY);
    }
}
