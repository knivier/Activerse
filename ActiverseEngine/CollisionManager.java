package ActiverseEngine;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Manages collision detection between actors in the world.
 * This class provides a method to check if two actors intersect.
 *
 * @author Knivier
 * @version 1.4.0
 */
public class CollisionManager {

    /**
     * Checks if two actors intersect using pixel-perfect collision detection.
     *
     * @param a The first actor.
     * @param b The second actor.
     * @return true if the non-transparent pixels of the two actors overlap, false otherwise.
     */
    public static boolean intersects(Actor a, Actor b) {
        // Get bounding rectangles
        Rectangle r1 = new Rectangle(a.getX(), a.getY(), a.getImage().getImage().getWidth(null), a.getImage().getImage().getHeight(null));
        Rectangle r2 = new Rectangle(b.getX(), b.getY(), b.getImage().getImage().getWidth(null), b.getImage().getImage().getHeight(null));
        Rectangle intersection = r1.intersection(r2);

        if (intersection.isEmpty()) {
            return false;
        }

        // Convert images to BufferedImage for pixel access
        BufferedImage imgA = toBufferedImage(a.getImage().getImage());
        BufferedImage imgB = toBufferedImage(b.getImage().getImage());

        for (int y = intersection.y; y < intersection.y + intersection.height; y++) {
            for (int x = intersection.x; x < intersection.x + intersection.width; x++) {
                int ax = x - a.getX();
                int ay = y - a.getY();
                int bx = x - b.getX();
                int by = y - b.getY();

                if (ax < 0 || ay < 0 || bx < 0 || by < 0 ||
                    ax >= imgA.getWidth() || ay >= imgA.getHeight() ||
                    bx >= imgB.getWidth() || by >= imgB.getHeight()) {
                    continue;
                }

                int pixelA = imgA.getRGB(ax, ay);
                int pixelB = imgB.getRGB(bx, by);

                // Check if both pixels are not transparent
                if (((pixelA >> 24) & 0xff) != 0 && ((pixelB >> 24) & 0xff) != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    // Utility method to convert Image to BufferedImage
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bimage = new BufferedImage(
                img.getWidth(null), img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }
}
