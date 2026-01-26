package ActiverseUtils;

import ActiverseEngine.ActiverseImage;

import java.awt.Dimension;
import java.awt.Image;

/**
 * ImageUtils - Shared helpers for safe image dimension lookups.
 *
 * @author Knivier
 * @version 1.4.0
 */
public final class ImageUtils {
    private ImageUtils() {
    }

    public static Dimension getImageDimensions(ActiverseImage image, int fallbackWidth, int fallbackHeight) {
        int width = fallbackWidth;
        int height = fallbackHeight;

        if (image != null) {
            try {
                Image awtImage = image.getImage();
                int imgWidth = awtImage.getWidth(null);
                int imgHeight = awtImage.getHeight(null);
                if (imgWidth > 0) {
                    width = imgWidth;
                }
                if (imgHeight > 0) {
                    height = imgHeight;
                }
            } catch (RuntimeException ignored) {
                // Fall back to stored dimensions if image access fails.
            }
        }

        if (width < 0) {
            width = 0;
        }
        if (height < 0) {
            height = 0;
        }
        return new Dimension(width, height);
    }
}
