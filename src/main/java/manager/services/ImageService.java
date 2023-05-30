package manager.services;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageService {

    public static void convertAndResizeImage(File file, String outputPath, boolean overwrite) throws IOException {
        // Check if the output file exists and whether to overwrite it
        File outputFile = new File(outputPath);
        if (outputFile.exists() && !overwrite) {
            throw new IOException("Output file already exists and overwrite flag is set to false.");
        }

        // Load the image file
        BufferedImage originalImage = ImageIO.read(file);

        // Resize the image while maintaining aspect ratio
        BufferedImage resizedImage = resizeImage(originalImage, 240, 240);

        // Save the FX Image to the output file
        saveFxImage(resizedImage, outputFile);
    }

    public static void convertAndResizeImage(BufferedImage originalImage, String outputPath, boolean overwrite) throws IOException {
        // Check if the output file exists and whether to overwrite it
        File outputFile = new File(outputPath);
        if (outputFile.exists() && !overwrite) {
            throw new IOException("Output file already exists and overwrite flag is set to false.");
        }

        // Resize the image while maintaining aspect ratio
        BufferedImage resizedImage = resizeImage(originalImage, 240, 240);

        // Save the FX Image to the output file
        saveFxImage(resizedImage, outputFile);
    }

    private static BufferedImage resizeImage(BufferedImage image, int maxWidth, int maxHeight) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Calculate the new dimensions while maintaining aspect ratio
        if (width > maxWidth || height > maxHeight) {
            double aspectRatio = (double) width / height;

            if (aspectRatio > 1) {
                width = maxWidth;
                height = (int) (width / aspectRatio);
                if (height > maxHeight) {
                    height = maxHeight;
                    width = (int) (height * aspectRatio);
                }
            } else {
                height = maxHeight;
                width = (int) (height * aspectRatio);
                if (width > maxWidth) {
                    width = maxWidth;
                    height = (int) (width / aspectRatio);
                }
            }

            // Resize the image
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = resizedImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.drawImage(image, 0, 0, width, height, null);
            graphics.dispose();

            return resizedImage;
        }

        return image;
    }

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

    private static void saveFxImage(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "png", file);
    }
}
