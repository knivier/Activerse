package ActiverseEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Provides utility methods to retrieve information about the mouse pointer location
 * and detect mouse clicks.
 *
 * @author Knivier
 * @version 1.3.2
 */
public class ActiverseMouseInfo implements MouseListener {
    private static volatile boolean leftClick = false;
    private static volatile boolean rightClick = false;
    private static final Object clickLock = new Object();
    private static Component componentReference = null;

    /**
     * @param component Constructor for the ActiverseMouseInfo class.
     */
    private ActiverseMouseInfo(Component component) {
        addMouseListenerToComponent(component);
    }

    /**
     * Creates a new instance of the ActiverseMouseInfo class with the specified component.
     *
     * @param component Core component
     * @return Returns the instance of the current mouse info
     */
    public static ActiverseMouseInfo createInstance(Component component) {
        ActiverseMouseInfo instance = new ActiverseMouseInfo(component);
        return instance;
    }

    /**
     * Sets the component reference for accurate mouse positioning
     *
     * @param component The component to use for mouse positioning
     */
    public static void setComponentReference(Component component) {
        componentReference = component;
    }

    /**
     * Retrieves the current location of the mouse pointer on the screen.
     *
     * @return A Point object representing the coordinates of the mouse pointer.
     */
    public static Point getMouseLocation() {
        return MouseInfo.getPointerInfo().getLocation();
    }
    
    /**
     * Gets the mouse location relative to the component, prioritizing offset issues
     *
     * @return The mouse location relative to the game component, or null if not available
     */
    public static Point getMouseLocationOnComponent() {
        if (componentReference == null) {
            return getMouseLocation();
        }
        
        try {
            Point screenLocation = MouseInfo.getPointerInfo().getLocation();
            Point componentLocation = componentReference.getLocationOnScreen();
            
            return new Point(
                screenLocation.x - componentLocation.x,
                screenLocation.y - componentLocation.y
            );
        } catch (Exception e) {
            return getMouseLocation();
        }
    }

    /**
     * Checks if the left mouse button is clicked.
     *
     * @return true if the left mouse button is clicked, false otherwise.
     */
    public static boolean isLeftClick() {
        synchronized (clickLock) {
            return leftClick;
        }
    }

    /**
     * Checks if the right mouse button is clicked.
     *
     * @return true if the right mouse button is clicked, false otherwise.
     */
    public static boolean isRightClick() {
        synchronized (clickLock) {
            return rightClick;
        }
    }

    /**
     * @param component Adds a mouse listener to the specified component.
     */
    private void addMouseListenerToComponent(Component component) {
        component.addMouseListener(this);
    }

    /**
     * Not in use method (built on in the future)
     *
     * @param e Mouse event
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }


    /**
     * Detects if a mouse button is pressed.
     *
     * @param e The event if a mouse is pressed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        synchronized (clickLock) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                leftClick = true;
            } else if (SwingUtilities.isRightMouseButton(e)) {
                rightClick = true;
            }
        }
    }


    /**
     * Detects if a mouse button is released.
     * Limitations for this method are greater than mousePressed
     *
     * @param e The event when mouse is released
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        synchronized (clickLock) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                leftClick = false;
            } else if (SwingUtilities.isRightMouseButton(e)) {
                rightClick = false;
            }
        }
    }


    /**
     * To be built in the future
     *
     * @param e Mouse Event, later implementated
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }

    /*
     * @param e
     * To be built in the future
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }
}