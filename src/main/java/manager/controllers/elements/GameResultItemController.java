package manager.controllers.elements;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import manager.models.GameSearchResult;
import manager.services.ImageService;

public class GameResultItemController {
    @FXML
    private ImageView imageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Label subTitle;

    public void setItem(GameSearchResult game) {
        // Set the item data to the corresponding controls
        titleLabel.setText(game.getName());

        // if(game.getReleaseDate().isEmpty()){
        //     subTitle.setText("None");
        // }else{
        //     subTitle.setText(game.getReleaseDate());
        // }

        if(game.getCoverImage() == null){
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/noimage.png")));
        }else{
            imageView.setImage(ImageService.convertToFxImage(game.getCoverImage()));
        }
       
        
    }

}
