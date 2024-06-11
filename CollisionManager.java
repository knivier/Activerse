import java.awt.Rectangle;

public class CollisionManager {
    public static boolean intersects(Actor a, Actor b) {
        Rectangle r1 = new Rectangle(a.getX(), a.getY(), a.getImage().getImage().getWidth(null), a.getImage().getImage().getHeight(null));
        Rectangle r2 = new Rectangle(b.getX(), b.getY(), b.getImage().getImage().getWidth(null), b.getImage().getImage().getHeight(null));
        return r1.intersects(r2);
    }
}
