package manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import manager.controllers.MainWindowController;

/**
 * Hello world!
 *
 */
public class App extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create a transparent stage with no default decorations
        primaryStage.initStyle(StageStyle.TRANSPARENT);
    
        // Create a custom shape for the stage
        Rectangle windowShape = new Rectangle(910, 600);
        windowShape.setArcWidth(20);
        windowShape.setArcHeight(20);
        windowShape.setFill(Color.WHITE);
    
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
        Parent root = loader.load();
    
        // Retrieve the controller from FXMLLoader
        MainWindowController controller = loader.getController();

        // Set the primaryStage
        controller.setPrimaryStage(primaryStage);
    
        // Customize the appearance of the stage
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        root.setClip(windowShape);
    
        // Set the scene for the stage
        primaryStage.setScene(scene);
        primaryStage.show();
    
    }
    
    public static void main( String[] args )
    {
        launch(args);
    }
}
