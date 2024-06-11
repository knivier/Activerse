import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The base class for all actors in the world.
 * 
 * @author Knivier
 */
public abstract class Actor {
    private int x, y;
    private double direction; // Angle in radians
    private World world;
    private ActiverseImage image;

    /**
     * Performs the actor's action.
     */
    public abstract void act();

    /**
     * Sets the location of the actor.
     * 
     * @param x The x-coordinate of the new location.
     * @param y The y-coordinate of the new location.
     */
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the x-coordinate of the actor's location.
     * 
     * @return The x-coordinate of the actor's location.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of the actor's location.
     * 
     * @return The y-coordinate of the actor's location.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the image of the actor.
     * 
     * @param image The image to set for the actor.
     */
    public void setImage(ActiverseImage image) {
        this.image = image;
    }

    /**
     * Gets the image of the actor.
     * 
     * @return The image of the actor.
     */
    public ActiverseImage getImage() {
        return image;
    }

    /**
     * Draws the actor on the given graphics context.
     * 
     * @param g The graphics context to draw on.
     */
    public void paint(Graphics g) {
        if (image != null) {
            g.drawImage(image.getImage(), x, y, null);
        }
    }

    /**
     * Sets the world the actor belongs to.
     * 
     * @param world The world the actor belongs to.
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Gets the world the actor belongs to.
     * 
     * @return The world the actor belongs to.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Checks if this actor intersects with another actor.
     * 
     * @param other The other actor to check for intersection.
     * @return true if this actor intersects with the other actor, false otherwise.
     */
    public boolean intersects(Actor other) {
        Rectangle r1 = getBoundingBox();
        Rectangle r2 = other.getBoundingBox();
        return r1.intersects(r2);
    }

    /**
     * Gets the bounding box of the actor.
     * 
     * @return The bounding box of the actor.
     */
    protected Rectangle getBoundingBox() {
        return new Rectangle(x, y, getImage().getImage().getWidth(null), getImage().getImage().getHeight(null));
    }

    /**
     * Moves the actor in its current direction by the specified distance.
     * 
     * @param distance The distance to move the actor.
     */
    public void move(int distance) {
        int dx = (int) (distance * Math.cos(direction));
        int dy = (int) (distance * Math.sin(direction));
        int newX = x + dx;
        int newY = y + dy;
        // Check if the new position exceeds the world limits
        if (newX >= 0 && newX + getImage().getImage().getWidth(null) <= world.getWidth() &&
            newY >= 0 && newY + getImage().getImage().getHeight(null) <= world.getHeight()) {
            x = newX;
            y = newY;
        }
    }

    /**
     * Turns the actor by the specified angle.
     * 
     * @param angle The angle to turn the actor (in radians).
     */
    public void turn(double angle) {
        direction = (direction + angle) % (2 * Math.PI);
    }

    /**
     * Delays the execution of the next action by the specified milliseconds.
     * 
     * @param ms The delay in milliseconds.
     */
    public void delayNext(int ms) {
        new Thread(() -> {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            act(); // Execute the next action after the delay
        }).start();
    }
}
