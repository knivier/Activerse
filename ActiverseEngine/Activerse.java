package ActiverseEngine;

import java.awt.*;
import javax.swing.*;

/**
 * Provides utility methods to start and stop the Activerse application.
 * This class contains static methods to manage the application lifecycle.
 * 
 * @author Knivier  
 * @version 1.2.2
 */
public class Activerse {
    private static World currentWorld;
    private static JFrame frame;
    private static GameLoop gameLoop;

    /**
     * Starts the Activerse application with the specified world.
     *
     * @param world The world to start the application with.
     * @throws IllegalArgumentException if the world is null.
     */
    public static void start(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        currentWorld = world;
        gameLoop = new GameLoop(currentWorld);
        new Thread(gameLoop).start();
        SwingUtilities.invokeLater(() -> {
            try {
                frame = new JFrame("Activerse Instance v1.3.1");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(currentWorld, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
                currentWorld.start(); // Start the world
            } catch (HeadlessException | IllegalArgumentException e) {
                System.out.println("1A.IO:(LN: SwingUtilities.invokeLater caught Exception at Activerse.start()) - ACEHS Error fetched and thrown; a specific error occurred. Please see the stack trace for more information. Contact ActiverseEngine support for bugs.");
                e.printStackTrace();
                stop(currentWorld);
            } catch (Exception e) {
                System.out.println("1A.IO:(LN: SwingUtilities.invokeLater caught Exception at Activerse.start()) - ACEHS Error fetched and thrown; an unidentified error occurred. Please see the stack trace for more information. Contact ActiverseEngine support for bugs.");
                e.printStackTrace();
                stop(currentWorld);
            }
        });
    }

    /**
     * Sets the world to the inputted world.
     *
     * @param world The new world to be set.
     * @throws IllegalArgumentException if the world is null.
     */
    public static void setWorld(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }
        if (currentWorld != null) {
            stop(currentWorld);
            frame.getContentPane().remove(currentWorld);
        }
        currentWorld = world;
        frame.getContentPane().add(currentWorld, BorderLayout.CENTER);
        frame.getContentPane().revalidate();
        frame.getContentPane().repaint();

        if (gameLoop != null) {
            gameLoop = new GameLoop(currentWorld);
            new Thread(gameLoop).start();
        }
        currentWorld.start();
    }

    /**
     * Stops the Activerse application by stopping the specified world.
     *
     * @param world The world to stop.
     */
    public static void stop(World world) {
        if (world != null) {
            try {
                world.stop();
            } catch (Exception e) {
                System.out.println("1A.IO:(LN: stop(World world)) - ACEHS Error fetched and thrown; an unidentified error occurred. Please see the stack trace for more information. Contact ActiverseEngine support for bugs.");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}