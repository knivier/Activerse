package ActiverseEngine;

import javax.swing.*;
import java.awt.*;

/**
 * Provides utility methods to start and stop the Activerse application.
 * This class contains static methods to manage the application lifecycle.
 */
public class Activerse {
    private static World currentWorld;
    private static JFrame frame;

    /**
     * Starts the Activerse application with the specified world.
     * Creates a JFrame and adds the world to it.
     *
     * @param world The world to start the application with.
     */
    public static void start(World world) {
        frame = new JFrame("Activerse Instance v1.0.7-delta");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(world, BorderLayout.CENTER); // Add world to content pane
        KeyboardInfo keyboardInfo = new KeyboardInfo();
        frame.addKeyListener(keyboardInfo);
        frame.pack();
        frame.setVisible(true);
        frame.requestFocus();
        world.start(); // Start the world
        currentWorld = world; // Set current world
    }

    /**
     * Sets the world to the inputted world.
     *
     * @param world The new world to be set.
     */
    public static void setWorld(World world) {
        if (currentWorld != null) {
            currentWorld.stop();
            frame.getContentPane().remove(currentWorld);
        }
        currentWorld = world;
        frame.getContentPane().add(currentWorld, BorderLayout.CENTER);
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();
        currentWorld.start();
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
