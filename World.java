import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents the world where actors interact.
 * This class extends JPanel and implements ActionListener.
 * The world has a fixed size and a black border, and can display a background image.
 *
 * @author Knivier
 */
public class World extends JPanel implements ActionListener {
    private Timer timer;
    private List<Actor> actors;
    private final int fixedWidth;
    private final int fixedHeight;
    private Image backgroundImage;

    // Variables for showing text
    private String displayText;
    private int textX;
    private int textY;

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
        setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add a black border
        actors = new CopyOnWriteArrayList<>(); // Use CopyOnWriteArrayList to avoid ConcurrentModificationException
        timer = new Timer(50, this); // Create a timer with a delay of 50 milliseconds

        // Initialize text display variables
        displayText = null;
    }

    /**
     * Sets the background image for the world.
     *
     * @param imagePath The path to the background image file.
     */
    public void setBackgroundImage(String imagePath) {
        backgroundImage = Toolkit.getDefaultToolkit().getImage(imagePath);
    }

    /**
     * Adds an actor to the world at the specified position.
     *
     * @param actor The actor to add to the world.
     * @param x     The x-coordinate of the actor's position.
     * @param y     The y-coordinate of the actor's position.
     */
    public void addObject(Actor actor, int x, int y) {
        actor.setLocation(x, y); // Set the location of the actor
        actor.setWorld(this); // Set the world of the actor
        actors.add(actor); // Add the actor to the list of actors
    }

    /**
     * Removes an actor from the world.
     *
     * @param actor The actor to remove from the world.
     */
    public void removeObject(Actor actor) {
        actors.remove(actor); // Remove the actor from the list of actors
    }

    /**
     * Starts the simulation by starting the timer.
     */
    public void start() {
        timer.start(); // Start the timer
    }

    /**
     * Stops the simulation by stopping the timer.
     */
    public void stop() {
        timer.stop(); // Stop the timer
    }

    /**
     * Sets the text to be displayed at the specified location.
     *
     * @param x     The x-coordinate of the text's position.
     * @param y     The y-coordinate of the text's position.
     * @param text  The text to be displayed.
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

        // Draw the background image if it exists
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, fixedWidth, fixedHeight, this);
        }

        // Paint each actor in the world
        for (Actor actor : actors) {
            actor.paint(g);
        }

        // Draw the text if it exists
        if (displayText != null) {
            g.setColor(Color.BLACK); // Set the text color to black
            g.drawString(displayText, textX, textY); // Draw the text at the specified location
        }
    }

    /**
     * Overrides the actionPerformed method to update the world and repaint it.
     *
     * @param e The ActionEvent object representing the timer event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        for (Actor actor : actors) {
            actor.act(); // Perform actions for each actor in the world
        }
        repaint(); // Repaint the world to reflect the changes
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
