package ActiverseUtils;

import java.awt.*;

/**
 * Utility class for creating and managing shapes for actors.
 * Provides convenient methods to build rectangles, circles, and other shapes
 * without requiring images.
 *
 * @author Knivier
 * @version 1.4.1
 */
public class Shaper {
    
    /**
     * Represents a rectangle shape configuration.
     * Used to store rectangle properties for actors.
     */
    public static class RectangleShape {
        private final int width;
        private final int height;
        private Color fillColor;
        private Color outlineColor;
        private int outlineWidth;
        
        /**
         * Creates a rectangle shape with the specified dimensions.
         *
         * @param width  The width of the rectangle
         * @param height The height of the rectangle
         */
        public RectangleShape(int width, int height) {
            this.width = width;
            this.height = height;
            this.fillColor = Color.BLACK;
            this.outlineColor = null;
            this.outlineWidth = 0;
        }
        
        /**
         * Sets the fill color of the rectangle.
         *
         * @param color The color to fill the rectangle with
         * @return This RectangleShape instance for method chaining
         */
        public RectangleShape withFillColor(Color color) {
            this.fillColor = color;
            return this;
        }
        
        /**
         * Sets the outline color of the rectangle.
         *
         * @param color The color for the rectangle outline
         * @return This RectangleShape instance for method chaining
         */
        public RectangleShape withOutlineColor(Color color) {
            this.outlineColor = color;
            return this;
        }
        
        /**
         * Sets the outline width of the rectangle.
         *
         * @param width The width of the outline in pixels
         * @return This RectangleShape instance for method chaining
         */
        public RectangleShape withOutlineWidth(int width) {
            this.outlineWidth = width;
            return this;
        }
        
        /**
         * Gets the width of the rectangle.
         *
         * @return The width
         */
        public int getWidth() {
            return width;
        }
        
        /**
         * Gets the height of the rectangle.
         *
         * @return The height
         */
        public int getHeight() {
            return height;
        }
        
        /**
         * Gets the fill color.
         *
         * @return The fill color
         */
        public Color getFillColor() {
            return fillColor;
        }
        
        /**
         * Gets the outline color.
         *
         * @return The outline color, or null if not set
         */
        public Color getOutlineColor() {
            return outlineColor;
        }
        
        /**
         * Gets the outline width.
         *
         * @return The outline width
         */
        public int getOutlineWidth() {
            return outlineWidth;
        }
        
        /**
         * Paints the rectangle on the given graphics context at the specified position.
         *
         * @param g The graphics context to draw on
         * @param x The x-coordinate of the rectangle
         * @param y The y-coordinate of the rectangle
         */
        public void paint(Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            
            // Draw fill
            if (fillColor != null) {
                g2d.setColor(fillColor);
                g2d.fillRect(x, y, width, height);
            }
            
            // Draw outline
            if (outlineColor != null && outlineWidth > 0) {
                g2d.setColor(outlineColor);
                g2d.setStroke(new BasicStroke(outlineWidth));
                g2d.drawRect(x, y, width, height);
            }
        }
        
        /**
         * Creates a bounding box rectangle for collision detection.
         *
         * @param x The x-coordinate of the rectangle
         * @param y The y-coordinate of the rectangle
         * @return A Rectangle representing the bounding box
         */
        public Rectangle getBoundingBox(int x, int y) {
            return new Rectangle(x, y, width, height);
        }
    }
    
    /**
     * Represents a circle shape configuration.
     */
    public static class CircleShape {
        private final int diameter;
        private Color fillColor;
        private Color outlineColor;
        private int outlineWidth;
        
        /**
         * Creates a circle shape with the specified diameter.
         *
         * @param diameter The diameter of the circle
         */
        public CircleShape(int diameter) {
            this.diameter = diameter;
            this.fillColor = Color.BLACK;
            this.outlineColor = null;
            this.outlineWidth = 0;
        }
        
        /**
         * Sets the fill color of the circle.
         *
         * @param color The color to fill the circle with
         * @return This CircleShape instance for method chaining
         */
        public CircleShape withFillColor(Color color) {
            this.fillColor = color;
            return this;
        }
        
        /**
         * Sets the outline color of the circle.
         *
         * @param color The color for the circle outline
         * @return This CircleShape instance for method chaining
         */
        public CircleShape withOutlineColor(Color color) {
            this.outlineColor = color;
            return this;
        }
        
        /**
         * Sets the outline width of the circle.
         *
         * @param width The width of the outline in pixels
         * @return This CircleShape instance for method chaining
         */
        public CircleShape withOutlineWidth(int width) {
            this.outlineWidth = width;
            return this;
        }
        
        /**
         * Gets the diameter of the circle.
         *
         * @return The diameter
         */
        public int getDiameter() {
            return diameter;
        }
        
        /**
         * Gets the fill color.
         *
         * @return The fill color
         */
        public Color getFillColor() {
            return fillColor;
        }
        
        /**
         * Gets the outline color.
         *
         * @return The outline color, or null if not set
         */
        public Color getOutlineColor() {
            return outlineColor;
        }
        
        /**
         * Gets the outline width.
         *
         * @return The outline width
         */
        public int getOutlineWidth() {
            return outlineWidth;
        }
        
        /**
         * Paints the circle on the given graphics context at the specified position.
         *
         * @param g The graphics context to draw on
         * @param x The x-coordinate of the circle (top-left corner of bounding box)
         * @param y The y-coordinate of the circle (top-left corner of bounding box)
         */
        public void paint(Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            
            // Draw fill
            if (fillColor != null) {
                g2d.setColor(fillColor);
                g2d.fillOval(x, y, diameter, diameter);
            }
            
            // Draw outline
            if (outlineColor != null && outlineWidth > 0) {
                g2d.setColor(outlineColor);
                g2d.setStroke(new BasicStroke(outlineWidth));
                g2d.drawOval(x, y, diameter, diameter);
            }
        }
        
        /**
         * Creates a bounding box rectangle for collision detection.
         *
         * @param x The x-coordinate of the circle
         * @param y The y-coordinate of the circle
         * @return A Rectangle representing the bounding box
         */
        public Rectangle getBoundingBox(int x, int y) {
            return new Rectangle(x, y, diameter, diameter);
        }
    }
    
    /**
     * Creates a new rectangle shape with the specified dimensions.
     *
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     * @return A RectangleShape instance
     */
    public static RectangleShape rectangle(int width, int height) {
        return new RectangleShape(width, height);
    }
    
    /**
     * Creates a new rectangle shape with dimensions scaled from an original size.
     * Useful for making shapes longer/wider by a percentage.
     *
     * @param originalWidth  The original width
     * @param originalHeight The original height
     * @param scaleFactor    The scale factor (e.g., 1.1 for 10% larger)
     * @return A RectangleShape instance with scaled dimensions
     */
    public static RectangleShape rectangleScaled(int originalWidth, int originalHeight, double scaleFactor) {
        return new RectangleShape(
            (int)(originalWidth * scaleFactor),
            (int)(originalHeight * scaleFactor)
        );
    }
    
    /**
     * Creates a new square shape with the specified side length.
     *
     * @param sideLength The length of each side
     * @return A RectangleShape instance
     */
    public static RectangleShape square(int sideLength) {
        return new RectangleShape(sideLength, sideLength);
    }
    
    /**
     * Creates a new circle shape with the specified diameter.
     *
     * @param diameter The diameter of the circle
     * @return A CircleShape instance
     */
    public static CircleShape circle(int diameter) {
        return new CircleShape(diameter);
    }
    
    /**
     * Creates a new circle shape with diameter calculated from radius.
     *
     * @param radius The radius of the circle
     * @return A CircleShape instance
     */
    public static CircleShape circleFromRadius(int radius) {
        return new CircleShape(radius * 2);
    }
}
