package ActiverseEngine;

import java.awt.*;

/**
 * Represents an image loader for loading images from files.
 * This class provides methods to load an image and retrieve it.
 * 
 * @author Knivier
 * @version 1.2.2
 */
public class ActiverseImage {
    private final Image image; // Image object to store the loaded image
    private final String path; // Store the image path

    /**
     * Constructs a new ActiverseImage object with the image loaded from the specified file.
     *
     * @param filename The path to the image file via (jpg/jpeg, png, gif, bmp, wbmp)
     */
    public ActiverseImage(String filename) {
        image = Toolkit.getDefaultToolkit().getImage(filename);
        path = filename;
        if(image == null){
            throw new NullPointerException("2A.IN:(LN: ActiverseImage(String filename) - ACEHS Error thrown; image is null (Report: INTO). Please check the image path and try again.");
        }
    }

    /**
     * Gets the loaded image.
     *
     * @return The Image object representing the loaded image.
     */
    public Image getImage() {
        if(image == null) {
            throw new NullPointerException("2A.OUT:(LN: getImage() - ACEHS Error thrown; image is null. Please check the image path and try again.");
        }
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
