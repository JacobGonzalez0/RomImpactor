package manager.controllers.elements;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import manager.services.ImageService;

import java.awt.image.BufferedImage;

public class SystemListItemController {

    @FXML
    private ImageView imageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subTitle;

    public void setItem(String systemName, BufferedImage image, Integer totalTitles) {
        // Set the item data to the corresponding controls
        titleLabel.setText(systemName);

        if(totalTitles < 0){
            subTitle.setText("Unknown");
        }
        else{
            subTitle.setText(totalTitles.toString());
        }
        
        // You can customize the image based on the item data as well
        imageView.setImage(ImageService.convertToFxImage(image));
        
    }
}
