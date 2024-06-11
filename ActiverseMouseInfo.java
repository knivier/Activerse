import java.awt.Point;
import java.awt.MouseInfo;

public class ActiverseMouseInfo {
    public static Point getMouseLocation() {
        return MouseInfo.getPointerInfo().getLocation();
    }
}
