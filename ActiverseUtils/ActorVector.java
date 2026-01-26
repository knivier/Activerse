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

    public ActorVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public ActorVector(Actor actor) {
        this.x = actor.getX();
        this.y = actor.getY();
    }

    public ActorVector(Actor from, Actor to) {
        this.x = to.getX() - from.getX();
        this.y = to.getY() - from.getY();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double magnitude() {
        try {
            return MathUtils.distance(0, 0, x, y);
        } catch (Exception e) {
            ErrorLogger.reportException("1B", "LN", "magnitude()", e);
            return 0;
        }
    }

    public ActorVector normalize() {
        try {
            double mag = magnitude();
            if (mag == 0) return new ActorVector(0, 0);
            return new ActorVector(x / mag, y / mag);
        } catch (Exception e) {
            ErrorLogger.reportException("1B", "LN", "normalize()", e);
            return new ActorVector(0, 0);
        }
    }

    public ActorVector scale(double scalar) {
        try {
            return new ActorVector(x * scalar, y * scalar);
        } catch (Exception e) {
            ErrorLogger.reportException("1B", "LN", "scale(double scalar)", e);
            return new ActorVector(0, 0);
        }
    }

    public ActorVector add(ActorVector other) {
        try {
            return new ActorVector(this.x + other.x, this.y + other.y);
        } catch (Exception e) {
            ErrorLogger.reportException("1B", "LN", "add(ActorVector other)", e);
            return new ActorVector(0, 0);
        }
    }

    public ActorVector subtract(ActorVector other) {
        try {
            return new ActorVector(this.x - other.x, this.y - other.y);
        } catch (Exception e) {
            ErrorLogger.reportException("1B", "LN", "subtract(ActorVector other)", e);
            return new ActorVector(0, 0);
        }
    }

    public double dot(ActorVector other) {
        try {
            return this.x * other.x + this.y * other.y;
        } catch (Exception e) {
            ErrorLogger.reportException("1B", "LN", "dot(ActorVector other)", e);
            return 0;
        }
    }

    public double angleBetween(ActorVector other) {
        try {
            double dot = this.dot(other);
            double magProduct = this.magnitude() * other.magnitude();
            if (magProduct == 0) return 0;
            return Math.toDegrees(Math.acos(dot / magProduct));
        } catch (Exception e) {
            ErrorLogger.reportException("1B", "LN", "angleBetween(ActorVector other)", e);
            return 0;
        }
    }

    public ActorVector toAcceleration(double mass) {
        try {
            if (mass == 0) throw new ArithmeticException("Division by zero (mass=0)");
            return new ActorVector(x / mass, y / mass);
        } catch (Exception e) {
            ErrorLogger.reportException("1B", "IN", "toAcceleration(double mass)", e);
            return new ActorVector(0, 0);
        }
    }

    public double toKineticEnergy(double mass) {
        try {
            double speed = MathUtils.distance(0, 0, x, y);
            return Physics.calculateKineticEnergy(mass, speed);
        } catch (Exception e) {
            ErrorLogger.reportException("1B", "LN", "toKineticEnergy(double mass)", e);
            return 0;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ActorVector)) return false;
        ActorVector other = (ActorVector) obj;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) * 31 + Double.hashCode(y);
    }

}
