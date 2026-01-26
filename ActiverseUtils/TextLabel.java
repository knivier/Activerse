package ActiverseUtils;

import java.awt.*;

/**
 * TextLabel - A text display component for World UI
 * Allows drawing text without using Graphics2D directly in game code
 * 
 * @author Knivier
 * @version 1.4.0
 */
public class TextLabel {
    private String text;
    private int x;
    private int y;
    private Color color;
    private Font font;
    
    public TextLabel(String text, int x, int y) {
        this(text, x, y, Color.BLACK, new Font("Arial", Font.PLAIN, 12));
    }
    
    public TextLabel(String text, int x, int y, Color color) {
        this(text, x, y, color, new Font("Arial", Font.PLAIN, 12));
    }
    
    public TextLabel(String text, int x, int y, Color color, Font font) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
        this.font = font;
    }
    
    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setFont(font);
        g2d.drawString(text, x, y);
    }
    
    // Getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    
    public Font getFont() { return font; }
    public void setFont(Font font) { this.font = font; }
}
