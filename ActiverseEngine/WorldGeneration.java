package ActiverseEngine;
import java.util.Random;

/**
 * WorldGeneration provides utilities for procedural world generation.
 * It supports seeded randomness, tile map management, and helper methods
 * for generating terrain, placing objects, and manipulating tiles.
 */
public class WorldGeneration {
    protected final int width;
    protected final int height;
    protected final Random random;
    protected final String[][] tileMap;

    /**
     * Constructs a WorldGeneration helper with a random seed.
     */
    public WorldGeneration(int width, int height) {
        this(width, height, System.currentTimeMillis());
    }

    /**
     * Constructs a WorldGeneration helper with a specific seed.
     */
    public WorldGeneration(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.random = new Random(seed);
        this.tileMap = new String[width][height];
    }

    /**
     * Returns the tile type at the given coordinates.
     */
    public String getTile(int x, int y) {
        if (inBounds(x, y)) {
            return tileMap[x][y];
        }
        return null;
    }

    /**
     * Sets the tile type at the given coordinates.
     */
    public void setTile(int x, int y, String type) {
        if (inBounds(x, y)) {
            tileMap[x][y] = type;
        }
    }

    /**
     * Checks if the given coordinates are within the world bounds.
     */
    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Fills a rectangular area with a specific tile type.
     */
    public void fillRect(int x, int y, int w, int h, String type) {
        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                setTile(i, j, type);
            }
        }
    }

    /**
     * Generates a simple surface using Gaussian noise.
     */
    public int[] generateSurface(int baseLevel, double variance) {
        int[] surface = new int[width];
        for (int x = 0; x < width; x++) {
            surface[x] = baseLevel + (int) (random.nextGaussian() * variance);
        }
        return surface;
    }

    /**
     * Fills the world below a surface with two layers (e.g., dirt and stone).
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
     * Places a tile at a random location within bounds.
     */
    public void placeRandomTile(String type) {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        setTile(x, y, type);
    }

    /**
     * Returns the underlying tile map.
     */
    public String[][] getTileMap() {
        return tileMap;
    }

    /**
     * Returns the random instance for custom generation.
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Finds the first empty tile below a given column.
     */
    public int findSurfaceY(int x) {
        for (int y = 0; y < height; y++) {
            if (getTile(x, y) != null) {
                return y - 1;
            }
        }
        return height - 1;
    }

    /**
     * Clears the world.
     */
    public void clear() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tileMap[x][y] = null;
            }
        }
    }
}
