package ActiverseEngine;

import java.awt.*;

/**
 * UIBar - A progress bar component for World UI (health, stamina, etc.)
 * Allows drawing bars without using Graphics2D directly in game code
 * 
 * @author Knivier
 * @version 1.4.0
 */
public class UIBar {
    private int x;
    private int y;
    private int width;
    private int height;
    private double currentValue;
    private double maxValue;
    private Color fillColor;
    private Color backgroundColor;
    private Color borderColor;
    private String label;
    private boolean showLabel;
    
    public UIBar(int x, int y, int width, int height, Color fillColor) {
        this(x, y, width, height, fillColor, new Color(50, 50, 50), Color.BLACK);
    }
    
    public UIBar(int x, int y, int width, int height, Color fillColor, Color backgroundColor, Color borderColor) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.fillColor = fillColor;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.currentValue = 100;
        this.maxValue = 100;
        this.label = "";
        this.showLabel = false;
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw background
        g2d.setColor(backgroundColor);
        g2d.fillRect(x, y, width, height);
        
        // Draw fill based on percentage
        double percentage = currentValue / maxValue;
        int fillWidth = (int)(width * percentage);
        g2d.setColor(fillColor);
        g2d.fillRect(x, y, fillWidth, height);
        
        // Draw border
        g2d.setColor(borderColor);
        g2d.drawRect(x, y, width, height);
        
        // Draw label if enabled
        if (showLabel && !label.isEmpty()) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String displayText = label + ": " + (int)currentValue + "/" + (int)maxValue;
            g2d.drawString(displayText, x + 5, y + height - 5);
        }
    }
    
    public void setValue(double current, double max) {
        this.currentValue = Math.max(0, Math.min(current, max));
        this.maxValue = max;
    }
    
    // Getters and setters
    public void setLabel(String label, boolean show) {
        this.label = label;
        this.showLabel = show;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public void setColors(Color fill, Color background, Color border) {
        this.fillColor = fill;
        this.backgroundColor = background;
        this.borderColor = border;
    }
}
