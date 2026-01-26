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
        if (a == null || b == null) {
            return false;
        }
        
        ActiverseImage imgA = a.getImage();
        ActiverseImage imgB = b.getImage();
        
        if (imgA == null || imgB == null || imgA.getImage() == null || imgB.getImage() == null) {
            // Fall back to bounding box collision if images are not available
            Rectangle r1 = a.getBoundingBox();
            Rectangle r2 = b.getBoundingBox();
            return r1.intersects(r2);
        }
        
        // Get bounding rectangles
        Image imageA = imgA.getImage();
        Image imageB = imgB.getImage();
        int widthA = imageA.getWidth(null);
        int heightA = imageA.getHeight(null);
        int widthB = imageB.getWidth(null);
        int heightB = imageB.getHeight(null);
        
        if (widthA < 0 || heightA < 0 || widthB < 0 || heightB < 0) {
            // Image not loaded yet, use bounding box
            Rectangle r1 = a.getBoundingBox();
            Rectangle r2 = b.getBoundingBox();
            return r1.intersects(r2);
        }
        
        Rectangle r1 = new Rectangle(a.getX(), a.getY(), widthA, heightA);
        Rectangle r2 = new Rectangle(b.getX(), b.getY(), widthB, heightB);
        Rectangle intersection = r1.intersection(r2);

        if (intersection.isEmpty()) {
            return false;
        }

        // Convert images to BufferedImage for pixel access
        BufferedImage bufferedImgA = toBufferedImage(imageA);
        BufferedImage bufferedImgB = toBufferedImage(imageB);

        for (int y = intersection.y; y < intersection.y + intersection.height; y++) {
            for (int x = intersection.x; x < intersection.x + intersection.width; x++) {
                int ax = x - a.getX();
                int ay = y - a.getY();
                int bx = x - b.getX();
                int by = y - b.getY();

                if (ax < 0 || ay < 0 || bx < 0 || by < 0 ||
                        ax >= bufferedImgA.getWidth() || ay >= bufferedImgA.getHeight() ||
                        bx >= bufferedImgB.getWidth() || by >= bufferedImgB.getHeight()) {
                    continue;
                }

                int pixelA = bufferedImgA.getRGB(ax, ay);
                int pixelB = bufferedImgB.getRGB(bx, by);

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
