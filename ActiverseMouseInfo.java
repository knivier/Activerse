import java.awt.*;

/**
 * Provides utility methods to retrieve information about the mouse pointer location.
 *
 * @author Knivier
 */
public class ActiverseMouseInfo {

    /**
     * Retrieves the current location of the mouse pointer on the screen.
     *
     * @return A Point object representing the coordinates of the mouse pointer.
     */
    public static Point getMouseLocation() {
        // Get the current location of the mouse pointer using MouseInfo
        return MouseInfo.getPointerInfo().getLocation();
    }
}
