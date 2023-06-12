package manager.controllers.elements;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import manager.models.Rom;
import manager.services.ImageService;

public class RomListItemController {

    @FXML
    private ImageView imageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subTitle;

    public void setItem(Rom rom) {
        // Set the item data to the corresponding controls
        titleLabel.setText(rom.getName());

        if(rom.getReleaseDate().isEmpty()){
            subTitle.setText("None");
        }else{
            subTitle.setText(rom.getReleaseDate());
        }

        if(rom.getImageFile() == null || !rom.getImageFile().exists()){
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/noimage.png")));
        }else{
            imageView.setImage(ImageService.convertToFxImage(rom.getImageAsBufferedImage()));
        }
       
        
    }
}
