package ActiverseEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents the world where actors interact.
 * This class extends JPanel and implements ActionListener, KeyListener.
 * The world has a fixed size and a black border, and can display a background image.
 */
public class World extends JPanel implements ActionListener, KeyListener {
    private final int fixedWidth;
    private final int fixedHeight;
    private final Timer timer;
    private final List<Actor> actors;
    private final List<String> loadedImages;
    private final JButton terminateButton;
    private final List<ActiverseSound> sounds;
    // Memory tracker
    private final MemoryTracker memoryTracker;
    private Image backgroundImage;
    private String displayText;
    private int textX;
    private int textY;
    private JButton debugButton;
    private boolean debugMode = false;
    // FPS tracking variables
    private int frames;
    private static int fps;
    private long lastFpsTime;

    /**
     * Constructs a new World with the specified dimensions and cell size.
     *
     * @param width    The width of the world (number of cells).
     * @param height   The height of the world (number of cells).
     * @param cellSize The size of each cell in pixels.
     */
    public World(int width, int height, int cellSize) {
        this.fixedWidth = width * cellSize;
        this.fixedHeight = height * cellSize;
        setPreferredSize(new Dimension(this.fixedWidth, this.fixedHeight));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        actors = new CopyOnWriteArrayList<>();
        loadedImages = new ArrayList<>();
        sounds = new ArrayList<>();
        timer = new Timer(1000 / 60, this);

        displayText = null;

        memoryTracker = new MemoryTracker();

        Properties props = loadProperties();

        boolean showDebug = Boolean.parseBoolean(props.getProperty("show_debug", "true"));

        if (showDebug) {
            debugButton = new JButton("Debug");
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

        terminateButton = new JButton("End");
        terminateButton.setFont(new Font("Arial", Font.PLAIN, 10));
        terminateButton.setPreferredSize(new Dimension(60, 20));
        terminateButton.setBounds(this.fixedWidth - 90, 50, 60, 20);
        terminateButton.addActionListener(e -> {
            stop();
            System.exit(0);
        });
        add(terminateButton);

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        lastFpsTime = System.nanoTime();
    }

    /**
     * Loads properties from Activerse.properties file.
     *
     * @return Properties object containing loaded properties.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream inStream = getClass().getClassLoader().getResourceAsStream("Activerse.properties")) {
            if (inStream != null) {
                props.load(inStream);
            } else {
                System.err.println("Activerse.properties not found or could not be loaded.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading Activerse.properties: " + e.getMessage());
        }
        return props;
    }

    /**
     * Sets the background image for the world.
     *
     * @param imagePath The path to the background image file.
     */
    public void setBackgroundImage(String imagePath) {
        backgroundImage = Toolkit.getDefaultToolkit().getImage(imagePath);
        loadedImages.add(imagePath);
    }

    /**
     * Updates the state of the world.
     */
    public void update() {
        for (Actor actor : actors) {
            actor.act();
        }
        memoryTracker.update();
    }

    /**
     * Adds an actor to the world at the specified position.
     *
     * @param actor The actor to add to the world.
     * @param x     The x-coordinate of the actor's position.
     * @param y     The y-coordinate of the actor's position.
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
     * Removes an actor from the world.
     *
     * @param actor The actor to remove from the world.
     */
    public void removeObject(Actor actor) {
        actors.remove(actor);
        if (actor.getImage() != null) {
            loadedImages.remove(actor.getImage().getPath());
        }
    }

    /**
     * Adds a sound to the world.
     *
     * @param sound The sound to add to the world.
     */
    public void addSound(ActiverseSound sound) {
        sounds.add(sound);
    }

    /**
     * Starts the simulation by starting the timer.
     */
    public void start() {
        timer.start();
    }

    /**
     * Stops the simulation by stopping the timer.
     */
    public void stop() {
        timer.stop();
    }
    public static int getFPS(){
        return fps;
    }
    /**
     * Sets the text to be displayed at the specified location.
     *
     * @param x    The x-coordinate of the text's position.
     * @param y    The y-coordinate of the text's position.
     * @param text The text to be displayed.
     */
    public void showText(int x, int y, String text) {
        this.textX = x;
        this.textY = y;
        this.displayText = text;
    }

    /**
     * Overrides the paintComponent method to paint the background image, all actors in the world, and the text.
     *
     * @param g The Graphics object used for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, fixedWidth, fixedHeight, this);
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
    }

    private void drawDebugInfo(Graphics g) {
        g.setColor(Color.BLACK);
        int y = 50;

        // Calculate FPS
        long now = System.nanoTime();
        if (now - lastFpsTime >= 1_000_000_000) {
            fps = frames;
            frames = 0;
            lastFpsTime = now;
        }
        g.drawString("FPS: " + fps + " @target " + memoryTracker.getTargetFPS(), 10, y);
        y += 20;

        g.drawString(memoryTracker.getMemoryUsagePerSecond(), 10, y);
        y += 20;

        for (Actor actor : actors) {
            String info = String.format("Actor at (%d, %d)", actor.getX(), actor.getY());
            boolean isColliding = checkCollision(actor);
            info += isColliding ? " - Colliding" : " - Not colliding";
            g.drawString(info, 10, y);
            y += 20;
        }

        StringBuilder imagesInfo = new StringBuilder("Loaded Images: ");
        for (String imagePath : loadedImages) {
            imagesInfo.append(imagePath).append(" ");
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
        g.drawString("Current Keys:", 10, y);
        y += 20;
        StringBuilder keysInfo = new StringBuilder();
        for (int i = 0; i < KeyboardInfo.keys.length; i++) {
            if (KeyboardInfo.keys[i]) {
                keysInfo.append(KeyEvent.getKeyText(i)).append(" ");
            }
        }
        g.drawString(keysInfo.toString(), 10, y);
    }

    private boolean checkCollision(Actor actor) {
        for (Actor other : actors) {
            if (actor != other && CollisionManager.intersects(actor, other)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Overrides the actionPerformed method to update the world and repaint it.
     *
     * @param e The ActionEvent object representing the timer event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != debugButton) {
            update();
            repaint();
        }
        frames++;
    }

    /**
     * Handles key press events.
     *
     * @param e The KeyEvent object representing the key press event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        KeyboardInfo.keys[e.getKeyCode()] = true;
    }

    /**
     * Handles key release events.
     *
     * @param e The KeyEvent object representing the key release event.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        KeyboardInfo.keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // To be implemented if needed
    }

    /**
     * Returns a list of actors in the world.
     *
     * @return A list of actors in the world.
     */
    public List<Actor> getActors() {
        return actors;
    }

    /**
     * Overrides the getPreferredSize method to ensure the world has a fixed size.
     *
     * @return The preferred size of the world.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(fixedWidth, fixedHeight);
    }
}
