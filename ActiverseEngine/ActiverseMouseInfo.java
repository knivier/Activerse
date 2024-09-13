package ActiverseEngine;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

/**
 * Provides utility methods to retrieve information about the mouse pointer location
 * and detect mouse clicks.
 * 
 * @author Knivier 
 * @version 1.2.2
 */
public class ActiverseMouseInfo implements MouseListener {
    private static boolean leftClick = false;
    private static boolean rightClick = false;

    public ActiverseMouseInfo(Component component) {
        component.addMouseListener(this);
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
     * Checks if the left mouse button is clicked.
     *
     * @return true if the left mouse button is clicked, false otherwise.
     */
    public static boolean isLeftClick() {
        return leftClick;
    }

    /**
     * Checks if the right mouse button is clicked.
     *
     * @return true if the right mouse button is clicked, false otherwise.
     */
    public static boolean isRightClick() {
        return rightClick;
    }

    
    /** 
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }

    
    /** 
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftClick = true;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightClick = true;
        }
    }

    
    /** 
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            leftClick = false;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            rightClick = false;
        }
    }

    
    /** 
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }
}