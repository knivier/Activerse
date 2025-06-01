package ActiverseEngine;

import java.util.Random;

/**
 * PerlinNoise generates smooth gradient-based noise commonly used for
 * procedural textures, terrain, and other effects. This implementation supports
 * 1D, 2D, and 3D Perlin noise with support for fractal octave noise.
 * <p>
 * Based on Ken Perlin's original algorithm.
 *
 * @author Knivier
 * @version 1.4.0
 */
public class PerlinNoise {
    private final int[] permutation;
    private final int[] p;  // Doubled permutation array for fast access

    /**
     * Constructs a new PerlinNoise generator with a given seed.
     *
     * @param seed Seed for the internal random number generator.
     */
    public PerlinNoise(long seed) {
        permutation = new int[256];
        p = new int[512];

        Random rand = new Random(seed);
        for (int i = 0; i < 256; i++) {
            permutation[i] = i;
        }

        // Fisher–Yates shuffle
        for (int i = 255; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = permutation[i];
            permutation[i] = permutation[j];
            permutation[j] = tmp;
        }

        // Duplicate permutation to avoid overflow in hash calculations
        for (int i = 0; i < 512; i++) {
            p[i] = permutation[i % 256];
        }
    }

    // === Public Perlin noise methods ===

    /**
     * Computes 1D Perlin noise.
     *
     * @param x Input coordinate.
     * @return Noise value in the range [-1, 1].
     */
    public double noise(double x) {
        return noise(x, 0, 0);
    }

    /**
     * Computes 2D Perlin noise.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Noise value in the range [-1, 1].
     */
    public double noise(double x, double y) {
        return noise(x, y, 0);
    }

    /**
     * Computes 3D Perlin noise.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @return Noise value in the range [-1, 1].
     */
    public double noise(double x, double y, double z) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);

        double u = fade(x);
        double v = fade(y);
        double w = fade(z);

        int A = p[X] + Y;
        int AA = p[A] + Z;
        int AB = p[A + 1] + Z;
        int B = p[X + 1] + Y;
        int BA = p[B] + Z;
        int BB = p[B + 1] + Z;

        return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z),
                                grad(p[BA], x - 1, y, z)),
                        lerp(u, grad(p[AB], x, y - 1, z),
                                grad(p[BB], x - 1, y - 1, z))),
                lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1),
                                grad(p[BA + 1], x - 1, y, z - 1)),
                        lerp(u, grad(p[AB + 1], x, y - 1, z - 1),
                                grad(p[BB + 1], x - 1, y - 1, z - 1))));
    }

    // === Fractal noise with octaves ===

    /**
     * Computes 1D fractal Perlin noise using multiple octaves.
     *
     * @param x           Input coordinate.
     * @param octaves     Number of layers of detail.
     * @param persistence Controls amplitude scaling between octaves.
     * @return Fractal noise value in the range [-1, 1].
     */
    public double octaveNoise(double x, int octaves, double persistence) {
        double total = 0;
        double maxAmplitude = 0;
        double amplitude = 1;
        double frequency = 1;

        for (int i = 0; i < octaves; i++) {
            total += noise(x * frequency) * amplitude;
            maxAmplitude += amplitude;
            amplitude *= persistence;
            frequency *= 2;
        }

        return total / maxAmplitude;
    }

    /**
     * Computes 2D fractal Perlin noise using multiple octaves.
     *
     * @param x           X coordinate.
     * @param y           Y coordinate.
     * @param octaves     Number of layers of detail.
     * @param persistence Controls amplitude scaling between octaves.
     * @return Fractal noise value in the range [-1, 1].
     */
    public double octaveNoise(double x, double y, int octaves, double persistence) {
        double total = 0;
        double maxAmplitude = 0;
        double amplitude = 1;
        double frequency = 1;

        for (int i = 0; i < octaves; i++) {
            total += noise(x * frequency, y * frequency) * amplitude;
            maxAmplitude += amplitude;
            amplitude *= persistence;
            frequency *= 2;
        }

        return total / maxAmplitude;
    }

    // === Utility functions ===

    /**
     * Fade function to smooth interpolation.
     *
     * @param t Value in [0, 1].
     * @return Smoothed value.
     */
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    /**
     * Linear interpolation.
     *
     * @param t Interpolation factor.
     * @param a Start value.
     * @param b End value.
     * @return Interpolated result.
     */
    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    /**
     * Gradient function based on hashed value.
     *
     * @param hash Hash value.
     * @param x    X offset.
     * @param y    Y offset.
     * @param z    Z offset.
     * @return Dot product result of gradient vector.
     */
    private double grad(int hash, double x, double y, double z) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
        return ((h & 1) == 0 ? u : -u) +
                ((h & 2) == 0 ? v : -v);
    }
}
