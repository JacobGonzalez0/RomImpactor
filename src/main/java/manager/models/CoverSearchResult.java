package manager.models;

import java.awt.image.BufferedImage;

public class CoverSearchResult {
    private String name;
    private String id;
    private BufferedImage image;

    public CoverSearchResult() {
    }

    public CoverSearchResult(String name, String id, BufferedImage image) {
        this.name = name;
        this.id = id;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getImageWidth() {
        return image != null ? image.getWidth() : 0;
    }

    public int getImageHeight() {
        return image != null ? image.getHeight() : 0;
    }

    public double getImageAspectRatio() {
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            if (height != 0) {
                return (double) width / height;
            }
        }
        return 0;
    }
}
