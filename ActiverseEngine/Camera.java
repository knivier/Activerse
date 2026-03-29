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
    private double prevOffsetX;
    private double prevOffsetY;
    private int viewWidth;
    private int viewHeight;
    private Actor target;
    private float smoothness; // 0.0 (instant) to 1.0 (very smooth)
    private double targetOffsetX;
    private double targetOffsetY;
    
    /** Zoom scale: 1.0 = default, >1 = zoom in, <1 = zoom out */
    private double zoom = 1.0;
    private static final double ZOOM_MIN = 0.6;
    private static final double ZOOM_MAX = 1.4;
    /** Multiplicative zoom step (logarithmic feel); each key press multiplies/divides by this */
    private static final double ZOOM_STEP_RATIO = 1.08;
    
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
     * Enables or disables clamping the view to {@link #setWorldBounds(int, int)}.
     * Use {@code false} for effectively infinite worlds.
     */
    public void setClampToWorldBounds(boolean clamp) {
        this.clampToWorld = clamp;
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
     * Updates the camera position (call each frame).
     * Keeps the target centered; when target is null, offset does not change.
     */
    public void update() {
        prevOffsetX = offsetX;
        prevOffsetY = offsetY;

        if (target != null) {
            // Center camera on target; visible world size is viewSize/zoom
            double halfViewW = viewWidth / (2.0 * zoom);
            double halfViewH = viewHeight / (2.0 * zoom);
            double centerX = target.getX() + target.getWidth() / 2.0;
            double centerY = target.getY() + target.getHeight() / 2.0;
            targetOffsetX = centerX - halfViewW;
            targetOffsetY = centerY - halfViewH;
        }
        
        // Clamp to world bounds if enabled
        if (clampToWorld) {
            double visibleW = viewWidth / zoom;
            double visibleH = viewHeight / zoom;
            targetOffsetX = MathUtils.clamp(targetOffsetX, 0, Math.max(0, worldWidth - visibleW));
            targetOffsetY = MathUtils.clamp(targetOffsetY, 0, Math.max(0, worldHeight - visibleH));
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
        prevOffsetX = x;
        prevOffsetY = y;
        offsetX = x;
        offsetY = y;
        targetOffsetX = x;
        targetOffsetY = y;
    }
    
    /**
     * Converts world coordinates to screen coordinates (accounts for zoom).
     *
     * @param worldX World X coordinate
     * @param worldY World Y coordinate
     * @return Screen coordinates
     */
    public Point worldToScreen(int worldX, int worldY) {
        return new Point(
            (int)((worldX - offsetX) * zoom),
            (int)((worldY - offsetY) * zoom)
        );
    }
    
    /**
     * Converts screen coordinates to world coordinates (accounts for zoom).
     *
     * @param screenX Screen X coordinate
     * @param screenY Screen Y coordinate
     * @return World coordinates
     */
    public Point screenToWorld(int screenX, int screenY) {
        return new Point(
            (int)(offsetX + screenX / zoom),
            (int)(offsetY + screenY / zoom)
        );
    }
    
    /**
     * Checks if a point is visible in the viewport (accounts for zoom).
     */
    public boolean isVisible(int worldX, int worldY) {
        double visibleW = viewWidth / zoom;
        double visibleH = viewHeight / zoom;
        return worldX >= offsetX && worldX <= offsetX + visibleW &&
               worldY >= offsetY && worldY <= offsetY + visibleH;
    }
    
    /**
     * Checks if a rectangular area is visible in the viewport (accounts for zoom).
     */
    public boolean isVisible(int worldX, int worldY, int width, int height) {
        double visibleW = viewWidth / zoom;
        double visibleH = viewHeight / zoom;
        return !(worldX + width < offsetX ||
                 worldX > offsetX + visibleW ||
                 worldY + height < offsetY ||
                 worldY > offsetY + visibleH);
    }

    /**
     * Same as {@link #isVisible(int, int, int, int)} but uses interpolated camera offsets
     * (match {@link #getInterpolatedOffsetX(double)} / {@link World#getRenderAlpha()}).
     */
    public boolean isVisible(int worldX, int worldY, int width, int height, double renderAlpha) {
        double ox = getInterpolatedOffsetX(renderAlpha);
        double oy = getInterpolatedOffsetY(renderAlpha);
        double visibleW = viewWidth / zoom;
        double visibleH = viewHeight / zoom;
        return !(worldX + width < ox ||
                 worldX > ox + visibleW ||
                 worldY + height < oy ||
                 worldY > oy + visibleH);
    }
    
    /**
     * Returns the camera X offset interpolated between previous and current state.
     * @param alpha Interpolation factor (0.0 = previous state, 1.0 = current state)
     */
    public double getInterpolatedOffsetX(double alpha) {
        return prevOffsetX + (offsetX - prevOffsetX) * alpha;
    }

    /**
     * Returns the camera Y offset interpolated between previous and current state.
     * @param alpha Interpolation factor (0.0 = previous state, 1.0 = current state)
     */
    public double getInterpolatedOffsetY(double alpha) {
        return prevOffsetY + (offsetY - prevOffsetY) * alpha;
    }

    /**
     * Converts world coordinates to screen coordinates using interpolated camera position.
     */
    public Point worldToScreenInterpolated(int worldX, int worldY, double alpha) {
        double interpX = getInterpolatedOffsetX(alpha);
        double interpY = getInterpolatedOffsetY(alpha);
        return new Point(
            (int)((worldX - interpX) * zoom),
            (int)((worldY - interpY) * zoom)
        );
    }

    // Getters
    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public int getViewWidth() { return viewWidth; }
    public int getViewHeight() { return viewHeight; }
    public double getZoom() { return zoom; }
    
    /**
     * Sets zoom level (clamped between ZOOM_MIN and ZOOM_MAX).
     * @param zoom Scale factor; 1.0 = default, &gt;1 = zoom in, &lt;1 = zoom out
     */
    public void setZoom(double zoom) {
        this.zoom = MathUtils.clamp(zoom, ZOOM_MIN, ZOOM_MAX);
    }
    
    /** Zoom in by a scaled factor (multiplicative / logarithmic feel). */
    public void zoomIn() {
        setZoom(zoom * ZOOM_STEP_RATIO);
    }
    
    /** Zoom out by a scaled factor (multiplicative / logarithmic feel). */
    public void zoomOut() {
        setZoom(zoom / ZOOM_STEP_RATIO);
    }
    
    /**
     * Apply a per-frame zoom factor for smooth continuous zoom while key is held.
     * Call every frame: factor &gt; 1 to zoom in, factor &lt; 1 to zoom out.
     * e.g. applySmoothZoom(1.012) when T held, applySmoothZoom(0.988) when G held
     */
    public void applySmoothZoom(double factor) {
        setZoom(zoom * factor);
    }
    
    // Setters
    public void setSmoothness(float smoothness) {
        this.smoothness = MathUtils.clamp(smoothness, 0.0f, 1.0f);
    }
}
