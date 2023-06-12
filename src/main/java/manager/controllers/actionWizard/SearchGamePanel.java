package manager.controllers.actionWizard;

import java.io.IOException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import manager.elements.GameResultCell;
import manager.interfaces.SearchProvider;
import manager.models.GameSearchResult;
import manager.models.SystemListItem;
import manager.services.searchproviders.CoverArtProjectUtil;
import manager.elements.GameResultCell;

public class SearchGamePanel {

    @FXML
    private AnchorPane wizardPane;

    @FXML
    private ListView<GameSearchResult> GameSearchResults;

    @FXML
    private ImageView image;

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
        ObservableList<GameSearchResult> systemList = FXCollections.observableArrayList();
                        
        List<GameSearchResult> gameItems = searchProvider.searchGames(query);

        for(GameSearchResult i : gameItems){
            systemList.add(i);
        }

        // Set the custom cell factory for the ListView
        GameSearchResults.setCellFactory(listView -> new GameResultCell());
    }


}
