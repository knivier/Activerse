package ActiverseEngine;

public class Physics {

    // Gravitational constant (m/s^2)
    public static final double EARTH_GRAVITY = 9.80665;

    // Calculate force: F = m * a
    public static double calculateForce(double mass, double acceleration) {
        return mass * acceleration;
    }

    // Calculate weight on Earth: W = m * g
    public static double calculateWeight(double mass) {
        return mass * EARTH_GRAVITY;
    }

    // Calculate velocity: v = u + a * t
    public static double calculateVelocity(double initialVelocity, double acceleration, double time) {
        return initialVelocity + acceleration * time;
    }

    // Calculate displacement: s = ut + 0.5 * a * t^2
    public static double calculateDisplacement(double initialVelocity, double acceleration, double time) {
        return initialVelocity * time + 0.5 * acceleration * time * time;
    }

    // Calculate speed (magnitude of velocity)
    public static double calculateSpeed(double dx, double dy) {
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Calculate momentum: p = m * v
    public static double calculateMomentum(double mass, double velocity) {
        return mass * velocity;
    }

    // Calculate kinetic energy: KE = 0.5 * m * v^2
    public static double calculateKineticEnergy(double mass, double velocity) {
        return 0.5 * mass * velocity * velocity;
    }

    // Calculate potential energy: PE = m * g * h
    public static double calculatePotentialEnergy(double mass, double height) {
        return mass * EARTH_GRAVITY * height;
    }

    // Calculate elastic potential energy: E = 0.5 * k * x^2
    public static double calculateElasticPotentialEnergy(double springConstant, double displacement) {
        return 0.5 * springConstant * displacement * displacement;
    }

    // Calculate centripetal force: F = m * v^2 / r
    public static double calculateCentripetalForce(double mass, double velocity, double radius) {
        return mass * velocity * velocity / radius;
    }

    // Calculate angular momentum: L = I * w
    public static double calculateAngularMomentum(double momentOfInertia, double angularVelocity) {
        return momentOfInertia * angularVelocity;
    }

    // Calculate torque: T = r * F * sin(theta)
    public static double calculateTorque(double radius, double force, double angleRadians) {
        return radius * force * Math.sin(angleRadians);
    }

    // Calculate impulse: J = F * t
    public static double calculateImpulse(double force, double time) {
        return force * time;
    }

    // Calculate work: W = F * d * cos(theta)
    public static double calculateWork(double force, double displacement, double angleRadians) {
        return force * displacement * Math.cos(angleRadians);
    }

    // Calculate power: P = W / t
    public static double calculatePower(double work, double time) {
        return work / time;
    }

    // Calculate coefficient of restitution: e = (v2f - v1f) / (v1i - v2i)
    public static double calculateCoefficientOfRestitution(double v1Initial, double v2Initial, double v1Final, double v2Final) {
        return (v2Final - v1Final) / (v1Initial - v2Initial);
    }

    // Calculate friction force: F = mu * N
    public static double calculateFrictionForce(double coefficientOfFriction, double normalForce) {
        return coefficientOfFriction * normalForce;
    }

    // Calculate drag force (quadratic): Fd = 0.5 * Cd * rho * A * v^2
    public static double calculateDragForce(double dragCoefficient, double fluidDensity, double area, double velocity) {
        return 0.5 * dragCoefficient * fluidDensity * area * velocity * velocity;
    }

    // Calculate spring force (Hooke's Law): F = -k * x
    public static double calculateSpringForce(double springConstant, double displacement) {
        return -springConstant * displacement;
    }

    // Calculate pressure: P = F / A
    public static double calculatePressure(double force, double area) {
        return force / area;
    }

    // Calculate density: rho = m / V
    public static double calculateDensity(double mass, double volume) {
        return mass / volume;
    }

    // Calculate period of a simple pendulum: T = 2 * pi * sqrt(l / g)
    public static double calculatePendulumPeriod(double length) {
        return 2 * Math.PI * Math.sqrt(length / EARTH_GRAVITY);
    }

    // Calculate escape velocity: v = sqrt(2 * G * M / r)
    public static double calculateEscapeVelocity(double gravitationalConstant, double mass, double radius) {
        return Math.sqrt(2 * gravitationalConstant * mass / radius);
    }    
}
