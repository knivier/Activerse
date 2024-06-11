import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInfo implements KeyListener {
    private static boolean[] keys = new boolean[256];

    public static boolean isKeyDown(int keyCode) {
        return keys[keyCode];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
