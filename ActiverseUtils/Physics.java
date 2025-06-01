package ActiverseUtils;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    /**
     * WorldGeneration provides advanced utilities for procedural world generation.
     * It supports biomes, cave generation, tile metadata, Perlin noise terrain,
     * structure placement, and world serialization.
     *
     * @author Knivier
     * @version 1.4.0
     */
    public static class WorldGeneration {
        // Dimensions of the world
        protected final int width;
        protected final int height;

        // Random number generator for procedural content
        protected final Random random;

        // 2D array representing the world’s tiles
        protected final Tile[][] tileMap;

        // Instance of Perlin noise generator for terrain
        protected final PerlinNoise perlin;

        /**
         * Constructs a world with the given dimensions using current time as seed.
         */
        public WorldGeneration(int width, int height) {
            this(width, height, System.currentTimeMillis());
        }

        /**
         * Constructs a world with given dimensions and a specified random seed.
         */
        public WorldGeneration(int width, int height, long seed) {
            this.width = width;
            this.height = height;
            this.random = new Random(seed);
            this.perlin = new PerlinNoise(seed);  // Deterministic noise based on seed
            this.tileMap = new Tile[width][height];
        }

        /**
         * Returns the Tile object at the specified (x, y), or null if out of bounds.
         *
         * @return Tile at (x, y) or null if out of bounds.
         */
        public Tile getTile(int x, int y) {
            return inBounds(x, y) ? tileMap[x][y] : null;
        }

        /**
         * Sets the tile at (x, y) with a type and no metadata.
         */
        public void setTile(int x, int y, String type) {
            setTile(x, y, type, new HashMap<>());
        }

        /**
         * Sets the tile at (x, y) with a specified type and metadata map.
         */
        public void setTile(int x, int y, String type, Map<String, Object> metadata) {
            if (inBounds(x, y)) {
                tileMap[x][y] = new Tile(type, metadata);
            }
        }

        /**
         * Returns true if (x, y) is within world boundaries.
         *
         * @return boolean indicating if coordinates are valid.
         */
        public boolean inBounds(int x, int y) {
            return x >= 0 && x < width && y >= 0 && y < height;
        }

        /**
         * Returns the internal tile map.
         */
        public Tile[][] getTileMap() {
            return tileMap;
        }

        /**
         * Returns the internal random instance (for procedural hooks).
         *
         * @return Random instance used for procedural generation.
         */
        public Random getRandom() {
            return random;
        }

        /**
         * Clears the entire tile map by setting all tiles to null.
         */
        public void clear() {
            for (int x = 0; x < width; x++)
                Arrays.fill(tileMap[x], null);
        }

        /**
         * Generates a 1D heightmap using Perlin noise with default octaves and persistence.
         *
         * @param baseLevel The vertical base to center terrain around.
         * @param frequency Controls horizontal stretching (lower = wider features).
         * @param amplitude Controls vertical height variation.
         * @return An array of Y-values representing the surface.
         */
        public int[] generatePerlinSurface(int baseLevel, double frequency, double amplitude) {
            return generatePerlinSurface(baseLevel, frequency, amplitude, 4, 0.5);
        }

        /**
         * Generates a customizable Perlin-based heightmap with multiple octaves.
         *
         * @param baseLevel   The vertical base to center terrain around.
         * @param frequency   Controls horizontal stretching (lower = wider features).
         * @param amplitude   Controls vertical height variation.
         * @param octaves     Number of octaves for detail (higher = more detail).
         * @param persistence Controls amplitude scaling between octaves (0.0-1.0).
         * @return An array of Y-values representing the surface.
         */
        public int[] generatePerlinSurface(int baseLevel, double frequency, double amplitude, int octaves, double persistence) {
            int[] surface = new int[width];
            for (int x = 0; x < width; x++) {
                double nx = x * frequency;
                double noise = perlin.octaveNoise(nx, octaves, persistence);
                surface[x] = baseLevel + (int) (noise * amplitude);
            }
            return surface;
        }

        /**
         * Fills terrain below the surface based on a heightmap using layered materials.
         *
         * @param surface     The heightmap array.
         * @param stoneLevel  Y-value below which stone is used.
         * @param surfaceType Tile type for surface layer.
         * @param dirtType    Tile type for soil layer.
         * @param stoneType   Tile type for deep underground.
         */
        public void fillBelowSurface(int[] surface, int stoneLevel, String surfaceType, String dirtType, String stoneType) {
            for (int x = 0; x < width; x++) {
                int s = surface[x];
                for (int y = 0; y < height; y++) {
                    if (y > s) {
                        setTile(x, y, y > stoneLevel ? stoneType : dirtType);
                    } else if (y == s) {
                        setTile(x, y, surfaceType);
                    }
                }
            }
        }

        /**
         * Assigns biomes to each tile based on horizontal position and biome width.
         * Each tile receives a "biome" key in its metadata.
         */
        public void generateBiomes(int biomeWidth, String[] biomes) {
            for (int x = 0; x < width; x++) {
                String biome = biomes[(x / biomeWidth) % biomes.length];
                for (int y = 0; y < height; y++) {
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("biome", biome);
                    setTile(x, y, null, meta);
                }
            }
        }

        /**
         * Places a rectangular structure into the world, starting at (x, y).
         * Each non-null string in the 2D array represents a tile type.
         */
        public void placeStructure(int x, int y, String[][] structure) {
            for (int dx = 0; dx < structure.length; dx++) {
                for (int dy = 0; dy < structure[0].length; dy++) {
                    String type = structure[dx][dy];
                    if (type != null) {
                        setTile(x + dx, y + dy, type);
                    }
                }
            }
        }

        /**
         * Generates cave-like structures using a randomized cellular automata.
         *
         * @param fillProbability Initial chance for a cell to be wall.
         * @param steps           Number of smoothing iterations (higher = smoother caves).
         */
        public void generateCaves(double fillProbability, int steps) {
            boolean[][] caveMap = new boolean[width][height];

            // Step 1: Randomly fill cave map
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    caveMap[x][y] = random.nextDouble() < fillProbability;

            // Step 2: Apply smoothing rules
            for (int i = 0; i < steps; i++) {
                boolean[][] newMap = new boolean[width][height];
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int walls = countNeighbors(caveMap, x, y);
                        newMap[x][y] = walls > 4;
                    }
                }
                caveMap = newMap;
            }

            // Step 3: Null out empty spaces
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                    if (!caveMap[x][y])
                        setTile(x, y, null);
        }

        /**
         * Helper for counting wall neighbors around a given cell.
         *
         * @return Number of neighboring cells that are walls.
         */
        private int countNeighbors(boolean[][] map, int x, int y) {
            int count = 0;
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    if (!(dx == 0 && dy == 0) && inBounds(x + dx, y + dy) && map[x + dx][y + dy])
                        count++;
            return count;
        }

        /**
         * Serializes the tileMap to a file on disk.
         */
        public void saveToFile(String path) throws IOException {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
                oos.writeObject(tileMap);
            }
        }

        /**
         * Loads a previously saved tileMap from disk and replaces the current one.
         */
        @SuppressWarnings("unchecked")
        public void loadFromFile(String path) throws IOException, ClassNotFoundException {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
                Tile[][] loaded = (Tile[][]) ois.readObject();
                for (int x = 0; x < width; x++)
                    if (height >= 0) System.arraycopy(loaded[x], 0, tileMap[x], 0, height);
            }
        }

        /**
         * Tile is a serializable record that stores the type of tile and any custom metadata.
         * */
        public record Tile(String type, Map<String, Object> metadata) implements Serializable {
        }
    }
}
