package ActiverseEngine;

/**
 * The {@code Physics} class provides a comprehensive suite of physics-related
 * calculations, suitable for simulations, games, and educational tools.
 *
 * <p>Includes support for classical mechanics formulas such as force,
 * energy, motion, circular dynamics, and more.
 *
 * @author Knivier
 * @version 1.4.0
 */
public class Physics {

    /**
     * Standard gravitational acceleration on Earth (m/s^2)
     */
    public static final double EARTH_GRAVITY = 9.80665;

    /**
     * Calculates force using Newton's Second Law: F = m * a
     *
     * @param mass         Mass in kilograms
     * @param acceleration Acceleration in m/s²
     * @return Force in newtons (N)
     */
    public static double calculateForce(double mass, double acceleration) {
        return mass * acceleration;
    }

    /**
     * Calculates the weight of an object on Earth: W = m * g
     *
     * @param mass Mass in kilograms
     * @return Weight in newtons (N)
     */
    public static double calculateWeight(double mass) {
        return mass * EARTH_GRAVITY;
    }

    /**
     * Calculates final velocity after constant acceleration: v = u + at
     *
     * @param initialVelocity Initial velocity (m/s)
     * @param acceleration    Acceleration (m/s²)
     * @param time            Time in seconds
     * @return Final velocity (m/s)
     */
    public static double calculateVelocity(double initialVelocity, double acceleration, double time) {
        return initialVelocity + acceleration * time;
    }

    /**
     * Calculates displacement under constant acceleration: s = ut + 0.5at²
     *
     * @param initialVelocity Initial velocity (m/s)
     * @param acceleration    Acceleration (m/s²)
     * @param time            Time (s)
     * @return Displacement (m)
     */
    public static double calculateDisplacement(double initialVelocity, double acceleration, double time) {
        return initialVelocity * time + 0.5 * acceleration * time * time;
    }

    /**
     * Calculates speed from velocity components: speed = sqrt(dx² + dy²)
     *
     * @param dx Change in x (m)
     * @param dy Change in y (m)
     * @return Speed (m/s)
     */
    public static double calculateSpeed(double dx, double dy) {
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates momentum: p = m * v
     *
     * @param mass     Mass in kg
     * @param velocity Velocity in m/s
     * @return Momentum (kg·m/s)
     */
    public static double calculateMomentum(double mass, double velocity) {
        return mass * velocity;
    }

    /**
     * Calculates kinetic energy: KE = 0.5 * m * v²
     *
     * @param mass     Mass in kg
     * @param velocity Velocity in m/s
     * @return Kinetic energy (Joules)
     */
    public static double calculateKineticEnergy(double mass, double velocity) {
        return 0.5 * mass * velocity * velocity;
    }

    /**
     * Calculates gravitational potential energy: PE = m * g * h
     *
     * @param mass   Mass in kg
     * @param height Height in meters
     * @return Potential energy (Joules)
     */
    public static double calculatePotentialEnergy(double mass, double height) {
        return mass * EARTH_GRAVITY * height;
    }

    /**
     * Calculates elastic potential energy: E = 0.5 * k * x²
     *
     * @param springConstant Spring constant (N/m)
     * @param displacement   Displacement from rest position (m)
     * @return Elastic potential energy (Joules)
     */
    public static double calculateElasticPotentialEnergy(double springConstant, double displacement) {
        return 0.5 * springConstant * displacement * displacement;
    }

    /**
     * Calculates centripetal force: F = mv² / r
     *
     * @param mass     Mass in kg
     * @param velocity Tangential velocity (m/s)
     * @param radius   Radius of the circular path (m)
     * @return Centripetal force (N)
     */
    public static double calculateCentripetalForce(double mass, double velocity, double radius) {
        return mass * velocity * velocity / radius;
    }

    /**
     * Calculates angular momentum: L = I * ω
     *
     * @param momentOfInertia Moment of inertia (kg·m²)
     * @param angularVelocity Angular velocity (rad/s)
     * @return Angular momentum (kg·m²/s)
     */
    public static double calculateAngularMomentum(double momentOfInertia, double angularVelocity) {
        return momentOfInertia * angularVelocity;
    }

    /**
     * Calculates torque: T = r * F * sin(θ)
     *
     * @param radius       Lever arm length (m)
     * @param force        Applied force (N)
     * @param angleRadians Angle between force and lever (radians)
     * @return Torque (N·m)
     */
    public static double calculateTorque(double radius, double force, double angleRadians) {
        return radius * force * Math.sin(angleRadians);
    }

    /**
     * Calculates impulse: J = F * t
     *
     * @param force Force applied (N)
     * @param time  Duration of force (s)
     * @return Impulse (N·s)
     */
    public static double calculateImpulse(double force, double time) {
        return force * time;
    }

    /**
     * Calculates mechanical work: W = F * d * cos(θ)
     *
     * @param force        Force applied (N)
     * @param displacement Distance moved (m)
     * @param angleRadians Angle between force and motion direction (radians)
     * @return Work done (Joules)
     */
    public static double calculateWork(double force, double displacement, double angleRadians) {
        return force * displacement * Math.cos(angleRadians);
    }

    /**
     * Calculates power: P = W / t
     *
     * @param work Work done (J)
     * @param time Time taken (s)
     * @return Power (Watts)
     */
    public static double calculatePower(double work, double time) {
        return work / time;
    }

    /**
     * Calculates coefficient of restitution: e = (v2f - v1f) / (v1i - v2i)
     *
     * @param v1Initial Initial velocity of object 1 (m/s)
     * @param v2Initial Initial velocity of object 2 (m/s)
     * @param v1Final   Final velocity of object 1 (m/s)
     * @param v2Final   Final velocity of object 2 (m/s)
     * @return Coefficient of restitution (unitless)
     */
    public static double calculateCoefficientOfRestitution(double v1Initial, double v2Initial, double v1Final, double v2Final) {
        return (v2Final - v1Final) / (v1Initial - v2Initial);
    }

    /**
     * Calculates friction force: F = μ * N
     *
     * @param coefficientOfFriction Coefficient of friction (unitless)
     * @param normalForce           Normal force (N)
     * @return Frictional force (N)
     */
    public static double calculateFrictionForce(double coefficientOfFriction, double normalForce) {
        return coefficientOfFriction * normalForce;
    }

    /**
     * Calculates aerodynamic drag force: Fd = 0.5 * Cd * ρ * A * v²
     *
     * @param dragCoefficient Drag coefficient (unitless)
     * @param fluidDensity    Density of the fluid (kg/m³)
     * @param area            Cross-sectional area (m²)
     * @param velocity        Velocity of object (m/s)
     * @return Drag force (N)
     */
    public static double calculateDragForce(double dragCoefficient, double fluidDensity, double area, double velocity) {
        return 0.5 * dragCoefficient * fluidDensity * area * velocity * velocity;
    }

    /**
     * Calculates spring force using Hooke's Law: F = -k * x
     *
     * @param springConstant Spring constant (N/m)
     * @param displacement   Displacement from rest position (m)
     * @return Spring force (N)
     */
    public static double calculateSpringForce(double springConstant, double displacement) {
        return -springConstant * displacement;
    }

    /**
     * Calculates pressure: P = F / A
     *
     * @param force Force applied (N)
     * @param area  Surface area (m²)
     * @return Pressure (Pascals)
     */
    public static double calculatePressure(double force, double area) {
        return force / area;
    }

    /**
     * Calculates density: ρ = m / V
     *
     * @param mass   Mass (kg)
     * @param volume Volume (m³)
     * @return Density (kg/m³)
     */
    public static double calculateDensity(double mass, double volume) {
        return mass / volume;
    }

    /**
     * Calculates period of a simple pendulum: T = 2π√(l/g)
     *
     * @param length Length of pendulum (m)
     * @return Period (s)
     */
    public static double calculatePendulumPeriod(double length) {
        return 2 * Math.PI * Math.sqrt(length / EARTH_GRAVITY);
    }

    /**
     * Calculates escape velocity: v = √(2GM/r)
     *
     * @param gravitationalConstant Gravitational constant (N·m²/kg²)
     * @param mass                  Mass of planet/body (kg)
     * @param radius                Distance from center (m)
     * @return Escape velocity (m/s)
     */
    public static double calculateEscapeVelocity(double gravitationalConstant, double mass, double radius) {
        return Math.sqrt(2 * gravitationalConstant * mass / radius);
    }

    /**
     * Calculates average velocity: v = Δx / Δt
     *
     * @param displacement Change in position (m)
     * @param time         Time interval (s)
     * @return Average velocity (m/s)
     */
    public static double calculateAverageVelocity(double displacement, double time) {
        return displacement / time;
    }

    /**
     * Calculates gravitational force between two objects: F = G * m1 * m2 / r²
     *
     * @param gravitationalConstant G in N·m²/kg²
     * @param mass1                 Mass of object 1 (kg)
     * @param mass2                 Mass of object 2 (kg)
     * @param distance              Distance between centers (m)
     * @return Gravitational force (N)
     */
    public static double calculateGravitationalForce(double gravitationalConstant, double mass1, double mass2, double distance) {
        return gravitationalConstant * mass1 * mass2 / (distance * distance);
    }

    /**
     * Calculates rotational kinetic energy: KE = 0.5 * I * ω²
     *
     * @param momentOfInertia Moment of inertia (kg·m²)
     * @param angularVelocity Angular velocity (rad/s)
     * @return Rotational kinetic energy (Joules)
     */
    public static double calculateRotationalKineticEnergy(double momentOfInertia, double angularVelocity) {
        return 0.5 * momentOfInertia * angularVelocity * angularVelocity;
    }
}
