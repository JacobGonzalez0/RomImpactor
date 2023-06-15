package manager.models;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Rom {
    private String name;
    private String releaseDate;
    private File[] files;
    private String system;

    public Rom(String name, String releaseDate, File[] files, String system) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.files = files;
        this.system = system;
    }

    public Rom(String name, String releaseDate, File[] files) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.files = files;
    }

    public Rom(){}

    public void updateName() {
        if (files != null && name != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i] != null) {
                    String filePath = files[i].getPath();
                    String extension = "";
    
                    int dotIndex = files[i].getName().lastIndexOf('.');
                    if (dotIndex > 0) {
                        extension = files[i].getName().substring(dotIndex);
                    }
    
                    File renamedFile = new File(filePath.replace(files[i].getName(), name + extension));
                    if (files[i].renameTo(renamedFile)) {
                        files[i] = renamedFile;
                    } else {
                        System.out.println("Failed to rename file: " + files[i].getName());
                    }
                }
            }
        }
    }    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public File getRomFile() {
        if (files != null && files.length >= 1) {
            return files[0];
        }
        return null;
    }

    public File getImageFile() {
        if (files != null && files.length >= 2) {
            return files[1];
        }
        return null;
    }

    public BufferedImage getImageAsBufferedImage() {
        File imageFile = getImageFile();
        if (imageFile != null) {
            try {
                return ImageIO.read(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}

