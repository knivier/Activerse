package ActiverseEngine;

import ActiverseUtils.DelayScheduler;
import ActiverseUtils.ErrorLogger;
import ActiverseUtils.ImageUtils;
import ActiverseUtils.MathUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

/**
 * The base class for all actors in the world.
 *
 * @author Knivier
 * @version 1.4.1
 */
public abstract class Actor {
    protected double direction;
    private int x, y;
    private double preciseX, preciseY; // Float positions for smooth movement
    private boolean usePrecisePosition = false;
    private World world;
    private ActiverseImage image;
    private double velocityX, velocityY;
    private int height;
    private int width;
    private List<Item> inventory; // List of items the actor can hold
    private int maxInventory = 10000; // Maximum number of items the actor can hold
    private boolean isStatic = false; // Whether the actor is static or not

    /**
     * Returns the state of the actor on whether it is static or not.
     * A static actor does not move and is not affected by gravity.
     * It will not be updated in the debug menus
     *
     * @return boolean indicating if the actor is static
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Sets the state of the actor to static or not.
     *
     * @param isStatic
     * @return boolean indicating if the state was successfully set
     */
    public boolean setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return true;
    }

    /**
     * When true, {@link World#update} skips {@link #act()} each tick. Use for actors with no
     * per-frame logic (saves huge cost when thousands of terrain tiles exist).
     */
    public boolean isTickInert() {
        return false;
    }

    /**
     * Performs the actor's action.
     */
    public abstract void act();

    /**
     * Enables inventory for the actor. By default, it is disabled.
     */
    public void enableInventory() {
        if (inventory == null) {
            inventory = new java.util.ArrayList<>();
        } else {
            ErrorLogger.report("5A", "OUT", "enableInventory()", "inventory is already enabled.", "2A", "OUT");
        }
    }

    /**
     * Disables the actor's inventory.
     * Clears the inventory list and sets it to null.
     */
    public void disableInventory() {
        if (inventory != null) {
            inventory.clear();
            inventory = null;
        } else {
            ErrorLogger.report("5A", "OUT", "disableInventory()", "inventory is already disabled.", "2A", "OUT");
        }
    }

    /**
     * Gets the maximum size of the actor's inventory.
     *
     * @return The maximum size of the inventory.
     */
    public int getInventorySize() {
        if (!ensureInventoryEnabled("getInventorySize()")) return 0;
        return inventory.size();
    }

    /**
     * Sets the inventory size
     *
     * @param size the maximum inventory size (maximum number of type Item)
     */
    public void setInventorySize(int size) {
        if (!ensureInventoryEnabled("setInventorySize(int size)")) return;
        if (size < 0) {
            ErrorLogger.report("5A", "OUT", "setInventorySize(int size)", "size cannot be negative.", "2A", "OUT");
            return;
        }
        maxInventory = size;
    }
    
    /**
     * Gets the maximum inventory size
     *
     * @return The maximum number of items the inventory can hold
     */
    public int getMaxInventorySize() {
        if (!ensureInventoryEnabled("getMaxInventorySize()")) return 0;
        return maxInventory;
    }

    /**
     * Checks if the actor has an inventory.
     *
     * @return true if the actor has an inventory, false otherwise.
     */
    public boolean hasInventory() {
        return inventory != null;
    }

    /**
     * Adds an item to the actor's inventory.
     *
     * @param item The item to add to the inventory.
     * @return Boolean the success of adding the item
     */
    public boolean addItem(Item item) {
        if (!ensureInventoryEnabled("addItem(Item item)")) return false;
        if (inventory.size() < maxInventory) { // Assuming a maximum of 10 items
            inventory.add(item);
            return true;
        } else {
            ErrorLogger.report("5A", "OUT", "addItem(Item item)", "inventory is full.", "2A", "OUT");
            return false;
        }
    }

    /**
     * Removes an item from the actor's inventory.
     *
     * @param item The item to remove from the inventory.
     */
    public void removeItem(Item item) {
        if (!ensureInventoryEnabled("removeItem(Item item)")) return;
        if (inventory.contains(item)) {
            inventory.remove(item);
        } else {
            ErrorLogger.report("5A", "OUT", "removeItem(Item item)", "item not found in inventory.", "2A", "OUT");
        }
    }

    /**
     * Gets the inventory of the actor.
     *
     * @return The list of items in the actor's inventory, or null if inventory is not enabled.
     */
    public List<Item> getInventory() {
        if (!ensureInventoryEnabled("getInventory()")) return null;
        return inventory;
    }

    /**
     * Enables precise float positioning for smooth sub-pixel movement
     */
    public void enablePrecisePosition() {
        usePrecisePosition = true;
        preciseX = x;
        preciseY = y;
    }
    
    /**
     * Sets the precise location using floats
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    public void setPreciseLocation(double x, double y) {
        if (usePrecisePosition) {
            this.preciseX = x;
            this.preciseY = y;
            this.x = (int)Math.round(x);
            this.y = (int)Math.round(y);
        } else {
            this.x = (int)Math.round(x);
            this.y = (int)Math.round(y);
        }
    }
    
    /**
     * Gets the precise X position
     *
     * @return The precise x-coordinate
     */
    public double getPreciseX() {
        return usePrecisePosition ? preciseX : x;
    }
    
    /**
     * Gets the precise Y position
     *
     * @return The precise y-coordinate
     */
    public double getPreciseY() {
        return usePrecisePosition ? preciseY : y;
    }

    /**
     * Sets the location of the actor.
     *
     * @param x The x-coordinate of the new location.
     * @param y The y-coordinate of the new location.
     */
    public void setLocation(int x, int y) {
        updatePrecisePosition(x, y);
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
        updatePrecisePosition(x, null);
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
        updatePrecisePosition(null, y);
        this.y = y;
    }

    /**
     * Returns the width of the Actor
     *
     * @return int Actor width in Activerse Unit (relative)
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the actor.
     *
     * @param width The width to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the image of the actor.
     *
     * @return The image of the actor, or null if no image is set.
     *         Returns null if the actor uses shapes instead of images.
     */
    public ActiverseImage getImage() {
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
     * If no image is set, this method does nothing. Subclasses should override
     * this method to draw shapes or custom graphics when not using images.
     *
     * @param g The graphics context to draw on.
     */
    public void paint(Graphics g) {
        if (image == null) return;
        try {
            Image awtImage = image.getImage();
            Graphics2D g2d = (Graphics2D) g;
            Dimension dims = ImageUtils.getImageDimensions(image, width, height);
            width = dims.width;
            height = dims.height;
            AffineTransform old = g2d.getTransform();

            g2d.rotate(direction, x + (double) dims.width / 2, y + (double) dims.height / 2);
            g2d.drawImage(awtImage, x, y, null);

            g2d.setTransform(old);
        } catch (Exception e) {
            ErrorLogger.reportException("5A", "OUT", "paint(Graphics g)", e, "2A", "OUT");
        }
        // If image is null, the actor should override paint() to draw shapes
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
    public Rectangle getBoundingBox() {
        Dimension dims = ImageUtils.getImageDimensions(getImage(), width, height);
        return new Rectangle(x, y, dims.width, dims.height);
    }

    /**
     * Moves the actor in its current direction by the specified distance.
     *
     * @param distance The distance to move the actor.
     */
    public void move(int distance) {
        if (world == null) {
            return; // Cannot move without a world
        }
        
        int dx = (int) (distance * Math.cos(direction));
        int dy = (int) (distance * Math.sin(direction));
        int newX = x + dx;
        int newY = y + dy;
        
        Dimension dims = ImageUtils.getImageDimensions(image, width, height);
        int actorWidth = dims.width;
        int actorHeight = dims.height;
        
        // Check if the new position exceeds the world limits
        if (newX >= 0 && newX + actorWidth <= world.getWidth() &&
                newY >= 0 && newY + actorHeight <= world.getHeight()) {
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
        // Normalize to [0, 2π) range
        if (direction < 0) {
            direction += 2 * Math.PI;
        }
    }

    /**
     * Finds the intesrsectiuon of the actor with another actor then returns it
     *
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
     * Delays the execution of the next {@link #act()} by the specified milliseconds.
     * Uses a shared daemon scheduler (not one thread per call).
     *
     * @param ms The delay in milliseconds.
     */
    public void delayNext(int ms) {
        if (ms < 0) {
            ErrorLogger.report("5A", "IN", "delayNext(int ms)", "delay cannot be negative.");
            return;
        }
        DelayScheduler.runAfterMillis(ms, () -> {
            if (world != null) {
                act();
            }
        });
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
     *
     * @param other The actor to follow
     * @param x     The number of iterations to follow the actor (seconds)
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
     *
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
     *
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

            // Adjust movement direction (convert degrees to radians)
            if (directionX == 1) this.direction = 0; // right (0 radians)
            else if (directionX == -1) this.direction = Math.PI; // left (π radians)
            else if (directionY == 1) this.direction = Math.PI / 2; // down (π/2 radians)
            else if (directionY == -1) this.direction = -Math.PI / 2; // up (-π/2 radians)

            this.move(1); // move to the next cell

            currentNode = currentNode.parent;
        }
    }
    
    /**
     * Gets the distance to another actor
     *
     * @param other The other actor
     * @return The distance in pixels
     */
    public double getDistanceTo(Actor other) {
        if (other == null) return Double.MAX_VALUE;
        return MathUtils.distance(getX(), getY(), other.getX(), other.getY());
    }
    
    /**
     * Gets the angle to another actor in radians
     *
     * @param other The other actor
     * @return The angle in radians from this actor to the other
     */
    public double getAngleTo(Actor other) {
        if (other == null) return 0;
        int dx = other.getX() - this.getX();
        int dy = other.getY() - this.getY();
        return Math.atan2(dy, dx);
    }
    
    /**
     * Applies knockback to this actor
     *
     * @param angle The angle of knockback in radians
     * @param force The force/distance of knockback
     */
    public void knockback(double angle, double force) {
        int dx = (int)(Math.cos(angle) * force);
        int dy = (int)(Math.sin(angle) * force);
        setX(getX() + dx);
        setY(getY() + dy);
    }

    private boolean ensureInventoryEnabled(String methodSignature) {
        if (inventory == null) {
            ErrorLogger.report("5A", "OUT", methodSignature, "inventory is not enabled.", "2A", "OUT");
            return false;
        }
        return true;
    }

    private void updatePrecisePosition(Integer newX, Integer newY) {
        if (!usePrecisePosition) return;
        if (newX != null) {
            preciseX = newX;
        }
        if (newY != null) {
            preciseY = newY;
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
     * @param x      The x-coordinate of the node.
     * @param y      The y-coordinate of the node.
     * @param parent The parent node of the current node.
     * @param gCost  The cost to reach the current node.
     * @param fCost  The total cost of the current node.
     * @return The node object.
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