package manager.elements;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import manager.controllers.elements.GameResultItemController;
import manager.models.GameSearchResult;


public class GameResultCell extends ListCell<GameSearchResult> {

    public GameResultCell() {
        // Set the preferred width for the cell
        setPrefWidth(120);
    }

    @Override
    protected void updateItem(GameSearchResult game, boolean empty) {
        super.updateItem(game, empty);

        if (empty || game == null) {
            // Clear cell content if item is null or empty
            setGraphic(null);
            setText(null);
        } else {
            // Load the cell layout from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/elements/RomItemCell.fxml"));
            try {
                HBox cellLayout = loader.load();
                GameResultItemController controller = loader.getController();
                controller.setItem(game);
                setGraphic(cellLayout);
                setText(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
