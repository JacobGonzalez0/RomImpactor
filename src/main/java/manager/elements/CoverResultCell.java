package manager.elements;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import manager.controllers.elements.CoverResultItemController;
import manager.controllers.elements.GameResultItemController;
import manager.models.CoverSearchResult;
import manager.models.GameSearchResult;


public class CoverResultCell extends ListCell<CoverSearchResult> {

    public CoverResultCell() {
        // Set the preferred width for the cell
        setPrefWidth(120);
    }

    @Override
    protected void updateItem(CoverSearchResult cover, boolean empty) {
        super.updateItem(cover, empty);

        if (empty || cover == null) {
            // Clear cell content if item is null or empty
            setGraphic(null);
            setText(null);
        } else {
            // Load the cell layout from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/elements/CoverResultCell.fxml"));
            try {
                HBox cellLayout = loader.load();
                CoverResultItemController controller = loader.getController();
                controller.setItem(cover);
                setGraphic(cellLayout);
                setText(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
