package ActiverseUtils;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * WorldGeneration provides advanced utilities for procedural world generation.
 * It supports biomes, cave generation, tile metadata, Perlin noise terrain,
 * structure placement, and world serialization.
 *
 * @author Knivier
 * @version 1.4.0
 */
public class WorldGeneration {
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
     * ACESH error reporting utility.
     * @param fileCode The file code (e.g., "1B" for ActorVector.java)
     * @param errorType The error type (e.g., "LN", "IN", "OUT", etc.)
     * @param method The method where the error occurred
     * @param e The exception
     */
    private static void reportACESH(String fileCode, String errorType, String method, Exception e) {
        System.out.println(fileCode + "." + errorType + "." + method + " :(LN: " + method + "() - ACESH Error thrown; " + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
    }

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
     * @param x Horizontal coordinate
     * @param y Vertical coordinate
     * @param type Tile type as a string
     */
    public void setTile(int x, int y, String type) {
        setTile(x, y, type, new HashMap<>());
    }

    /**
     * Sets the tile at (x, y) with a specified type and metadata map.
     * @param x Horizontal coordinate
     * @param y Vertical coordinate
     * @param type Tile type as a string
     * @param metadata Additional metadata for the tile
     */
    public void setTile(int x, int y, String type, Map<String, Object> metadata) {
        if (inBounds(x, y)) {
            tileMap[x][y] = new Tile(type, metadata);
        }
    }

    /**
     * Returns true if (x, y) is within world boundaries.
     * @param x Horizontal coordinate
     * @param y Vertical coordinate
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
        } catch (IOException e) {
            reportACESH("6B", "OUT", "saveToFile", e);
            throw e;
        }
    }

    /**
     * Loads a previously saved tileMap from disk and replaces the current one.
     * @throws IOException Errors from file reading
     */
    public void loadFromFile(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            Tile[][] loaded = (Tile[][]) ois.readObject();
            for (int x = 0; x < width; x++)
                if (height >= 0) System.arraycopy(loaded[x], 0, tileMap[x], 0, height);
        } catch (IOException | ClassNotFoundException e) {
            reportACESH("6B", "IN", "loadFromFile", e);
            throw e;
        }
    }

    /**
     * Tile is a serializable record that stores the type of tile and any custom metadata.
     *
     */
    public record Tile(String type, Map<String, Object> metadata) implements Serializable {
    }
}
