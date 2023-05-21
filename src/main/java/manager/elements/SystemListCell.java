package manager.elements;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import manager.App;
import manager.controllers.SystemListItemController;
import manager.models.SystemListItem;

import java.io.IOException;

public class SystemListCell extends ListCell<SystemListItem> {

    public SystemListCell() {
        // Set the preferred width for the cell
        setPrefWidth(120);
    }

    @Override
    protected void updateItem(SystemListItem system, boolean empty) {
        super.updateItem(system, empty);

        if (empty || system == null) {
            // Clear cell content if item is null or empty
            setGraphic(null);
            setText(null);
        } else {
            // Load the cell layout from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SystemItemCell.fxml"));
            try {
                HBox cellLayout = loader.load();
                SystemListItemController controller = loader.getController();
                controller.setItem(system.getSystemName(), system.getRomCount());
                setGraphic(cellLayout);
                setText(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
