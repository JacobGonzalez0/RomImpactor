package manager.controllers.actionWizard;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import manager.elements.GameResultCell;
import manager.interfaces.SearchProvider;
import manager.models.GameSearchResult;
import manager.models.SystemListItem;
import manager.services.ImageService;
import manager.services.searchproviders.CoverArtProjectUtil;
import manager.elements.GameResultCell;

public class SearchGamePanelController {

    @FXML
    private AnchorPane wizardPane;

    @FXML
    private StackPane imagePane;

    @FXML
    private ListView<GameSearchResult> GameSearchResults;

    @FXML
    private ImageView imagePreview;

    @FXML
    private Label gameNameLabel;

    private SearchProvider searchProvider;

    // Add your controller logic here
    @FXML
    public void initialize() {
        searchProvider = new CoverArtProjectUtil();
    }

    public void receiveQuery(String query) throws IOException{
        // Create an ObservableList to hold the data
        ObservableList<GameSearchResult> gameList = FXCollections.observableArrayList();
                        
        List<GameSearchResult> gameItems = searchProvider.searchGames(query);

        for(GameSearchResult i : gameItems){
            gameList.add(i);
        }

        // Set the custom cell factory for the ListView
        GameSearchResults.setCellFactory(listView -> new GameResultCell());

        GameSearchResults.setItems(gameList);

        if (!gameList.isEmpty()) {
            GameSearchResults.getSelectionModel().selectFirst();
            updateImagePreview(GameSearchResults.getSelectionModel().getSelectedItem());
            handleGameListViewClick(null);
        }
    }

    @FXML
    public void handleGameListViewClick(MouseEvent event){
        // Get the selected item from the ListView
        GameSearchResult selectedItem = GameSearchResults.getSelectionModel().getSelectedItem();

        updateImagePreview(selectedItem);
        // Select first item and populate

    }

    public void updateImagePreview(GameSearchResult game){

        gameNameLabel.setText("Name: " + game.getName());

        if(game.getCoverImage() == null ){
            imagePreview.setImage(new Image(getClass().getResourceAsStream("/images/noimage.png")));
        }else{
            Image image = ImageService.convertToFxImage(game.getCoverImage());
            imagePreview.setImage(image);

        }
    }

    public GameSearchResult sendQuery(){
        return GameSearchResults.getSelectionModel().getSelectedItem();
    }
}
