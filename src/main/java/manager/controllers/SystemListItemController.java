package manager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SystemListItemController {

    @FXML
    private ImageView imageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subTitle;

    public void setItem(String systemName, Integer totalTitles) {
        // Set the item data to the corresponding controls
        titleLabel.setText(systemName);

        if(totalTitles < 0){
            subTitle.setText("Unknown");
        }
        else{
            subTitle.setText(totalTitles.toString());
        }
        
        // You can customize the image based on the item data as well
        imageView.setImage(new Image(getClass().getResourceAsStream("/images/generic.png")));
        
    }
}
