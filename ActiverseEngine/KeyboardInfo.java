package ActiverseEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Provides utility methods to retrieve information about keyboard input.
 * This class implements the KeyListener interface to handle keyboard events.
 *
 * @author Knivier
 * @version 1.3.2
 */
public class KeyboardInfo implements KeyListener {
    private static final boolean[] keys = new boolean[256];
    private static final Object keysLock = new Object();

    /**
     * Checks if a specific key is currently pressed.
     *
     * @param keyCode The integer code of the key to check.
     * @return true if the key is currently pressed, false otherwise.
     */
    public static boolean isKeyDown(int keyCode) {
        if (keyCode < 0 || keyCode >= keys.length) {
            return false;
        }
        synchronized (keysLock) {
            return keys[keyCode];
        }
    }
    
    /**
     * Internal method to set key state. Package-private for use by World.
     */
    static void setKeyState(int keyCode, boolean pressed) {
        if (keyCode >= 0 && keyCode < keys.length) {
            synchronized (keysLock) {
                keys[keyCode] = pressed;
            }
        }
    }

    /**
     * Called when a key is pressed.
     * Updates the state of the corresponding key in the keys array.
     *
     * @param e The KeyEvent object representing the key press event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        setKeyState(e.getKeyCode(), true);
    }

    /**
     * Called when a key is released.
     * Updates the state of the corresponding key in the keys array.
     *
     * @param e The KeyEvent object representing the key release event.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        setKeyState(e.getKeyCode(), false);
    }

    /**
     * Called when a key is typed.
     * This method is not used in this class implementation.
     *
     * @param e The KeyEvent object representing the key typed event.
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }
}
