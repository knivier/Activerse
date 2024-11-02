package ActiverseEngine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * The base class for all actors in the world.
 */
public abstract class Actor {
    protected double direction; 
    private int x, y;
    private World world;
    private ActiverseImage image;
    private double velocityX, velocityY;
    private int height; 
    private int width; 

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
     * Sets the x-coordinate of the actor's location.
     *
     * @param x The x-coordinate to set.
     */
    public void setX(int x) {
        this.x = x;
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
     * Sets the y-coordinate of the actor's location.
     *
     * @param y The y-coordinate to set.
     */
    public void setY(int y) {
        this.y = y;
    }
    public int getWidth() {
        return width;
    }
    /**
     * Gets the image of the actor.
     *
     * @return The image of the actor, or null if no image is set.
     */
    public ActiverseImage getImage() {
        if (image == null) {
            System.err.println("Warning: Image not set for actor.");
            return null;
        }
        return image;
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
     * Draws the actor on the given graphics context.
     *
     * @param g The graphics context to draw on.
     */
    public void paint(Graphics g) {
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g;
            width = image.getImage().getWidth(null);
            int height = image.getImage().getHeight(null);            AffineTransform old = g2d.getTransform();

            g2d.rotate(direction, x + (double) width / 2, y + (double) height / 2);
            g2d.drawImage(image.getImage(), x, y, null);

            g2d.setTransform(old);
        } else {
            System.err.println("5A.OUT-CONNTO-2A.OUT:(LN: paint(Graphics g) - ACEHS Error thrown; image is null. Please check the image path and try again.");
        }
    }

    /**
     * Gets the world that the actor belongs to.
     *
     * @return The world that the actor belongs to.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Sets the world that the actor belongs to.
     *
     * @param world The world that the actor belongs to.
     */
    public void setWorld(World world) {
        this.world = world;
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
        ActiverseImage img = getImage();
        if (img != null) {
            return new Rectangle(x, y, img.getImage().getWidth(null), img.getImage().getHeight(null));
        } else {
            return new Rectangle(x, y, 0, 0);
        }
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
     * Finds the intesrsectiuon of the actor with another actor then returns it
     * @return The intersecting actor, or null if no intersection is found.
     */
    public Actor getOneIntersectingObject() {
        List<Actor> actors = getWorld().getActors(); // Assume getActors() returns a List<Actor>
        for (Actor other : actors) {
            // Avoid checking against itself
            if (other != this && CollisionManager.intersects(this, other)) {
                return other; // Return the first intersecting actor
            }
        }

        return null; // No intersection found
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
                System.err.println("5A.IO:(LN: delayNext(int ms) - ACEHS Error thrown; an error occurred while delaying the next action.");
            }
            act();
        }).start();
    }

    /**
     * Checks if a specific key is currently pressed.
     *
     * @param key The character representing the key to check.
     * @return true if the key is currently pressed, false otherwise.
     */
    public boolean keyIsDown(char key) {
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(key);
        return KeyboardInfo.isKeyDown(keyCode);
    }

    /**
     * Gets the horizontal velocity of the actor.
     *
     * @return The horizontal velocity of the actor.
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Sets the horizontal velocity of the actor.
     *
     * @param velocityX The horizontal velocity to set.
     */
    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    /**
     * Gets the vertical velocity of the actor.
     *
     * @return The vertical velocity of the actor.
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Sets the vertical velocity of the actor.
     *
     * @param velocityY The vertical velocity to set.
     */
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    /**
     * Gets the height of the actor.
     *
     * @return The height of the actor.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the actor.
     *
     * @param height The height to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the direction of the actor.
     * @author Knivier
     * @param other The actor to follow
     * @param x The number of iterations to follow the actor (seconds)
     */
    public void useAStar(Actor other, int x) {
    // Define start and target positions
    int startX = this.getX();
    int startY = this.getY();
    int targetX = other.getX();
    int targetY = other.getY();

    // Define open and closed lists for A*
    PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
    Set<Node> closedList = new HashSet<>();

    // Start node
    Node startNode = new Node(startX, startY, null, 0, calculateHeuristic(startX, startY, targetX, targetY));
    openList.add(startNode);

    // A* algorithm
    while (!openList.isEmpty() && x > 0) {
        Node currentNode = openList.poll();
        
        // If target is reached
        if (currentNode.x == targetX && currentNode.y == targetY) {
            followPath(currentNode);
            return;
        }

        closedList.add(currentNode);

        // Explore neighbors (up, down, left, right)
        for (int[] direction : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
            int neighborX = currentNode.x + direction[0];
            int neighborY = currentNode.y + direction[1];

            if (closedList.contains(new Node(neighborX, neighborY))) continue;

            double gCost = currentNode.gCost + 1;
            double hCost = calculateHeuristic(neighborX, neighborY, targetX, targetY);
            double fCost = gCost + hCost;

            Node neighborNode = new Node(neighborX, neighborY, currentNode, gCost, fCost);

            if (openList.stream().anyMatch(n -> n.equals(neighborNode) && n.gCost <= gCost)) continue;

            openList.add(neighborNode);
        }
        
        x--; 
    }
}

/**
 * Calculates the heuristic cost between two points using the Manhattan distance.
 * @param x1
 * @param y1
 * @param x2
 * @param y2
 * @return
 */
private double calculateHeuristic(int x1, int y1, int x2, int y2) {
    return Math.abs(x1 - x2) + Math.abs(y1 - y2);
}

/**
 * Follows the path from the target node to the start node.
 * @param targetNode
 */
private void followPath(Node targetNode) {
    Node currentNode = targetNode;

    while (currentNode.parent != null) {
        int directionX = currentNode.x - currentNode.parent.x;
        int directionY = currentNode.y - currentNode.parent.y;
        
        // Set actor's position
        this.setX(currentNode.x);
        this.setY(currentNode.y);

        // Adjust movement direction
        if (directionX == 1) this.turn(0); // right
        if (directionX == -1) this.turn(180); // left
        if (directionY == 1) this.turn(90); // down
        if (directionY == -1) this.turn(270); // up
        
        this.move(1); // move to the next cell
        
        currentNode = currentNode.parent;
    }
}

// Node class for A* pathfinding
/**
 * Represents a node in the A* pathfinding algorithm.
 * The node contains the x and y coordinates, the parent node, the cost to reach the node (gCost),
 * and the total cost of the node (fCost).
 * The fCost is the sum of the gCost and the heuristic cost (hCost).
 * The heuristic cost is the estimated cost to reach the target node from the current node.
 * The node also overrides the equals and hashCode methods to compare nodes based on their x and y coordinates.
 * The node class is used in the A* pathfinding algorithm to find the shortest path from the start node to the target node.
 * The algorithm explores the neighboring nodes and calculates the cost to reach each node.
 * The node with the lowest total cost (fCost) is selected for further exploration
 * until the target node is reached or the open list is empty.
 * The path is then reconstructed by following the parent nodes from the target node to the start node.
 * The actor moves along the path by setting its position to the x and y coordinates of each node.
 * The actor also adjusts its movement direction based on the difference between the current node and its parent node.
 * The actor moves one cell at a time to reach the target node.
 *
 * @param x The x-coordinate of the node.
 * @param y The y-coordinate of the node.
 * @param parent The parent node of the current node.
 * @param gCost The cost to reach the current node.
 * @param fCost The total cost of the current node.
 * @return The node object.
 *
 */
class Node {
    int x, y;
    Node parent;
    double gCost, fCost;

    Node(int x, int y) {
        this(x, y, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    Node(int x, int y, Node parent, double gCost, double fCost) {
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.gCost = gCost;
        this.fCost = fCost;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
}