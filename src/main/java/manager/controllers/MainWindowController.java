package manager.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser.ExtensionFilter;
import manager.controllers.actionWizard.RomActionWizard;
import manager.elements.RomListCell;
import manager.elements.SystemListCell;
import manager.enums.Language;
import manager.enums.devices.DeviceSupport;
import manager.enums.devices.FunkeyDevice;
import manager.enums.devices.PowKiddyV90Device;
import manager.models.General;
import manager.models.Rom;
import manager.models.Settings;
import manager.models.SystemListItem;
import manager.services.DirectoryService;
import manager.services.ImageService;
import manager.services.SettingsService;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class MainWindowController {
    @FXML
    private Menu topMenu;
    @FXML
    private MenuItem optionsMenuItem, closeMenuItem, aboutMenuItem;
    @FXML
    private Button closeButton, maximizeButton, minimizeButton;
    @FXML
    private Button changeDirButton, impactButton, addRomButton;
    @FXML
    private Label directoryLabel, leftStatus, romInfoTitle, romInfoSubTitle, romListHoverLabel,
                  detailsLabel, currentDirectoryLabel, systemSelectLabel, windowTitle, romListLabel;
    @FXML
    private ListView<Rom> romListView;
    @FXML
    private ListView<SystemListItem>systemListView;
    @FXML
    private ImageView imagePreview;
    @FXML
    private Pane romInfo;
    @FXML
    private AnchorPane romListHover, romPane, topBar;
    
    private Stage primaryStage;

    private double xOffset;
    private double yOffset;

    private Rom selectedRom;
    private boolean isDraggingRomActionWizard;
    private Settings settings;
    private SystemListItem selectedSystem;

    // Initialize method, called after all @FXML annotated members have been injected
    @FXML
    public void initialize() {
        settings = SettingsService.loadSettings();

        if(settings != null){
            
        }else{
            settings = SettingsService.defaultSettings();
        }

        directoryLabel.setText(settings.getGeneral().getRootDirectory());
        setLanguage(settings.getGeneral().getLanguage());

        // Add event listeners for drag functionality
        topBar.setOnMousePressed(this::handleMousePressed);
        topBar.setOnMouseDragged(this::handleMouseDragged);

        // Create an ObservableList to hold the data
        ObservableList<SystemListItem> systemList = FXCollections.observableArrayList();
        
        List<SystemListItem> systemListItems = DirectoryService.loadDevice(settings.getGeneral().getRootDirectory());

        for(SystemListItem i : systemListItems){
            systemList.add(i);
        }

        // Set the custom cell factory for the ListView
        systemListView.setCellFactory(listView -> new SystemListCell());

        // Set the ObservableList as the data source for the ListView
        systemListView.setItems(systemList);

        // Select first item and populate
        if (!systemList.isEmpty()) {
            systemListView.getSelectionModel().selectFirst();
            setupDragAndDrop();
            handleSystemListViewClick(null);
        }

        //Hide rom hover
        romListHover.setVisible(false);

        Label windowTitle = new Label("Rom Impactor");
        windowTitle.setLayoutX(topBar.getWidth() / 2);
        windowTitle.setLayoutY(4);

    }

    private List<String> getAllowedExtentions(){
        DeviceSupport device = DeviceSupport.getByName(settings.getGeneral().getDeviceProfile());

        List<String> allowedExtensions;
        SystemListItem system = systemListView.getSelectionModel().getSelectedItem();
        switch(device){
            case FUNKEY_S:
                allowedExtensions = FunkeyDevice.getFileExtensionsByFolderPath(system.getSystemName());
                break;
            case POWKIDDY_V90:
                allowedExtensions = PowKiddyV90Device.getFileExtensionsByFolderPath(system.getSystemName());
                break;
            default:
                allowedExtensions = new ArrayList<>();
                break;
        }
        return allowedExtensions;
    }

    private void setupDragAndDrop() {
        List<String> allowedExtensions = getAllowedExtentions();
    
        romPane.setOnDragOver(event -> {
            if (event.getGestureSource() != romPane && event.getDragboard().hasFiles()) {
                romListHover.setVisible(true);
                boolean hasSupportedFiles = event.getDragboard().getFiles().stream().anyMatch(f ->
                        allowedExtensions.contains(getFileExtension(f.getName())));
    
                if (hasSupportedFiles) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                    romListHoverLabel.setText("Drop ROM Here");
                } else {
                    romListHoverLabel.setText("File not supported");
                }
            }
            event.consume();
        });
    
        romPane.setOnDragDropped(event -> {
            boolean success = false;
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                List<File> files = dragboard.getFiles();
                for (File file : files) {
                    if (allowedExtensions.contains(getFileExtension(file.getName()))) {
                        // File matches the allowed extensions
                        Rom rom = createRom(file);
                        // Perform any additional actions with the ROM file
                        success = true;
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    
        romPane.setOnDragExited(event -> {
            romListHover.setVisible(false);
            romListHoverLabel.setText(""); // Resets the label text when the drag is exited
        });
    }
    

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }

    private void updateUI(Settings settings){

        // Create an ObservableList to hold the data
        ObservableList<SystemListItem> systemList = FXCollections.observableArrayList();
        List<SystemListItem> systemListItems;
        if(settings != null){
            systemListItems = DirectoryService.loadDevice(settings.getGeneral().getRootDirectory());
            directoryLabel.setText(settings.getGeneral().getRootDirectory());
        }else{
            systemListItems = DirectoryService.loadDevice(this.settings.getGeneral().getRootDirectory());
            directoryLabel.setText(this.settings.getGeneral().getRootDirectory());
        }

        for(SystemListItem i : systemListItems){
            systemList.add(i);
        }

        // Set the custom cell factory for the ListView
        systemListView.setCellFactory(listView -> new SystemListCell());

        // Set the ObservableList as the data source for the ListView
        systemListView.setItems(systemList);

        // Select first item and populate
        if (!systemList.isEmpty()) {
            systemListView.getSelectionModel().selectFirst();
            systemListView.scrollTo(systemListView.getSelectionModel().getSelectedItem());
            handleSystemListViewClick(null);
            setupDragAndDrop();
        }
    
    }

    private void updateSystemUI(Rom rom){

        // Create an ObservableList to hold the data
        ObservableList<SystemListItem> systemList = FXCollections.observableArrayList();
        List<SystemListItem> systemListItems;
        if(settings != null){
            systemListItems = DirectoryService.loadDevice(settings.getGeneral().getRootDirectory());
            directoryLabel.setText(settings.getGeneral().getRootDirectory());
        }else{
            systemListItems = DirectoryService.loadDevice(this.settings.getGeneral().getRootDirectory());
            directoryLabel.setText(this.settings.getGeneral().getRootDirectory());
        }

        SystemListItem selectedItem = null;

        for(SystemListItem i : systemListItems){
            if( i.getEnumName().equals(rom.getSystem()) ){
                selectedItem = i;
            }
            systemList.add(i);
        }

        if(selectedItem == null){
            selectedItem = systemList.get(0);
        }

        // Set the custom cell factory for the ListView
        systemListView.setCellFactory(listView -> new SystemListCell());

        // Set the ObservableList as the data source for the ListView
        systemListView.setItems(systemList);

        

        // Select first item and populate
        if (!systemList.isEmpty()) {
            systemListView.getSelectionModel().select(selectedItem);
            systemListView.scrollTo(systemListView.getSelectionModel().getSelectedItem());
            handleSystemListViewClick(null);
            updateRomUI(rom);
            setupDragAndDrop();
        }
    
    }

    private void updateRomUI(Rom inputRom) {
        SystemListItem selectedItem = systemListView.getSelectionModel().getSelectedItem();
    
        // Create an ObservableList to hold the data
        ObservableList<Rom> romList = FXCollections.observableArrayList();
    
        List<Rom> romListItems = selectedItem.getRoms();
    
        for (Rom rom : romListItems) {
            romList.add(rom);
        }
    
        if (inputRom != null) {
            romList.add(inputRom);
        }
    
        // Set the custom cell factory for the ListView
        romListView.setCellFactory(listView -> new RomListCell());
    
        // Set the ObservableList as the data source for the ListView
        romListView.setItems(romList);
    
        // Select the inputRom if it was selected previously, otherwise select the first ROM on the list
        if (inputRom != null && romList.contains(inputRom)) {
            romListView.getSelectionModel().select(inputRom);
        } else if (!romList.isEmpty()) {
            romListView.getSelectionModel().selectFirst();
        }
    
        // Update the ROM preview and set up drag and drop
        romListView.scrollTo(romListView.getSelectionModel().getSelectedItem());
        updateRomPreview(romListView.getSelectionModel().getSelectedItem());
        setupDragAndDrop();
        handleRomListViewClick(null);
    }
    

    private void setLanguage(Language language){
        ResourceBundle bundle = ResourceBundle.getBundle("localization/mainWindow", new Locale(language.getCode()));
    
        // Retrieve translations for each UI element from the resource bundle
        optionsMenuItem.setText(bundle.getString("optionsMenuItem"));
        closeMenuItem.setText(bundle.getString("closeMenuItem"));
        topMenu.setText(bundle.getString("topMenu"));
        aboutMenuItem.setText(bundle.getString("aboutMenuItem"));
        addRomButton.setText(bundle.getString("addRomButton"));
        changeDirButton.setText(bundle.getString("changeDirButton"));
        

        impactButton.setText(bundle.getString("impactButton"));

        leftStatus.setText(bundle.getString("leftStatusLabel"));

        windowTitle.setText(bundle.getString("windowTitle"));
        //make windowTitle layoutX half of 900 minus how long the text makes it to center it
        adjustLayoutXToFitText(windowTitle, 900);

        currentDirectoryLabel.setText(bundle.getString("currentDirectoryLabel"));
        systemSelectLabel.setText(bundle.getString("systemSelectLabel"));
        detailsLabel.setText(bundle.getString("detailsLabel"));
        romListLabel.setText(bundle.getString("romListLabel"));
    }

    private void adjustLayoutXToFitText(Label labelElement, double containerWidth) {
        Text tempText = new Text(labelElement.getText());
        tempText.setFont(labelElement.getFont());
    
        double textWidth = tempText.getLayoutBounds().getWidth();
    
        // Set layoutX to half of container width minus half of text width
        labelElement.setLayoutX((containerWidth - textWidth) / 2);
    }

    private void handleMousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    private void handleMouseDragged(MouseEvent event) {
        primaryStage.setX(event.getScreenX() - xOffset);
        primaryStage.setY(event.getScreenY() - yOffset);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public MainWindowController() {
  
    }

    @FXML
    public void handleSystemListViewClick(MouseEvent event){
        // Get the selected item from the ListView
        selectedSystem = systemListView.getSelectionModel().getSelectedItem();

        // Create an ObservableList to hold the data
        ObservableList<Rom> romList = FXCollections.observableArrayList();
        
        List<Rom> romListItems = selectedSystem.getRoms();

        for(Rom rom : romListItems){
            romList.add(rom);
        }

        // Set the custom cell factory for the ListView
        romListView.setCellFactory(listView -> new RomListCell());

        // Set the ObservableList as the data source for the ListView
        romListView.setItems(romList);

        // Select first item and populate
        if (!romList.isEmpty()) {
            romListView.getSelectionModel().selectFirst();
            updateRomPreview(romListView.getSelectionModel().getSelectedItem());
            setupDragAndDrop();
            handleRomListViewClick(null);
        }
    }

    /*
     *  RomView Events
     */

     @FXML
     public void handleRomListViewClick(MouseEvent event) {

        // Update selectedRom whenever a new Rom is clicked
        selectedRom = romListView.getSelectionModel().getSelectedItem();
        romListView.scrollTo(romListView.getSelectionModel().getSelectedItem());
        updateRomPreview(selectedRom);

        if (event != null && event.getButton() == MouseButton.SECONDARY && event.getClickCount() == 1) {
            // Only trigger the action on a right click event
            
        }
    }

    @FXML
    public void handleRomListViewKeyPressed(KeyEvent event) {
        if (event != null && event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN ||
            event.getCode() == KeyCode.PAGE_UP || event.getCode() == KeyCode.PAGE_DOWN
        ) {

            // Update selectedRom whenever a new Rom is selected via key press
            selectedRom = romListView.getSelectionModel().getSelectedItem();
            romListView.scrollTo(romListView.getSelectionModel().getSelectedItem());

            updateRomPreview(selectedRom);

        }
    }

    @FXML
    public void handleAddRomClick(MouseEvent event){
        Stage stage = (Stage) impactButton.getScene().getWindow(); // Replace 'yourNode' with the appropriate reference to your UI node

        FileChooser fileChooser = new FileChooser();
    
        // Set the title and initial directory of the file chooser dialog
        fileChooser.setTitle("Select ROM File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    
        // Specify the file extensions to filter
        List<String> allowedExtensions = getAllowedExtentions();
        ExtensionFilter extensionFilter = new ExtensionFilter("ROM Files", allowedExtensions);
        fileChooser.getExtensionFilters().add(extensionFilter);
    
        // Show the file chooser dialog and wait for user selection
        File selectedFile = fileChooser.showOpenDialog(stage);
    
        Rom rom = createRom(selectedFile);
        // pass this rom over to the Rom Wizard, that leads into the actionWizard;
    }

    public void updateRomPreview(Rom rom){


        romInfoTitle.setText("Name: " + rom.getName());

        if(rom.getReleaseDate() == null || rom.getReleaseDate().isEmpty()){
            romInfoSubTitle.setText("Release Date: None" );
        }else{
            romInfoSubTitle.setText("Release Date: " + rom.getReleaseDate());
        }

        if(rom.getImageFile() == null || !rom.getImageFile().exists()){
            imagePreview.setImage(new Image(getClass().getResourceAsStream("/images/noimage.png")));
        }else{
            imagePreview.setImage(ImageService.convertToFxImage(rom.getImageAsBufferedImage()));
        }
        
    }

    /*
     *  Titlebar Events
     */
    
    @FXML
    public void handleMinimizeButton(ActionEvent event) {
        primaryStage.setIconified(true);  // Minimize the window
    }

    @FXML
    public void handleCloseMenuItem() {
        primaryStage.close();  // Close the window
    }

    @FXML
    public void handleCloseButton(ActionEvent event) {
        primaryStage.close();  // Close the window
    }

    /*
     * Top Menu Events
     */
    
    @FXML
    public void handleOptionsMenuItem() {
        try {
            // Load the OptionsWindow.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/OptionsWindow.fxml"));
            Parent root = loader.load();
            
            // Create a new stage for the options window
            Stage optionsStage = new Stage();
            optionsStage.setTitle("Options");
            optionsStage.setScene(new Scene(root));
            
            // Show the options window
            optionsStage.show();

            // Get the controller of the options window
            OptionsWindowController optionsController = loader.getController();

            // Set listener for options window closing event
            optionsStage.setOnHidden(event -> {
                // Perform saveSettings() operation when the options window is closed
                optionsController.saveSettings();
                settings = SettingsService.loadSettings();
                setLanguage(settings.getGeneral().getLanguage());
                updateUI(settings);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleAboutMenuItem(MouseEvent event) {

       // 
   }

    @FXML
    public void handleChangeDirButton() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        String initialDirectoryPath = settings.getGeneral().getRootDirectory();
        File initialDirectory = new File(initialDirectoryPath);
        if (!initialDirectory.isDirectory()) {
            String removableDrivePath = DirectoryService.getFirstRemovableDrivePath();
            if (removableDrivePath != null) {
                initialDirectory = new File(removableDrivePath);
            }
        }
        directoryChooser.setInitialDirectory(initialDirectory);
        File selectedDirectory = directoryChooser.showDialog(directoryLabel.getScene().getWindow());
        if (selectedDirectory != null) {
            String selectedDirectoryString = selectedDirectory.getAbsolutePath();
            directoryLabel.setText(selectedDirectoryString);

            Settings settings = new Settings();
            General general = new General();
            general.setRootDirectory(selectedDirectoryString);
            settings.setGeneral(general);
            updateUI(settings);
        }
    }

    @FXML
    public void handlelocalImageButton() {
        createNewActionWizard();
    }

    public void createNewActionWizard(){
        try {
            // Load the RomActionWizard.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/actionWizard/RomActionWizard.fxml"));
            Parent root = loader.load();

            int minWindowWidth = 800;
            int minWindowHeight = 528;

            // Create a custom shape for the stage
            Rectangle windowShape = new Rectangle(minWindowWidth, minWindowHeight);
            windowShape.setArcWidth(20);
            windowShape.setArcHeight(20);
            root.setClip(windowShape);

            // Create a new stage for the options window
            Stage localImageStage = new Stage();
            localImageStage.initStyle(StageStyle.TRANSPARENT);
            localImageStage.setTitle("Local Image Wizard");
            Scene scene = new Scene(root, Color.TRANSPARENT);
            localImageStage.setScene(scene);

            // Make the stage resizable
            scene.setOnMouseMoved(event -> {
                double x = event.getX();
                double y = event.getY();
                double width = localImageStage.getWidth();
                double height = localImageStage.getHeight();

                // Change the cursor when near the edge
                if (x < 5 && y < 5) {
                    scene.setCursor(Cursor.NW_RESIZE);
                } else if (x < 5 && Math.abs(y - height) < 5) {
                    scene.setCursor(Cursor.SW_RESIZE);
                } else if (Math.abs(x - width) < 5 && y < 5) {
                    scene.setCursor(Cursor.NE_RESIZE);
                } else if (Math.abs(x - width) < 5 && Math.abs(y - height) < 5) {
                    scene.setCursor(Cursor.SE_RESIZE);
                } else if (x < 5) {
                    scene.setCursor(Cursor.W_RESIZE);
                } else if (Math.abs(x - width) < 5) {
                    scene.setCursor(Cursor.E_RESIZE);
                } else if (y < 5) {
                    scene.setCursor(Cursor.N_RESIZE);
                } else if (Math.abs(y - height) < 5) {
                    scene.setCursor(Cursor.S_RESIZE);
                } else {
                    scene.setCursor(Cursor.DEFAULT);
                }
            });

            scene.setOnMouseDragged(event -> {
                // Resize the window when dragging near the edge
                if (scene.getCursor() == Cursor.NW_RESIZE) {
                    isDraggingRomActionWizard = true;
                    double deltaX = event.getScreenX() - localImageStage.getX();
                    double deltaY = event.getScreenY() - localImageStage.getY();
                    double newWidth = localImageStage.getWidth() - deltaX;
                    double newHeight = localImageStage.getHeight() - deltaY;
            
                    if (newWidth > minWindowWidth  && newHeight > 0) {
                        localImageStage.setX(event.getScreenX());
                        localImageStage.setWidth(newWidth);
                        localImageStage.setY(event.getScreenY());
                        localImageStage.setHeight(newHeight);
                    }
                } else if (scene.getCursor() == Cursor.NE_RESIZE) {
                    isDraggingRomActionWizard = true;
                    double deltaX = event.getScreenX() - localImageStage.getX() - localImageStage.getWidth();
                    double deltaY = event.getScreenY() - localImageStage.getY();
                    double newWidth = localImageStage.getWidth() + deltaX;
                    double newHeight = localImageStage.getHeight() - deltaY;
            
                    if (newWidth > minWindowWidth && newHeight > minWindowHeight) {
                        localImageStage.setY(event.getScreenY());
                        localImageStage.setWidth(newWidth);
                        localImageStage.setHeight(newHeight);
                    }
                } else if (scene.getCursor() == Cursor.SW_RESIZE) {
                    isDraggingRomActionWizard = true;
                    double deltaX = event.getScreenX() - localImageStage.getX();
                    double deltaY = event.getScreenY() - localImageStage.getY() - localImageStage.getHeight();
                    double newWidth = localImageStage.getWidth() - deltaX;
                    double newHeight = localImageStage.getHeight() + deltaY;
            
                    if (newWidth > minWindowWidth && newHeight > minWindowHeight) {
                        localImageStage.setX(event.getScreenX());
                        localImageStage.setWidth(newWidth);
                        localImageStage.setHeight(newHeight);
                    }
                } else if (scene.getCursor() == Cursor.SE_RESIZE) {
                    isDraggingRomActionWizard = true;
                    double deltaX = event.getScreenX() - localImageStage.getX() - localImageStage.getWidth();
                    double deltaY = event.getScreenY() - localImageStage.getY() - localImageStage.getHeight();
                    double newWidth = localImageStage.getWidth() + deltaX;
                    double newHeight = localImageStage.getHeight() + deltaY;
            
                    if (newWidth > minWindowWidth && newHeight > minWindowHeight) {
                        localImageStage.setWidth(newWidth);
                        localImageStage.setHeight(newHeight);
                    }
                } else if (scene.getCursor() == Cursor.N_RESIZE) {
                    isDraggingRomActionWizard = true;
                    double deltaY = event.getScreenY() - localImageStage.getY();
                    double newHeight = localImageStage.getHeight() - deltaY;
            
                    if (newHeight > minWindowHeight) {
                        localImageStage.setY(event.getScreenY());
                        localImageStage.setHeight(newHeight);
                    }
                } else if (scene.getCursor() == Cursor.S_RESIZE) {
                    isDraggingRomActionWizard = true;
                    double deltaY = event.getScreenY() - localImageStage.getY() - localImageStage.getHeight();
                    double newHeight = localImageStage.getHeight() + deltaY;
            
                    if (newHeight > minWindowHeight) {
                        localImageStage.setHeight(newHeight);
                    }
                } else if (scene.getCursor() == Cursor.W_RESIZE) {
                    isDraggingRomActionWizard = true;
                    double deltaX = event.getScreenX() - localImageStage.getX();
                    double newWidth = localImageStage.getWidth() - deltaX;
            
                    if (newWidth > minWindowWidth) {
                        localImageStage.setX(event.getScreenX());
                        localImageStage.setWidth(newWidth);
                    }
                } else if (scene.getCursor() == Cursor.E_RESIZE) {
                    isDraggingRomActionWizard = true;
                    double deltaX = event.getScreenX() - localImageStage.getX() - localImageStage.getWidth();
                    double newWidth = localImageStage.getWidth() + deltaX;
            
                    if (newWidth > minWindowWidth) {
                        localImageStage.setWidth(newWidth);
                    }
                }

                // Update the custom shape
                windowShape.setWidth(localImageStage.getWidth());
                windowShape.setHeight(localImageStage.getHeight());
                
            });
            scene.setOnMouseReleased(event -> {
                isDraggingRomActionWizard = false;
            });

            // Show the options window
            localImageStage.show();

            RomActionWizard localImageWindowController = loader.getController();
            localImageWindowController.setPrimaryStage(localImageStage);
            localImageWindowController.receiveRom(selectedRom);
            localImageWindowController.setParentController(this);

            // Set listener for options window closing event
            localImageStage.setOnHidden(event -> {
                handleSystemListViewClick(null);
                updateSystemUI(selectedRom);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDragging() {
        return this.isDraggingRomActionWizard;
    }

    public Rom createRom(File file){
        // take the file, and put it in a Rom
        Rom rom = new Rom();
        File[] files = {file,null};

        rom.setFiles(files);
        rom.setName(file.getName());
        rom.setReleaseDate("");
        rom.setSystem(selectedSystem.getEnumName());

        updateSystemUI(DirectoryService.saveRom(rom));

        return rom;
    }

    public void setSelectedRom(Rom inputRom) {
        this.selectedRom = inputRom;
    }



}