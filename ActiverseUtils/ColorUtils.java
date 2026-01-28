package ActiverseUtils;

import java.awt.Color;

/**
 * ColorUtils - Utility functions for color manipulation
 * 
 * @author Knivier
 * @version 1.4.1
 */
public class ColorUtils {
    
    /**
     * Darkens a color by a percentage
     *
     * @param color The color to darken
     * @param percent Percentage to darken (0.0 to 1.0)
     * @return Darkened color
     */
    public static Color darken(Color color, double percent) {
        int r = (int)(color.getRed() * (1 - percent));
        int g = (int)(color.getGreen() * (1 - percent));
        int b = (int)(color.getBlue() * (1 - percent));
        return new Color(
            MathUtils.clamp(r, 0, 255),
            MathUtils.clamp(g, 0, 255),
            MathUtils.clamp(b, 0, 255),
            color.getAlpha()
        );
    }
    
    /**
     * Lightens a color by a percentage
     *
     * @param color The color to lighten
     * @param percent Percentage to lighten (0.0 to 1.0)
     * @return Lightened color
     */
    public static Color lighten(Color color, double percent) {
        int r = (int)(color.getRed() + (255 - color.getRed()) * percent);
        int g = (int)(color.getGreen() + (255 - color.getGreen()) * percent);
        int b = (int)(color.getBlue() + (255 - color.getBlue()) * percent);
        return new Color(
            MathUtils.clamp(r, 0, 255),
            MathUtils.clamp(g, 0, 255),
            MathUtils.clamp(b, 0, 255),
            color.getAlpha()
        );
    }
    
    /**
     * Blends two colors
     *
     * @param color1 First color
     * @param color2 Second color
     * @param ratio Blend ratio (0.0 = all color1, 1.0 = all color2)
     * @return Blended color
     */
    public static Color blend(Color color1, Color color2, double ratio) {
        ratio = MathUtils.clamp(ratio, 0.0, 1.0);
        int r = (int)(color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int g = (int)(color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int b = (int)(color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
        int a = (int)(color1.getAlpha() * (1 - ratio) + color2.getAlpha() * ratio);
        return new Color(r, g, b, a);
    }
    
    /**
     * Sets the alpha (transparency) of a color
     *
     * @param color The color
     * @param alpha Alpha value (0-255)
     * @return Color with new alpha
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(
            color.getRed(),
            color.getGreen(),
            color.getBlue(),
            MathUtils.clamp(alpha, 0, 255)
        );
    }
    
    /**
     * Converts RGB to HSV
     *
     * @param color RGB color
     * @return float array [hue (0-360), saturation (0-1), value (0-1)]
     */
    public static float[] rgbToHsv(Color color) {
        float[] hsv = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
        hsv[0] *= 360; // Convert hue to degrees
        return hsv;
    }
    
    /**
     * Creates a color from HSV values
     *
     * @param hue Hue (0-360 degrees)
     * @param saturation Saturation (0.0-1.0)
     * @param value Value/brightness (0.0-1.0)
     * @return RGB color
     */
    public static Color hsvToRgb(float hue, float saturation, float value) {
        return Color.getHSBColor(hue / 360f, saturation, value);
    }
}
