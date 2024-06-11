import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Represents the world where actors interact.
 * This class extends JPanel and implements ActionListener.
 *
 * @author Knivier
 */
public class World extends JPanel implements ActionListener {
    private Timer timer; // Timer for updating the world
    private ArrayList<Actor> actors; // List of actors in the world

    /**
     * Constructs a new World with the specified dimensions and cell size.
     *
     * @param width    The width of the world (number of cells).
     * @param height   The height of the world (number of cells).
     * @param cellSize The size of each cell in pixels.
     */
    public World(int width, int height, int cellSize) {
        setPreferredSize(new Dimension(width * cellSize, height * cellSize));
        setBackground(Color.WHITE);
        actors = new ArrayList<>();
        timer = new Timer(50, this); // Create a timer with a delay of 50 milliseconds
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
     * Overrides the paintComponent method to paint all actors in the world.
     *
     * @param g The Graphics object used for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Actor actor : actors) {
            actor.paint(g); // Paint each actor in the world
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
}
