package manager.elements;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import manager.controllers.RomListItemController;
import manager.models.Rom;

public class RomListCell extends ListCell<Rom> {

    public RomListCell() {
        // Set the preferred width for the cell
        setPrefWidth(120);
    }

    @Override
    protected void updateItem(Rom rom, boolean empty) {
        super.updateItem(rom, empty);

        if (empty || rom == null) {
            // Clear cell content if item is null or empty
            setGraphic(null);
            setText(null);
        } else {
            // Load the cell layout from the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RomItemCell.fxml"));
            try {
                HBox cellLayout = loader.load();
                RomListItemController controller = loader.getController();
                controller.setItem(rom);
                setGraphic(cellLayout);
                setText(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
