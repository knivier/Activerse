package ActiverseEngine;

import ActiverseUtils.DelayScheduler;
import ActiverseUtils.ErrorLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private static final AtomicBoolean exitInProgress = new AtomicBoolean(false);

    /**
     * The main window, or null before the UI is created on the EDT.
     */
    public static JFrame getFrame() {
        return frame;
    }

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
        SwingUtilities.invokeLater(() -> {
            try {
                frame = new JFrame("Activerse Instance v1.4.1");
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        shutdownApplication();
                    }
                });
                frame.getContentPane().add(currentWorld, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
                frame.setResizable(false); 
                frame.setLocationRelativeTo(null);
                currentWorld.start();
                gameLoop = new GameLoop(currentWorld);
                gameLoop.start();
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
        }
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
        currentWorld = world;

        Runnable setupOnEDT = () -> {
            if (frame != null) {
                frame.getContentPane().removeAll();
                frame.getContentPane().add(currentWorld, BorderLayout.CENTER);
                frame.getContentPane().revalidate();
                frame.getContentPane().repaint();
            }
            currentWorld.start();
            gameLoop = new GameLoop(currentWorld);
            gameLoop.start();
            currentWorld.setFocusable(true);
            if (!currentWorld.requestFocusInWindow()) {
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
        };
        if (SwingUtilities.isEventDispatchThread()) {
            setupOnEDT.run();
        } else {
            SwingUtilities.invokeLater(setupOnEDT);
        }
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

    /**
     * Full process teardown: save, stop simulation, release thread pools, close the window, exit JVM.
     * May be called from the EDT, the game-loop thread, or elsewhere; work always runs on a dedicated thread
     * so this never joins the current thread (avoids deadlock).
     */
    public static void shutdownApplication() {
        new Thread(Activerse::runShutdownSequence, "ActiverseShutdown").start();
    }

    private static void runShutdownSequence() {
        if (!exitInProgress.compareAndSet(false, true)) {
            return;
        }
        try {
            World world = currentWorld;
            if (world != null) {
                try {
                    world.saveBeforeHalt();
                } catch (Exception e) {
                    ErrorLogger.reportException("1A", "IO", "shutdownApplication saveBeforeHalt", e);
                }
            }
            if (gameLoop != null) {
                gameLoop.stop();
                gameLoop = null;
            }
            if (world != null) {
                try {
                    world.stop();
                } catch (Exception e) {
                    ErrorLogger.reportException("1A", "IO", "shutdownApplication world.stop", e);
                }
                try {
                    world.shutdownApplicationResources();
                } catch (Exception e) {
                    ErrorLogger.reportException("1A", "IO", "shutdownApplicationResources", e);
                }
            }
            currentWorld = null;
            try {
                DelayScheduler.shutdown();
            } catch (Exception e) {
                ErrorLogger.reportException("1A", "IO", "DelayScheduler.shutdown", e);
            }
            if (frame != null) {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        frame.dispose();
                    });
                } catch (Exception e) {
                    ErrorLogger.reportException("1A", "IO", "frame.dispose", e);
                    frame.dispose();
                }
                frame = null;
            }
        } catch (Exception e) {
            ErrorLogger.reportException("1A", "IO", "shutdownApplication", e);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}