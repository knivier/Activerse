package ActiverseEngine;
import ActiverseUtils.MathUtils;
import java.awt.Point;

/**
 * Camera - Viewport management system for following actors and scrolling worlds
 * Essential for infinite world generation and smooth camera movement
 * 
 * @author Knivier
 * @version 1.4.1
 */
public class Camera {
    private double offsetX;
    private double offsetY;
    private int viewWidth;
    private int viewHeight;
    private Actor target;
    private float smoothness; // 0.0 (instant) to 1.0 (very smooth)
    private double targetOffsetX;
    private double targetOffsetY;
    
    // World bounds (optional, for clamping)
    private int worldWidth;
    private int worldHeight;
    private boolean clampToWorld;
    
    /**
     * Creates a camera with specified viewport size
     *
     * @param viewWidth Width of the viewport in pixels
     * @param viewHeight Height of the viewport in pixels
     */
    public Camera(int viewWidth, int viewHeight) {
        this(viewWidth, viewHeight, 0.1f);
    }
    
    /**
     * Creates a camera with smoothing
     *
     * @param viewWidth Width of the viewport
     * @param viewHeight Height of the viewport
     * @param smoothness Smoothness factor (0.0 to 1.0)
     */
    public Camera(int viewWidth, int viewHeight, float smoothness) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.smoothness = smoothness;
        this.offsetX = 0;
        this.offsetY = 0;
        this.targetOffsetX = 0;
        this.targetOffsetY = 0;
        this.clampToWorld = false;
    }
    
    /**
     * Sets the world bounds for camera clamping
     *
     * @param worldWidth Width of the world
     * @param worldHeight Height of the world
     */
    public void setWorldBounds(int worldWidth, int worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.clampToWorld = true;
    }
    
    /**
     * Sets the target actor for the camera to follow
     *
     * @param target The actor to follow
     */
    public void setTarget(Actor target) {
        this.target = target;
    }
    
    /**
     * Updates the camera position (call each frame)
     */
    public void update() {
        if (target != null) {
            // Center camera on target
            targetOffsetX = target.getX() - viewWidth / 2.0;
            targetOffsetY = target.getY() - viewHeight / 2.0;
        }
        
        // Clamp to world bounds if enabled
        if (clampToWorld) {
            targetOffsetX = Math.max(0, Math.min(targetOffsetX, worldWidth - viewWidth));
            targetOffsetY = Math.max(0, Math.min(targetOffsetY, worldHeight - viewHeight));
        }
        
        // Smooth camera movement
        offsetX += (targetOffsetX - offsetX) * smoothness;
        offsetY += (targetOffsetY - offsetY) * smoothness;
    }
    
    /**
     * Instantly moves camera to position
     *
     * @param x X position
     * @param y Y position
     */
    public void snapTo(double x, double y) {
        offsetX = x;
        offsetY = y;
        targetOffsetX = x;
        targetOffsetY = y;
    }
    
    /**
     * Converts world coordinates to screen coordinates
     *
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @return Screen coordinates
     */
    public Point worldToScreen(int worldX, int worldY) {
        return new Point(
            (int)(worldX - offsetX),
            (int)(worldY - offsetY)
        );
    }
    
    /**
     * Converts screen coordinates to world coordinates
     *
     * @param screenX Screen X coordinate
     * @param screenY Screen Y coordinate
     * @return World coordinates
     */
    public Point screenToWorld(int screenX, int screenY) {
        return new Point(
            (int)(screenX + offsetX),
            (int)(screenY + offsetY)
        );
    }
    
    /**
     * Checks if a point is visible in the viewport
     *
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @return true if point is visible
     */
    public boolean isVisible(int worldX, int worldY) {
        return worldX >= offsetX && worldX <= offsetX + viewWidth &&
               worldY >= offsetY && worldY <= offsetY + viewHeight;
    }
    
    /**
     * Checks if a rectangular area is visible in the viewport
     *
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @param width Width of area
     * @param height Height of area
     * @return true if area intersects viewport
     */
    public boolean isVisible(int worldX, int worldY, int width, int height) {
        return !(worldX + width < offsetX || 
                 worldX > offsetX + viewWidth ||
                 worldY + height < offsetY || 
                 worldY > offsetY + viewHeight);
    }
    
    // Getters
    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public int getViewWidth() { return viewWidth; }
    public int getViewHeight() { return viewHeight; }
    
    // Setters
    public void setSmoothness(float smoothness) {
        this.smoothness = MathUtils.clamp(smoothness, 0.0f, 1.0f);
    }
}
