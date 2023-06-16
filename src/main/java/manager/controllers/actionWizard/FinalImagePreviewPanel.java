package manager.controllers.actionWizard;



import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import manager.enums.devices.FunkeyDevice;
import manager.models.Rom;
import manager.models.Settings;
import manager.services.ImageService;
import manager.services.SettingsService;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class FinalImagePreviewPanel{

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView imageView;

    @FXML
    private Label titleLabel;

    @FXML
    private Button saveButton;

    private Rom selectedRom;

    private BufferedImage image;

    public void initialize() {
        // Initialization code goes here
    }

    public void receiveData(BufferedImage image, Rom selectedRom){
        this.image = image;
        this.selectedRom = selectedRom;

        if(this.image != null){
            imageView.setImage(ImageService.convertToFxImage(this.image));
        }
    }

    public boolean saveImage(){
        try {
            Settings settings = SettingsService.loadSettings();
            String deviceName = settings.getGeneral().getDeviceProfile();

            String relativeFilePath = "";
            switch (deviceName) {
                case "FUNKEY_S":
                    FunkeyDevice funkeyDevice = FunkeyDevice.valueOf(selectedRom.getSystem());
                    relativeFilePath = funkeyDevice.getImageRegexPattern();
                    break;
            }

            selectedRom.updateName();
            // Get the base name of the Rom file (without extension)
            String romBaseName = selectedRom.getRomFile().getName();
            int pos = romBaseName.lastIndexOf(".");
            if (pos > 0) {
                romBaseName = romBaseName.substring(0, pos);
            }

            // Set the image file name as the base name of the Rom file with .png extension
            String imageName = romBaseName + ".png";
            File imageFile = new File(selectedRom.getRomFile().getParentFile(), imageName);

            ImageService.convertAndResizeImage(
                image,
                imageFile.getAbsolutePath(),
                true
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
