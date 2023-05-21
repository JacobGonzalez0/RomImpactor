package manager.elements;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import manager.App;
import manager.controllers.SystemListItemController;

import java.io.IOException;

public class SystemListCell extends ListCell<String> {

    public SystemListCell() {
        // Set the preferred width for the cell
        setPrefWidth(120);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            // Clear cell content if item is null or empty
            setGraphic(null);
            setText(null);
        } else {
            // Load the cell layout from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manager/elements/SystemItemCell.fxml"));
            try {
                HBox cellLayout = loader.load();
                SystemListItemController controller = loader.getController();
                controller.setItem(item);
                setGraphic(cellLayout);
                setText(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
