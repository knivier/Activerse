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
        JFrame frame = new JFrame("Activerse Instance v1.0.2-beta");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(world);
        KeyboardInfo keyboardInfo = new KeyboardInfo();
        frame.addKeyListener(keyboardInfo);
        frame.pack();
        frame.setVisible(true);
        frame.requestFocus();
        world.start();
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
