package ActiverseEngine;

import ActiverseUtils.ErrorLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Provides utility methods to start and stop the Activerse application.
 * This class contains static methods to manage the application lifecycle.
 *
 * @author Knivier
 * @version 1.4.1
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
                frame = new JFrame("Activerse Instance v1.4.1");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        if (currentWorld != null) {
                            currentWorld.saveBeforeHalt();
                        }
                    }
                });
                frame.getContentPane().add(currentWorld, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
                frame.setResizable(false); 
                frame.setLocationRelativeTo(null);
                currentWorld.start(); 
            } catch (HeadlessException | IllegalArgumentException e) {
                ErrorLogger.report("1A", "IO", "Activerse.start()",
                        "a specific error occurred. Please check the error details and ensure the environment supports windowed rendering.");
                stop(currentWorld);
            } catch (Exception e) {
                ErrorLogger.reportException("1A", "IO", "Activerse.start()", e);
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
            currentWorld.saveBeforeHalt();
            stop(currentWorld);
            if (frame != null) {
                frame.getContentPane().remove(currentWorld);
            }
        }
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
        currentWorld = world;
        if (frame != null) {
            frame.getContentPane().removeAll(); // Remove all components
            frame.getContentPane().add(currentWorld, BorderLayout.CENTER);
            frame.getContentPane().revalidate();
            frame.getContentPane().repaint();
        }
        
        // Create and start game loop
        gameLoop = new GameLoop(currentWorld);
        new Thread(gameLoop).start();
        
        // Start the world
        currentWorld.start();
        
        // Ensure the new world gets focus for keyboard input (must be done after adding to frame)
        // Use invokeLater to ensure this happens after the component is fully added and visible
        SwingUtilities.invokeLater(() -> {
            currentWorld.setFocusable(true);
            if (!currentWorld.requestFocusInWindow()) {
                // If focus request fails, try again after a short delay
                Timer focusTimer = new Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        currentWorld.requestFocusInWindow();
                        ((Timer) e.getSource()).stop();
                    }
                });
                focusTimer.setRepeats(false);
                focusTimer.start();
            }
        });
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
                ErrorLogger.reportException("1A", "IO", "stop(World world)", e);
                System.exit(0);
            }
        }
    }
}