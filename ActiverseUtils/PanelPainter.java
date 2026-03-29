package ActiverseUtils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * Reusable rounded panel painter for UI overlays.
 */
public final class PanelPainter {
    private PanelPainter() {}

    /**
     * Draws a rounded panel fill and optional border.
     *
     * @param g2d Graphics context to draw into
     * @param x Left coordinate
     * @param y Top coordinate
     * @param w Width in pixels
     * @param h Height in pixels
     * @param arc Arc diameter used for rounded corners
     * @param fillColor Fill color; when null, fill is skipped
     * @param borderColor Border color; when null, border is skipped
     * @param borderWidth Border thickness; border is skipped when {@code <= 0}
     */
    public static void drawRoundedPanel(Graphics2D g2d, int x, int y, int w, int h, int arc,
                                        Color fillColor, Color borderColor, float borderWidth) {
        if (fillColor != null) {
            g2d.setColor(fillColor);
            g2d.fillRoundRect(x, y, w, h, arc, arc);
        }
        if (borderColor != null && borderWidth > 0f) {
            Stroke old = g2d.getStroke();
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(borderWidth));
            g2d.drawRoundRect(x, y, w, h, arc, arc);
            g2d.setStroke(old);
        }
    }
}
