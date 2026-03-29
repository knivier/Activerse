package ActiverseUtils;

import java.awt.Graphics2D;

/**
 * Shared text layout helpers for menu/HUD rendering.
 */
public final class TextRenderUtils {
    private TextRenderUtils() {}

    /**
     * Returns an x-position that horizontally centers text in a container.
     *
     * @param g2d Graphics context used for font metrics
     * @param text Text to measure (null-safe)
     * @param containerX Left coordinate of the container
     * @param containerWidth Container width in pixels
     * @return Pixel x position for centered baseline drawing
     */
    public static int centeredX(Graphics2D g2d, String text, int containerX, int containerWidth) {
        if (g2d == null) {
            return containerX;
        }
        String safe = text != null ? text : "";
        return containerX + (containerWidth - g2d.getFontMetrics().stringWidth(safe)) / 2;
    }

    /**
     * Draws text horizontally centered in a container rectangle.
     *
     * @param g2d Graphics context used for measurement and drawing
     * @param text Text to draw (null-safe)
     * @param y Baseline y coordinate
     * @param containerX Left coordinate of the container
     * @param containerWidth Container width in pixels
     */
    public static void drawCenteredX(Graphics2D g2d, String text, int y, int containerX, int containerWidth) {
        String safe = text != null ? text : "";
        g2d.drawString(safe, centeredX(g2d, safe, containerX, containerWidth), y);
    }

    /**
     * Draws text centered in the full width [0, screenWidth].
     *
     * @param g2d Graphics context
     * @param text Text to draw (null-safe)
     * @param y Baseline y coordinate
     * @param screenWidth Full drawable width
     */
    public static void drawCenteredX(Graphics2D g2d, String text, int y, int screenWidth) {
        drawCenteredX(g2d, text, y, 0, screenWidth);
    }
}
