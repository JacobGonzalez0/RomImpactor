package manager.controllers.actionWizard;

import java.io.File;
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
import javafx.util.Pair;
import manager.elements.CoverResultCell;
import manager.elements.GameResultCell;
import manager.interfaces.SearchProvider;
import manager.models.CoverSearchResult;
import manager.models.GameSearchResult;
import manager.models.SystemListItem;
import manager.services.ImageService;
import manager.services.searchproviders.CoverArtProjectUtil;
import manager.services.searchproviders.IGDBUtil;
import manager.elements.GameResultCell;

public class SearchCoverPanelController {

    @FXML
    private AnchorPane wizardPane;

    @FXML
    private StackPane imagePane;

    @FXML
    private ListView<CoverSearchResult> CoverSearchResults;

    @FXML
    private ImageView imagePreview;

    @FXML
    private Label coverNameLabel;

    private SearchProvider searchProvider;

    private RomActionWizard parentController;

    // Add your controller logic here
    @FXML
    public void initialize() {
    }

    public void receiveQuery(Pair<SearchProvider, GameSearchResult> query) throws IOException{
        SearchProvider searchProvider = query.getKey();
        GameSearchResult gameQuery = query.getValue();


        // Create an ObservableList to hold the data
        ObservableList<CoverSearchResult> coverList = FXCollections.observableArrayList();
                        
        List<CoverSearchResult> coverItems = searchProvider.retriveCovers(gameQuery);

        for(CoverSearchResult i : coverItems){
            coverList.add(i);
        }

        // Set the custom cell factory for the ListView
        CoverSearchResults.setCellFactory(listView -> new CoverResultCell());

        CoverSearchResults.setItems(coverList);

        if (!coverList.isEmpty()) {
            CoverSearchResults.getSelectionModel().selectFirst();
            updateImagePreview(CoverSearchResults.getSelectionModel().getSelectedItem());
            handleGameListViewClick(null);
        }
    }

    @FXML
    public void handleGameListViewClick(MouseEvent event){
        // Get the selected item from the ListView
        CoverSearchResult selectedItem = CoverSearchResults.getSelectionModel().getSelectedItem();

        try {
            updateImagePreview(selectedItem);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Select first item and populate

    }

    public void updateImagePreview(CoverSearchResult cover) throws IOException {

        coverNameLabel.setText("Name: " + cover.getName());
    
        if(cover.getImage() == null ){
            imagePreview.setImage(new Image(getClass().getResourceAsStream("/images/noimage.png")));
        }else{
            Image image = ImageService.convertToFxImage(cover.getImage());
            imagePreview.setImage(image);
    
            File tempFile = ImageService.createTempFileFromBufferedImage(cover.getImage(), "png");
            parentController.setSelectedImageFile(tempFile);
        }
    }
    

    public Pair<SearchProvider, CoverSearchResult> sendQuery() {
        CoverSearchResult selectedResult = CoverSearchResults.getSelectionModel().getSelectedItem();
        return new Pair<>(searchProvider, selectedResult);
    }
    

    public void setParentController(RomActionWizard controller){
        parentController = controller;
    }
}
