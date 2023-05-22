package manager.services;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;

public class ImageService {

    public static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        ImageView imageView = new ImageView(wr);
        return applyBicubicFilter(imageView.getImage());
    }

    public static Image applyBicubicFilter(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        // Create a writable image and get the pixel reader
        WritableImage wr = new WritableImage(width, height);
        PixelReader pixelReader = image.getPixelReader();

        // Create an int[] buffer for the pixel data
        int[] pixels = new int[width * height];
        pixelReader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        // Apply the bicubic filter to the pixel data
        applyBicubicFilter(pixels, width, height);

        // Write the modified pixel data back to the writable image
        PixelWriter pixelWriter = wr.getPixelWriter();
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);

        return wr;
    }

    private static void applyBicubicFilter(int[] pixels, int width, int height) {
        // Perform bicubic filtering on the pixel data
        // Modify the pixel array directly as per your bicubic filter implementation
        // This code is just a placeholder

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];

            // Retrieve the pixel components (ARGB)
            int a = (pixel >> 24) & 0xff;
            int r = (pixel >> 16) & 0xff;
            int g = (pixel >> 8) & 0xff;
            int b = pixel & 0xff;

            // Apply bicubic filtering or any other image processing logic here
            // This code is just a placeholder

            // Modify the pixel values
            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

}
