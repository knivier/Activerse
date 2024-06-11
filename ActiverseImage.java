import java.awt.*;

/**
 * Represents an image loader for loading images from files.
 * This class provides methods to load an image and retrieve it.
 *
 * @author Knivier
 */
public class ActiverseImage {
    private Image image;

    /**
     * Constructs a new ActiverseImage object with the image loaded from the specified file.
     *
     * @param filename The path to the image file.
     */
    public ActiverseImage(String filename) {
        // Load the image from the specified file
        image = Toolkit.getDefaultToolkit().getImage(filename);
    }

    /**
     * Gets the loaded image.
     *
     * @return The Image object representing the loaded image.
     */
    public Image getImage() {
        return image;
    }
}
