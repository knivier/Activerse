package ActiverseUtils;

import ActiverseEngine.Actor;

/**
 * The ActorVector class represents a 2D vector in the Activerse game engine.
 * It can represent position, velocity, acceleration, or directional displacement
 * between two Actor objects.
 *
 * @author Knivier
 * @version 1.4.0
 */
public class ActorVector {
    // X and Y components of the vector
    private final double x;
    private final double y;

    /**
     * Constructs a vector from given x and y components.
     * @param x the x-component
     * @param y the y-component
     */
    public ActorVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a vector representing the position of a single actor.
     * Useful as a base vector or for physics calculations.
     * @param actor the Actor whose position to use
     */
    public ActorVector(Actor actor) {
        this.x = actor.getX();
        this.y = actor.getY();
    }

    /**
     * Constructs a vector from one actor to another.
     * Useful for computing direction, distance, and relative motion.
     * @param from the origin Actor
     * @param to the destination Actor
     */
    public ActorVector(Actor from, Actor to) {
        this.x = to.getX() - from.getX();
        this.y = to.getY() - from.getY();
    }

    /**
     * @return the x-component of the vector
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y-component of the vector
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the magnitude (length) of the vector using the Euclidean formula.
     * @return the vector's magnitude
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Returns a new vector representing the direction (unit vector) of this vector.
     * @return the normalized vector (same direction, length of 1)
     */
    public ActorVector normalize() {
        double mag = magnitude();
        if (mag == 0) return new ActorVector(0, 0); // Avoid division by zero
        return new ActorVector(x / mag, y / mag);
    }

    /**
     * Scales the vector by a scalar multiplier.
     * @param scalar the scalar value to multiply with
     * @return a new scaled ActorVector
     */
    public ActorVector scale(double scalar) {
        return new ActorVector(x * scalar, y * scalar);
    }

    /**
     * Adds another vector to this one.
     * @param other the vector to add
     * @return the resulting vector
     */
    public ActorVector add(ActorVector other) {
        return new ActorVector(this.x + other.x, this.y + other.y);
    }

    /**
     * Subtracts another vector from this one.
     * @param other the vector to subtract
     * @return the resulting vector
     */
    public ActorVector subtract(ActorVector other) {
        return new ActorVector(this.x - other.x, this.y - other.y);
    }

    /**
     * Computes the dot product between this vector and another.
     * Useful for projecting vectors or determining angles between them.
     * @param other the other ActorVector
     * @return the dot product (a scalar)
     */
    public double dot(ActorVector other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * Calculates the angle in degrees between this vector and another.
     * @param other the other vector
     * @return angle in degrees (0–180)
     */
    public double angleBetween(ActorVector other) {
        double dot = this.dot(other);
        double magProduct = this.magnitude() * other.magnitude();
        if (magProduct == 0) return 0;
        return Math.toDegrees(Math.acos(dot / magProduct));
    }

    /**
     * Simulates a force applied on a mass and returns the resulting acceleration vector.
     * a = F / m
     * @param mass the mass of the object (in kg)
     * @return acceleration vector (m/s²)
     */
    public ActorVector toAcceleration(double mass) {
        if (mass == 0) return new ActorVector(0, 0); // Avoid division by zero
        return new ActorVector(x / mass, y / mass);
    }

    /**
     * Computes kinetic energy: KE = 0.5 * m * v², where v = this vector's magnitude.
     * @param mass the mass of the object (in kg)
     * @return the kinetic energy in joules
     */
    public double toKineticEnergy(double mass) {
        double speedSquared = x * x + y * y;
        return 0.5 * mass * speedSquared;
    }

    /**
     * Returns a string representation of the vector in coordinate format.
     * @return the string (x, y)
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Checks if two vectors are equal based on x and y components.
     * @param obj the object to compare
     * @return true if the vectors are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ActorVector)) return false;
        ActorVector other = (ActorVector) obj;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
    }

    /**
     * Returns the hash code for this vector.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Double.hashCode(x) * 31 + Double.hashCode(y);
    }
}
