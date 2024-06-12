import javax.swing.JFrame;

/**
 * Provides utility methods to start and stop the Activerse application.
 * This class contains static methods to manage the application lifecycle.
 *
 * @author Knivier
 */
public class Activerse {

    /**
     * Starts the Activerse application with the specified world.
     * Creates a JFrame and adds the world to it.
     *
     * @param world The world to start the application with.
     */
    public static void start(World world) {
        JFrame frame = new JFrame("Activerse Instance v1.0.0-alpha"); // Create a JFrame with a title
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set default close operation
        frame.add(world); // Add the world to the frame
        KeyboardInfo keyboardInfo = new KeyboardInfo();
        frame.addKeyListener(keyboardInfo); // Add key listener to capture keyboard input
        frame.pack(); // Resize the frame to fit its contents
        frame.setVisible(true); // Make the frame visible
        frame.requestFocus(); // Request focus to capture key events
        world.start(); // Start the world
    }

    /**
     * Stops the Activerse application by stopping the specified world.
     *
     * @param world The world to stop.
     */
    public static void stop(World world) {
        world.stop(); // Stop the world
    }
}
