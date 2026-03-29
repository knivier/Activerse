package ActiverseEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Provides utility methods to retrieve information about keyboard input.
 * This class implements the KeyListener interface to handle keyboard events.
 * Supports special keyword "shift" for any shift key.
 *
 * @author Knivier
 * @version 1.4.1
 */
public class KeyboardInfo implements KeyListener {
    private static final boolean[] keys = new boolean[256];
    private static final Object keysLock = new Object();
    private static boolean shiftPressed = false;

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
     * Checks if a specific key character is currently pressed.
     *
     * @param keyChar Character to check (e.g. 'w', '/')
     * @return true if the key is currently pressed
     */
    public static boolean isKeyDown(char keyChar) {
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(keyChar);
        return isKeyDown(keyCode);
    }

    /**
     * Checks a letter key ignoring case.
     *
     * @param letter Alphabetic character
     * @return true if either lowercase or uppercase variant is pressed
     */
    public static boolean isLetterDown(char letter) {
        if (!Character.isLetter(letter)) {
            return isKeyDown(letter);
        }
        char lower = Character.toLowerCase(letter);
        char upper = Character.toUpperCase(letter);
        return isKeyDown(lower) || isKeyDown(upper);
    }

    /**
     * Returns true when any character key in the set is currently down.
     *
     * @param keyChars Character keys to test
     */
    public static boolean isAnyKeyDown(char... keyChars) {
        if (keyChars == null) return false;
        for (char c : keyChars) {
            if (isKeyDown(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true when any key code in the set is currently down.
     *
     * @param keyCodes Virtual key codes to test
     */
    public static boolean isAnyKeyDown(int... keyCodes) {
        if (keyCodes == null) return false;
        for (int keyCode : keyCodes) {
            if (isKeyDown(keyCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true on the rising edge (up -> down) for a sampled key state.
     *
     * @param keyDown Current sampled key state
     * @param wasDown Previous sampled key state
     */
    public static boolean justPressed(boolean keyDown, boolean wasDown) {
        return keyDown && !wasDown;
    }

    /**
     * Returns true on the falling edge (down -> up) for a sampled key state.
     *
     * @param keyDown Current sampled key state
     * @param wasDown Previous sampled key state
     */
    public static boolean justReleased(boolean keyDown, boolean wasDown) {
        return !keyDown && wasDown;
    }
    
    /**
     * Checks if a specific key is pressed by name (supports special keywords)
     * Special keywords: "shift" for any shift key
     *
     * @param keyName The name of the key (e.g., "shift")
     * @return true if the key is currently pressed
     */
    public static boolean isKeyDown(String keyName) {
        if (keyName == null) return false;
        
        if (keyName.equalsIgnoreCase("shift")) {
            synchronized (keysLock) {
                return shiftPressed;
            }
        }
        
        return false;
    }

    /**
     * Appends debounced A-Z, 0-9 and optional space text input based on current key states.
     * <p>
     * The {@code keyWasDown} array must be indexed by {@link java.awt.event.KeyEvent} key codes.
     *
     * @param current Existing text value (null-safe)
     * @param keyWasDown Per-key previous-state array
     * @param allowSpace Whether space should be accepted
     * @return Updated text value
     */
    public static String appendAlphaNumeric(String current, boolean[] keyWasDown, boolean allowSpace) {
        String value = current != null ? current : "";
        if (keyWasDown == null) {
            return value;
        }

        value = appendLetters(value, keyWasDown);
        value = appendDigits(value, keyWasDown);

        if (allowSpace) {
            value = appendSpace(value, keyWasDown);
        }
        return value;
    }

    /**
     * Synchronizes debounced key states for A-Z, 0-9 and optional space.
     * Use this when entering a menu state to avoid immediate held-key injection.
     *
     * @param keyWasDown Per-key previous-state array
     * @param includeSpace Whether to synchronize space as well
     */
    public static void syncAlphaNumericState(boolean[] keyWasDown, boolean includeSpace) {
        if (keyWasDown == null) {
            return;
        }
        syncRange(KeyEvent.VK_A, KeyEvent.VK_Z, keyWasDown);
        syncRange(KeyEvent.VK_0, KeyEvent.VK_9, keyWasDown);
        if (includeSpace) {
            keyWasDown[KeyEvent.VK_SPACE] = isKeyDown(KeyEvent.VK_SPACE);
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
     * Internal method to set shift key state. Package-private for use by World.
     */
    static void setShiftPressed(boolean pressed) {
        synchronized (keysLock) {
            shiftPressed = pressed;
        }
    }

    private static String appendLetters(String current, boolean[] keyWasDown) {
        return appendRange(current, KeyEvent.VK_A, KeyEvent.VK_Z, keyWasDown, keyCode -> {
            char c = (char) ('a' + (keyCode - KeyEvent.VK_A));
            if (isKeyDown("shift")) {
                c = Character.toUpperCase(c);
            }
            return c;
        });
    }

    private static String appendDigits(String current, boolean[] keyWasDown) {
        return appendRange(current, KeyEvent.VK_0, KeyEvent.VK_9, keyWasDown,
                keyCode -> (char) ('0' + (keyCode - KeyEvent.VK_0)));
    }

    private static String appendSpace(String current, boolean[] keyWasDown) {
        boolean spaceDown = isKeyDown(KeyEvent.VK_SPACE);
        if (justPressed(spaceDown, keyWasDown[KeyEvent.VK_SPACE])) {
            current += " ";
        }
        keyWasDown[KeyEvent.VK_SPACE] = spaceDown;
        return current;
    }

    private static String appendRange(String current, int startKey, int endKey, boolean[] keyWasDown,
                                      java.util.function.IntFunction<Character> mapper) {
        String value = current;
        for (int keyCode = startKey; keyCode <= endKey; keyCode++) {
            boolean keyDown = isKeyDown(keyCode);
            if (justPressed(keyDown, keyWasDown[keyCode])) {
                value += mapper.apply(keyCode);
            }
            keyWasDown[keyCode] = keyDown;
        }
        return value;
    }

    private static void syncRange(int startKey, int endKey, boolean[] keyWasDown) {
        for (int keyCode = startKey; keyCode <= endKey; keyCode++) {
            keyWasDown[keyCode] = isKeyDown(keyCode);
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
        
        // Track shift key separately
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            synchronized (keysLock) {
                shiftPressed = true;
            }
        }
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
        
        // Track shift key separately
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
            synchronized (keysLock) {
                shiftPressed = false;
            }
        }
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
