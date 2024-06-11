import java.awt.Graphics;

public abstract class Actor {
    private int x, y;
    private World world;
    private ActiverseImage image;

    public abstract void act();

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setImage(ActiverseImage image) {
        this.image = image;
    }

    public ActiverseImage getImage() {
        return image;
    }

    public void paint(Graphics g) {
        if (image != null) {
            g.drawImage(image.getImage(), x, y, null);
        }
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public boolean intersects(Actor other) {
        return CollisionManager.intersects(this, other);
    }
}
