import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Represents an image loader for loading images from files.
 * This class provides methods to load an image and retrieve it.
 *
 * @author Knivier
 */
public class ActiverseImage {
    private BufferedImage image;

    /**
     * Constructs a new ActiverseImage object with the image loaded from the specified file.
     *
     * @param filename The path to the image file.
     * @throws IOException If an error occurs during reading.
     */
    public ActiverseImage(String filename) throws IOException {
        // Load the image from the specified file
        image = ImageIO.read(new File(filename));
    }

    /**
     * Gets the loaded image.
     *
     * @return The BufferedImage object representing the loaded image.
     */
    public BufferedImage getImage() {
        return image;
    }
}
