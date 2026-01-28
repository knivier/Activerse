package ActiverseUtils;

/**
 * MathUtils - Common mathematical utility functions for game development
 * 
 * @author Knivier
 * @version 1.4.1
 */
public class MathUtils {
    
    /**
     * Linear interpolation between two values
     *
     * @param start Starting value
     * @param end Ending value
     * @param t Interpolation factor (0.0 to 1.0)
     * @return Interpolated value
     */
    public static double lerp(double start, double end, double t) {
        return start + (end - start) * t;
    }
    
    /**
     * Clamps a value between a minimum and maximum
     *
     * @param value The value to clamp
     * @param min Minimum value
     * @param max Maximum value
     * @return Clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
    
    /**
     * Clamps a float value between a minimum and maximum
     *
     * @param value The value to clamp
     * @param min Minimum value
     * @param max Maximum value
     * @return Clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
    
    /**
     * Clamps an integer value between a minimum and maximum
     *
     * @param value The value to clamp
     * @param min Minimum value
     * @param max Maximum value
     * @return Clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
    
    /**
     * Calculates distance between two points
     *
     * @param x1 First point X
     * @param y1 First point Y
     * @param x2 Second point X
     * @param y2 Second point Y
     * @return Distance between points
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Calculates angle between two points in radians
     *
     * @param x1 First point X
     * @param y1 First point Y
     * @param x2 Second point X
     * @param y2 Second point Y
     * @return Angle in radians
     */
    public static double angleBetween(double x1, double y1, double x2, double y2) {
        return Math.atan2(y2 - y1, x2 - x1);
    }
    
    /**
     * Normalizes an angle to be within 0 to 2π radians
     *
     * @param angle The angle in radians
     * @return Normalized angle
     */
    public static double normalizeAngle(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) angle += 2 * Math.PI;
        return angle;
    }
    
    /**
     * Converts degrees to radians
     *
     * @param degrees Angle in degrees
     * @return Angle in radians
     */
    public static double toRadians(double degrees) {
        return Math.toRadians(degrees);
    }
    
    /**
     * Converts radians to degrees
     *
     * @param radians Angle in radians
     * @return Angle in degrees
     */
    public static double toDegrees(double radians) {
        return Math.toDegrees(radians);
    }
    
    /**
     * Checks if a value is approximately equal to another (within epsilon)
     *
     * @param a First value
     * @param b Second value
     * @param epsilon Tolerance
     * @return true if values are approximately equal
     */
    public static boolean approximately(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }
    
    /**
     * Checks if a value is approximately equal to another (within 0.0001)
     *
     * @param a First value
     * @param b Second value
     * @return true if values are approximately equal
     */
    public static boolean approximately(double a, double b) {
        return approximately(a, b, 0.0001);
    }
    
    /**
     * Maps a value from one range to another
     *
     * @param value The value to map
     * @param inMin Input range minimum
     * @param inMax Input range maximum
     * @param outMin Output range minimum
     * @param outMax Output range maximum
     * @return Mapped value
     */
    public static double map(double value, double inMin, double inMax, double outMin, double outMax) {
        return outMin + (outMax - outMin) * ((value - inMin) / (inMax - inMin));
    }
}
