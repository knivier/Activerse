package ActiverseEngine;

import ActiverseUtils.ErrorLogger;
import ActiverseUtils.ResourcePaths;

import java.awt.*;
import java.net.URL;

/**
 * Represents an image loader for loading images from files.
 * This class provides methods to load an image and retrieve it.
 *
 * @author Knivier
 * @version 1.4.1
 */
public class ActiverseImage {
    private static final Component MEDIA_TRACKER_PEER = new Canvas();

    private final Image image;
    private final String path;
    private boolean loaded = false;

    /**
     * Constructs a new ActiverseImage object with the image loaded from the specified file.
     *
     * @param filename The path to the image file via (jpg/jpeg, png, gif, bmp, wbmp)
     */
    public ActiverseImage(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    ErrorLogger.format("2A", "IN", "ActiverseImage(String filename)", "filename cannot be null or empty."));
        }
        
        path = filename;
        URL url = ResourcePaths.requireUrl(
                filename,
                () -> new RuntimeException(
                        ErrorLogger.format("2A", "IN", "ActiverseImage(String filename)",
                                "could not resolve image path: " + filename))
        );
        image = Toolkit.getDefaultToolkit().getImage(url);

        if (image == null) {
            throw new NullPointerException(
                    ErrorLogger.format("2A", "IN", "ActiverseImage(String filename)", "image is null (Report: INTO). Please check the image path and try again."));
        }
        
        MediaTracker tracker = new MediaTracker(MEDIA_TRACKER_PEER);
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
            loaded = !tracker.isErrorID(0);
        } catch (InterruptedException e) {
            ErrorLogger.report("2A", "IN", "ActiverseImage(String filename)", "image loading was interrupted.");
            Thread.currentThread().interrupt();
            loaded = false;
        }
        
        if (!loaded) {
            throw new RuntimeException(
                    ErrorLogger.format("2A", "IN", "ActiverseImage(String filename)", "failed to load image from path: " + filename));
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
            throw new NullPointerException(
                    ErrorLogger.format("2A", "OUT", "getImage()", "image is null. Please check the image path and try again."));
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
            throw new NullPointerException(
                    ErrorLogger.format("2A", "IN.OUT", "getPath()", "path is null. Please check the image path and try again."));
        }
        return path;
    }
}
