package ActiverseEngine;

import java.awt.*;

/**
 * Represents an image loader for loading images from files.
 * This class provides methods to load an image and retrieve it.
 */
public class ActiverseImage {
    private Image image;
    private String path; // Store the image path

    /**
     * Constructs a new ActiverseImage object with the image loaded from the specified file.
     *
     * @param filename The path to the image file.
     */
    public ActiverseImage(String filename) {
        // Load the image from the specified file
        image = Toolkit.getDefaultToolkit().getImage(filename);
        path = filename; // Store the image path
    }

    /**
     * Gets the loaded image.
     *
     * @return The Image object representing the loaded image.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Gets the path to the image file.
     *
     * @return The path to the image file.
     */
    public String getPath() {
        return path;
    }
}
