package ActiverseEngine;

import ActiverseUtils.ErrorLogger;
import ActiverseUtils.MathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents the world where actors interact.
 * This class extends JPanel and implements ActionListener, KeyListener.
 * The world has a fixed size and a black border, and can display a background image.
 *
 * @author Knivier
 * @version 1.4.1
 */
public class World extends JPanel implements ActionListener, KeyListener {
    /**
     * Frames per second for the world update/render loop
     */
    private static int fps;
    /**
     * Number of update ticks completed since world start
     */
    private static int ticksDone = 0;
    /**
     * Fixed pixel width of the world
     */
    private final int fixedWidth;
    /**
     * Fixed pixel height of the world
     */
    private final int fixedHeight;
    /**
     * Timer for scheduling world updates
     */
    private final Timer timer;
    /**
     * List of all actors currently in the world
     */
    private final List<Actor> actors;
    /**
     * List of paths to loaded images in the world
     */
    private final List<String> loadedImages;
    /**
     * List of sounds currently managed by the world
     */
    private final List<ActiverseSound> sounds;
    /**
     * Tracks memory usage and performance statistics
     */
    private final MemoryTracker memoryTracker;
    /**
     * The background image displayed in the world
     */
    private Image backgroundImage;
    /**
     * Text to display on the world (if any)
     */
    private String displayText;
    /**
     * X-coordinate for displaying text
     */
    private int textX;
    /**
     * Y-coordinate for displaying text
     */
    private int textY;
    /**
     * Button to toggle debug mode
     */
    private JButton debugButton;
    /**
     * Whether debug mode is currently enabled
     */
    private boolean debugMode = false;
    /**
     * Whether dynamic lighting is enabled
     */
    private boolean dynamicLighting = false;

    private long lastFrameTime = System.currentTimeMillis();
    private int frameCount = 0;
    private int actualFPS = 0;

    /**
     * Constructor for the World class.
     * Initializes the world with the given width, height, and cell size.
     * Sets the preferred size, background color, and border.
     * Initializes the list of actors, images, and sounds.
     * Creates a timer for updating the world.
     * Loads properties and sets debug mode and dynamic lighting.
     * Adds a debug button and a terminate button to the world.
     * Adds key listener and sets focus to the world.
     *
     * @param width    The width of the world in cells.
     * @param height   The height of the world in cells.
     * @param cellSize The size of each cell in pixels.
     */
    public World(int width, int height, int cellSize) {
        this.fixedWidth = width * cellSize;
        this.fixedHeight = height * cellSize;
        setPreferredSize(new Dimension(this.fixedWidth, this.fixedHeight));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        actors = new CopyOnWriteArrayList<>();
        loadedImages = new CopyOnWriteArrayList<>();
        sounds = new CopyOnWriteArrayList<>();
        timer = new Timer(1000 / 60, this);

        displayText = null;

        memoryTracker = new MemoryTracker();

        boolean showDebug = ConfigPuller.getBoolean("show_debug", true);
        dynamicLighting = ConfigPuller.getBoolean("dynamic_lighting", false);

        if (showDebug) {
            debugButton = new JButton("Debug");
            debugButton.setFocusable(false); // keep keyboard focus on World for gameplay / menus
            debugButton.addActionListener(e -> {
                debugMode = !debugMode;
                requestFocusInWindow();
                repaint();
            });
            setLayout(null);
            int buttonWidth = 80;
            debugButton.setBounds(this.fixedWidth - buttonWidth - 10, 10, buttonWidth, 30);
            add(debugButton);
        }

        JButton terminateButton = new JButton("End");
        terminateButton.setFocusable(false);
        terminateButton.setFont(new Font("Arial", Font.PLAIN, 10));
        terminateButton.setPreferredSize(new Dimension(60, 20));
        terminateButton.setBounds(this.fixedWidth - 90, 50, 60, 20);
        terminateButton.addActionListener(e -> Activerse.shutdownApplication());
        add(terminateButton);

        setFocusable(true);
        // Tab must not trigger Swing focus traversal — otherwise focus jumps to child
        // JButtons and the World's KeyListener stops receiving letter keys (e.g. create-world name).
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        initializeKeyListener();
    }

    /**
     * Returns targeting FPS
     *
     * @return int
     */
    public static int getFPS() {
        return fps;
    }

    /**
     * @param fpsValue Targeted FPS value
     */
    public static void setFPS(int fpsValue) {
        fps = fpsValue;
    }

    /**
     * Sets default FPS value for Activerse (60)
     */

    public static void defaultFPS() {
        fps = 60;
    }

    /**
     * Gets ticks done in the engine
     *
     * @return int Ticks done
     */
    public static int getTicksDone() {
        return ticksDone;
    }

    private void initializeKeyListener() {
        addKeyListener(this);
    }

    /**
     * Sets background image of world subclasses
     *
     * @param imagePath Relative path for image to load as background image
     */
    public void setBackgroundImage(String imagePath) {
        backgroundImage = Toolkit.getDefaultToolkit().getImage(imagePath);
        loadedImages.add(imagePath);
    }

    /**
     * Called before the JVM exits, the window closes, or this world is replaced by another.
     * Subclasses override to persist player/world state synchronously.
     */
    public void saveBeforeHalt() {
    }

    /**
     * Called once during full application shutdown (after {@link #saveBeforeHalt()} and {@link #stop()}).
     * Override to release process-wide resources (e.g. thread pools).
     */
    protected void shutdownApplicationResources() {
    }

    /**
     * When true, {@link GameLoop} does not call {@link #update()}; the world's Swing {@link Timer}
     * on the EDT drives simulation instead. Subclasses that share mutable state between timer ticks
     * and the loop thread should return true to avoid concurrent double-updates.
     */
    protected boolean isSimulationDrivenBySwingTimer() {
        return false;
    }

    /**
     * Updates the world by calling the act method of each actor at a tick interval
     * Updates memory tracker
     *
     * @see MemoryTracker
     */
    public void update() {
        ticksDone++;
        // Copy-on-write: iterator snapshot is safe even if act() removes actors; do not wrap in
        // synchronized(this) — that would serialize the update thread with EDT paint and destroy FPS.
        for (Actor actor : actors) {
            if (!actor.isTickInert()) {
                actor.act();
            }
        }
        memoryTracker.update();
    }

    /**
     * Adds an actor to the world at the specified location
     *
     * @param actor The actor to add to the world
     * @param x     The x-coordinate of the actor
     * @param y     The y-coordinate of the actor
     * @see Actor
     */
    public void addObject(Actor actor, int x, int y) {
        actor.setLocation(x, y);
        actor.setWorld(this);
        actors.add(actor);
        if (actor.getImage() != null) {
            loadedImages.add(actor.getImage().getPath());
        }
    }

    /**
     * Removes an actor from the world
     *
     * @param actor The actor to remove from the world
     * @see Actor
     */
    public void removeObject(Actor actor) {
        if (actor != null) {
            actors.remove(actor);
            if (actor.getImage() != null) {
                loadedImages.remove(actor.getImage().getPath());
            }
            actor.setWorld(null); // Clear world reference
        }
    }

    /**
     * Removes many actors in one batch (used for chunk unload). Prefer over many {@link #removeObject} calls.
     */
    protected void removeActorsBulk(Collection<Actor> toRemove) {
        if (toRemove == null || toRemove.isEmpty()) {
            return;
        }
        for (Actor actor : toRemove) {
            if (actor == null) {
                continue;
            }
            if (actor.getImage() != null) {
                loadedImages.remove(actor.getImage().getPath());
            }
            actor.setWorld(null);
        }
        actors.removeAll(toRemove);
    }
    
    /**
     * Clears all actors from the world and cleans up resources.
     */
    public void clear() {
        for (Actor actor : actors) {
            if (actor != null) {
                actor.setWorld(null);
            }
        }
        actors.clear();
        loadedImages.clear();
    }

    /**
     * Adds a sound to the world
     *
     * @param sound The sound to add to the world
     * @see ActiverseSound
     */
    public void addSound(ActiverseSound sound) {
        sounds.add(sound);
    }

    /**
     * Starts the world timer
     *
     * @see Timer
     */
    public void start() {
        if (isSimulationDrivenBySwingTimer()) {
            timer.start();
        }
    }

    /**
     * Stops the world timer and cleans up resources
     *
     * @see Timer
     */
    public void stop() {
        timer.stop();
        // Clean up sounds
        for (ActiverseSound sound : sounds) {
            if (sound != null) {
                sound.dispose();
            }
        }
        sounds.clear();
    }

    /**
     * Pauses the world timer
     *
     * @see Timer
     */
    public void pause() {
        timer.stop();
    }

    /**
     * Resumes the world timer
     *
     * @see Timer
     */
    public void resume() {
        timer.start();
    }

    /**
     * Shows text on the world at the specified location
     *
     * @param x    The x-coordinate of the text
     * @param y    The y-coordinate of the text
     * @param text The text to display
     */
    public void showText(int x, int y, String text) {
        this.textX = x;
        this.textY = y;
        this.displayText = text;
    }

    /**
     * Paints the world by drawing the background image, actors, and debug info
     * Applies dynamic lighting if enabled
     *
     * @param g The graphics object to draw on
     * @see Graphics
     * @see Actor
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, fixedWidth, fixedHeight, this);
        }
        // Background image is optional - if null, just use the default background color

        if (dynamicLighting) {
            applyDynamicLighting(g);
        }

        for (Actor actor : actors) {
            actor.paint(g);
        }

        if (displayText != null) {
            g.setColor(Color.BLACK);
            g.drawString(displayText, textX, textY);
        }

        if (debugMode) {
            drawDebugInfo(g);
        }

        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= 1000) {
            actualFPS = frameCount;
            frameCount = 0;
            lastFrameTime = currentTime;
        }

    }

    /**
     * Applies dynamic lighting to the world.
     * Supports multiple light sources, flicker, color temperature, spotlights, and soft shadows.
     * Actors can cast soft shadows based on their bounding box and light direction.
     *
     * @param g The graphics object to draw on
     */
    private void applyDynamicLighting(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Lighting buffer for compositing
        BufferedImage lightingBuffer = new BufferedImage(fixedWidth, fixedHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gLighting = lightingBuffer.createGraphics();

        // Clear buffer
        gLighting.setComposite(AlphaComposite.Clear);
        gLighting.fillRect(0, 0, fixedWidth, fixedHeight);
        gLighting.setComposite(AlphaComposite.SrcOver);

        // Example: dynamic, flickering, colored, and directional lights
        List<LightSource> lightSources = List.of(
                new LightSource(fixedWidth / 2, fixedHeight / 2, Color.YELLOW, 300, 1.0f, true, 0f, (float) (2 * Math.PI), 4000f), // Flickering warm omni
                new LightSource(fixedWidth / 3, fixedHeight / 3, Color.CYAN, 200, 0.7f, false, (float) (Math.PI / 4), (float) (Math.PI / 2), 8000f), // Cool spotlight
                new LightSource(fixedWidth * 2 / 3, fixedHeight * 2 / 3, Color.MAGENTA, 250, 0.9f, false, (float) (Math.PI), (float) (Math.PI), 6500f) // Neutral semi-spot
        );

        // Update flicker and draw each light
        for (LightSource light : lightSources) {
            light.updateFlicker();
            Color tempColor = light.getTemperatureColor();
            float alpha = Math.min(1.0f, Math.max(0f, light.intensity));
            int centerAlpha = (int) (180 * alpha);

            // Spotlight or omni
            if (light.spread < 2 * Math.PI) {
                // Draw spotlight as a pie-shaped radial gradient
                BufferedImage spot = new BufferedImage(fixedWidth, fixedHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D gSpot = spot.createGraphics();
                gSpot.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create a radial gradient for the cone
                Point2D center = new Point2D.Float(light.x, light.y);
                float[] dist = {0f, 1f};
                Color[] colors = {
                        new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), centerAlpha),
                        new Color(0, 0, 0, 0)
                };
                RadialGradientPaint rgp = new RadialGradientPaint(center, light.radius, dist, colors);
                gSpot.setPaint(rgp);

                // Draw the cone
                Shape cone = createConeShape(light.x, light.y, light.radius, light.direction, light.spread);
                gSpot.setClip(cone);
                gSpot.fillRect(light.x - light.radius, light.y - light.radius, light.radius * 2, light.radius * 2);
                gSpot.dispose();

                gLighting.drawImage(spot, 0, 0, null);
            } else {
                // Omni light as radial gradient
                RadialGradientPaint gradient = new RadialGradientPaint(
                        new Point2D.Float(light.x, light.y), light.radius,
                        new float[]{0f, 1f},
                        new Color[]{
                                new Color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), centerAlpha),
                                new Color(0, 0, 0, 0)
                        }
                );
                gLighting.setPaint(gradient);
                gLighting.fillOval(light.x - light.radius, light.y - light.radius, light.radius * 2, light.radius * 2);
            }
        }

        // Draw soft shadows for each actor and light
        for (Actor actor : actors) {
            Rectangle bounds = actor.getBoundingBox();
            int actorCenterX = bounds.x + bounds.width / 2;
            int actorCenterY = bounds.y + bounds.height / 2;

            for (LightSource light : lightSources) {
                double dx = actorCenterX - light.x;
                double dy = actorCenterY - light.y;
                double distance = MathUtils.distance(0, 0, dx, dy);

                if (distance < light.radius && light.isWithinCone(actorCenterX, actorCenterY)) {
                    // Shadow direction: away from light
                    double angle = Math.atan2(dy, dx);
                    int shadowLength = (int) (60 * (1.0 - distance / light.radius) * light.intensity);

                    // Soft shadow polygon (project actor's bounding box away from light)
                    Polygon shadowPoly = new Polygon();
                    for (int i = 0; i < 4; i++) {
                        int bx = (i < 2) ? bounds.x : bounds.x + bounds.width;
                        int by = (i % 2 == 0) ? bounds.y : bounds.y + bounds.height;
                        int sx = (int) (bx + shadowLength * Math.cos(angle));
                        int sy = (int) (by + shadowLength * Math.sin(angle));
                        shadowPoly.addPoint(sx, sy);
                    }
                    // Fade shadow with distance and alpha
                    GradientPaint shadowPaint = new GradientPaint(
                            actorCenterX, actorCenterY, new Color(0, 0, 0, 80),
                            actorCenterX + (int) (shadowLength * Math.cos(angle)),
                            actorCenterY + (int) (shadowLength * Math.sin(angle)),
                            new Color(0, 0, 0, 0)
                    );
                    gLighting.setPaint(shadowPaint);
                    gLighting.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f * light.intensity));
                    gLighting.fillPolygon(shadowPoly);
                }
            }
        }

        // Optional: global ambient darkness overlay for more dramatic effect
        gLighting.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        gLighting.setColor(new Color(0, 0, 0, 180));
        gLighting.fillRect(0, 0, fixedWidth, fixedHeight);

        // Blend the lighting buffer onto the main graphics
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.drawImage(lightingBuffer, 0, 0, null);

        gLighting.dispose();
        g2d.dispose();
        lightingBuffer = null; // Help GC
    }

    /**
     * Helper to create a cone shape for spotlights.
     */
    private Shape createConeShape(int x, int y, int radius, float direction, float spread) {
        int segments = 60;
        double startAngle = direction - spread / 2;
        double endAngle = direction + spread / 2;
        Polygon cone = new Polygon();
        cone.addPoint(x, y);
        for (int i = 0; i <= segments; i++) {
            double angle = startAngle + (endAngle - startAngle) * i / segments;
            int px = (int) (x + radius * Math.cos(angle));
            int py = (int) (y + radius * Math.sin(angle));
            cone.addPoint(px, py);
        }
        return cone;
    }

    /**
     * Calculates the distance between two points, based on the Euclidean distance formula.
     * This method is useful for determining how far apart two points are in the world.
     * You're welcome to create your own distance calculation method, but this one is provided for convenience.
     * Access via: `World.calculateDistance(int x1, int y1, int x2, int y2)`.
     * If you do not have a set world, you can create a "empty" world with `new World(0, 0, 1)` and use this method.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return int Distance between two points as an Activerse unit relative
     */
    public float calculateDistance(int x1, int y1, int x2, int y2) {
        return (float) MathUtils.distance(x1, y1, x2, y2);
    }

    /**
     * Draws debug information on the world
     * Debug information can be relayed to logs.log if enabled
     * Displays FPS, memory usage, actor positions, loaded images, playing sounds, and current keys
     * Only occurs if property file allows for logging
     *
     * @param g The graphics object to draw on
     * @see logs.log
     */
    private void drawDebugInfo(Graphics g) {
        g.setColor(Color.BLACK);
        int y = 50;

        g.drawString("FPS: " + actualFPS, 10, y);
        y += 15;
        y += 20;
        g.drawString(" Ticks: " + ticksDone, 10, 60);

        g.drawString(memoryTracker.getMemoryUsagePerSecond(), 10, y);
        y += 20;

        for (Actor actor : actors) {
            if (!actor.isStatic()) {
                String info = String.format("Actor at (%d, %d)", actor.getX(), actor.getY());
                boolean isColliding = checkCollision(actor);
                info += isColliding ? " - Collides" : " - Not colliding";
                g.drawString(info, 10, y);
                y += 20;
            }
        }

        // Count occurrences of each image file
        java.util.Map<String, Integer> imageCountMap = new java.util.LinkedHashMap<>();
        for (String imagePath : loadedImages) {
            String fileName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
            imageCountMap.put(fileName, imageCountMap.getOrDefault(fileName, 0) + 1);
        }
        StringBuilder imagesInfo = new StringBuilder("Loaded Images: ");
        for (java.util.Map.Entry<String, Integer> entry : imageCountMap.entrySet()) {
            String imageName = entry.getKey();
            boolean exists = getClass().getClassLoader().getResource(imageName) != null;
            // Check if the image resource exists in the classpath
            // py ahh formatting
            imagesInfo.append(imageName);
            if (!exists) {
                imagesInfo.append("x MIA");
                ErrorLogger.report("10A", "IN", "drawDebugInfo()", "an image file is missing from the classpath: " + imageName + ". Please ensure the image exists in the resources directory.", "2A", "OUT");
            }
            if (entry.getValue() > 1) {
                imagesInfo.append("x").append(entry.getValue());
            }
            imagesInfo.append(" ");
        }
        g.drawString(imagesInfo.toString(), 10, y);

        y += 20;
        g.drawString("Playing Sounds:", 10, y);
        y += 20;
        for (ActiverseSound sound : sounds) {
            if (sound.isPlaying()) {
                g.drawString("Sound: " + sound.getFilename(), 10, y);
                y += 20;
            }
        }

        y += 20;
        g.drawString("Active Keys:", 10, y);
        y += 20;
        StringBuilder keysInfo = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            if (KeyboardInfo.isKeyDown(i)) {
                keysInfo.append(KeyEvent.getKeyText(i)).append(" ");
            }
        }
        g.drawString(keysInfo.toString(), 10, y);
    }

    /**
     * Checks if an actor is colliding with another actor
     *
     * @param actor The actor to check for collision
     * @return true if the actor is colliding with another actor, false otherwise
     * @see Actor
     */
    private boolean checkCollision(Actor actor) {
        for (Actor other : actors) {
            if (actor != other && CollisionManager.intersects(actor, other)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the world and repaints it
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isSimulationDrivenBySwingTimer()) {
            return;
        }
        if (e.getSource() == debugButton) {
            return;
        }
        update();
        repaint();
    }

    /**
     * Handles key presses
     */
    @Override
    public void keyPressed(KeyEvent e) {
        KeyboardInfo.setKeyState(e.getKeyCode(), true);
        // Also update shift key tracking in KeyboardInfo
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            KeyboardInfo.setShiftPressed(true);
        }
    }

    /**
     * Handles key releases
     */
    @Override
    public void keyReleased(KeyEvent e) {
        KeyboardInfo.setKeyState(e.getKeyCode(), false);
        // Also update shift key tracking in KeyboardInfo
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            KeyboardInfo.setShiftPressed(false);
        }
    }

    /**
     * Handles key typing
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // To be implemented if needed
    }

    /**
     * Returns the list of actors in the world
     *
     * @return The list of actors in the world
     * @see Actor
     */
    public List<Actor> getActors() {
        return actors;
    }
    
    /**
     * Returns all actors of a specific type
     *
     * @param type The class type to filter by
     * @return List of actors matching the type
     */
    public <T extends Actor> List<T> getActorsByType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Actor actor : actors) {
            if (type.isInstance(actor)) {
                result.add(type.cast(actor));
            }
        }
        return result;
    }
    
    /**
     * Gets actors within a specific region
     *
     * @param x X coordinate of region
     * @param y Y coordinate of region
     * @param width Width of region
     * @param height Height of region
     * @return List of actors in the region
     */
    public List<Actor> getActorsInRegion(int x, int y, int width, int height) {
        List<Actor> result = new ArrayList<>();
        Rectangle region = new Rectangle(x, y, width, height);
        for (Actor actor : actors) {
            if (region.intersects(actor.getBoundingBox())) {
                result.add(actor);
            }
        }
        return result;
    }
    
    /**
     * Finds the nearest actor of a specific type to a given position
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param type The class type to search for
     * @param maxDistance Maximum search distance (0 for unlimited)
     * @return The nearest actor of the type, or null if none found
     */
    public <T extends Actor> T getNearestActor(int x, int y, Class<T> type, double maxDistance) {
        T nearest = null;
        double nearestDist = maxDistance > 0 ? maxDistance : Double.MAX_VALUE;

        for (Actor actor : actors) {
            if (type.isInstance(actor)) {
                int dx = actor.getX() - x;
                int dy = actor.getY() - y;
                double dist = MathUtils.distance(0, 0, dx, dy);

                if (dist < nearestDist) {
                    nearestDist = dist;
                    nearest = type.cast(actor);
                }
            }
        }
        return nearest;
    }

    /**
     * Returns the list of loaded images in the world
     *
     * @return The list of loaded images in the world
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(fixedWidth, fixedHeight);
    }

    /**
     * Light source class for dynamic lighting.
     * Now supports intensity, flicker, directionality, and color temperature.
     */
    private static class LightSource {
        int x, y, radius;
        Color color;
        float intensity; // 0.0 (off) to 1.0 (full)
        boolean flicker;
        float direction; // angle in radians, for directional lights
        float spread;    // spread angle in radians for spotlights
        float colorTemperature; // in Kelvin, for color tinting

        LightSource(int x, int y, Color color, int radius) {
            this(x, y, color, radius, 1.0f, false, 0f, (float) (2 * Math.PI), 6500f);
        }

        LightSource(int x, int y, Color color, int radius, float intensity, boolean flicker, float direction, float spread, float colorTemperature) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.radius = radius;
            this.intensity = intensity;
            this.flicker = flicker;
            this.direction = direction;
            this.spread = spread;
            this.colorTemperature = colorTemperature;
        }

        /**
         * Optionally simulate flicker by randomizing intensity.
         */
        void updateFlicker() {
            if (flicker) {
                this.intensity = 0.7f + (float) Math.random() * 0.3f;
            }
        }

        /**
         * Returns a color adjusted for color temperature.
         */
        Color getTemperatureColor() {
            // Simple approximation: shift color towards warm/cool
            float t = (colorTemperature - 1000f) / 9000f; // 1000K to 10000K
            t = Math.max(0, Math.min(1, t));
            int r = (int) (color.getRed() * (1 - t) + 255 * t);
            int g = (int) (color.getGreen() * (1 - t) + 200 * t);
            int b = (int) (color.getBlue() * (1 - t) + 150 * (1 - t));
            return new Color(r, g, b, color.getAlpha());
        }

        /**
         * Checks if a point is within the spotlight cone.
         */
        boolean isWithinCone(int px, int py) {
            if (spread >= 2 * Math.PI) return true; // omni
            double angleToPoint = Math.atan2(py - y, px - x);
            double diff = Math.abs(angleToPoint - direction);
            while (diff > Math.PI) diff -= 2 * Math.PI;
            return Math.abs(diff) <= spread / 2;
        }
    }
}
