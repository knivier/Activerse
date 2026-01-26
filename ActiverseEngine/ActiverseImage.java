package ActiverseEngine;

import java.awt.*;

/**
 * Represents an image loader for loading images from files.
 * This class provides methods to load an image and retrieve it.
 *
 * @author Knivier
 * @version 1.4.0
 */
public class ActiverseImage {
    private final Image image; // Image object to store the loaded image
    private final String path; // Store the image path
    private boolean loaded = false; // Track if image is fully loaded

    /**
     * Constructs a new ActiverseImage object with the image loaded from the specified file.
     *
     * @param filename The path to the image file via (jpg/jpeg, png, gif, bmp, wbmp)
     */
    public ActiverseImage(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("2A.IN:(LN: ActiverseImage(String filename) - ACEHS Error thrown; filename cannot be null or empty.");
        }
        
        path = filename;
        image = Toolkit.getDefaultToolkit().getImage(filename);
        
        if (image == null) {
            throw new NullPointerException("2A.IN:(LN: ActiverseImage(String filename) - ACEHS Error thrown; image is null (Report: INTO). Please check the image path and try again.");
        }
        
        // Use MediaTracker to ensure image is loaded synchronously
        // Create a temporary frame for MediaTracker (it needs a Component)
        Frame tempFrame = new Frame();
        tempFrame.setVisible(false);
        MediaTracker tracker = new MediaTracker(tempFrame);
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
            loaded = !tracker.isErrorID(0);
        } catch (InterruptedException e) {
            System.out.println("2A.IN:(LN: ActiverseImage(String filename) - ACEHS Error thrown; image loading was interrupted.");
            Thread.currentThread().interrupt();
            loaded = false;
        } finally {
            tempFrame.dispose(); // Clean up temporary frame
        }
        
        if (!loaded) {
            throw new RuntimeException("2A.IN:(LN: ActiverseImage(String filename) - ACEHS Error thrown; failed to load image from path: " + filename);
        }
    }
    
    /**
     * Checks if the image has been fully loaded.
     *
     * @return true if the image is loaded, false otherwise.
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Gets the loaded image.
     *
     * @return The Image object representing the loaded image.
     */
    public Image getImage() {
        if (image == null) {
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
        if (path == null) {
            throw new NullPointerException("2A.IN.OUT:(LN: getPath() - ACEHS Error thrown; path is null. Please check the image path and try again.");
        }
        return path;
    }
}
