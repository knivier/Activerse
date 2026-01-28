package ActiverseUtils;

import java.util.Random;

/**
 * PerlinNoise generates smooth gradient-based noise commonly used for
 * procedural textures, terrain, and other effects. This implementation supports
 * 1D, 2D, and 3D Perlin noise with support for fractal octave noise.
 * <p>
 * Based on Ken Perlin's original algorithm.
 *
 * @author Knivier
 * @version 1.4.1
 */
public class PerlinNoise {
    private final int[] permutation;
    private final int[] p;  // Doubled permutation array for fast access

    public PerlinNoise(long seed) {
        permutation = new int[256];
        p = new int[512];

        try {
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
        } catch (Exception e) {
            ErrorLogger.reportException("3B", "IN", "PerlinNoise(long seed)", e);
        }
    }

    public double noise(double x) {
        try {
            return noise(x, 0, 0);
        } catch (Exception e) {
            ErrorLogger.reportException("3B", "IN", "noise(double x)", e);
            return 0;
        }
    }

    public double noise(double x, double y) {
        try {
            return noise(x, y, 0);
        } catch (Exception e) {
            ErrorLogger.reportException("3B", "IN", "noise(double x, double y)", e);
            return 0;
        }
    }

    public double noise(double x, double y, double z) {
        try {
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

            return MathUtils.lerp(
                    MathUtils.lerp(
                            MathUtils.lerp(grad(p[AA], x, y, z), grad(p[BA], x - 1, y, z), u),
                            MathUtils.lerp(grad(p[AB], x, y - 1, z), grad(p[BB], x - 1, y - 1, z), u),
                            v),
                    MathUtils.lerp(
                            MathUtils.lerp(grad(p[AA + 1], x, y, z - 1), grad(p[BA + 1], x - 1, y, z - 1), u),
                            MathUtils.lerp(grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1), u),
                            v),
                    w);
        } catch (Exception e) {
            ErrorLogger.reportException("3B", "IN", "noise(double x, double y, double z)", e);
            return 0;
        }
    }

    public double octaveNoise(double x, int octaves, double persistence) {
        try {
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
        } catch (Exception e) {
            ErrorLogger.reportException("3B", "IN", "octaveNoise(double x, int octaves, double persistence)", e);
            return 0;
        }
    }

    public double octaveNoise(double x, double y, int octaves, double persistence) {
        try {
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
        } catch (Exception e) {
            ErrorLogger.reportException("3B", "IN", "octaveNoise(double x, double y, int octaves, double persistence)", e);
            return 0;
        }
    }

    private double fade(double t) {
        try {
            return t * t * t * (t * (t * 6 - 15) + 10);
        } catch (Exception e) {
            ErrorLogger.reportException("3B", "IN", "fade(double t)", e);
            return 0;
        }
    }

    private double grad(int hash, double x, double y, double z) {
        try {
            int h = hash & 15;
            double u = h < 8 ? x : y;
            double v = h < 4 ? y : (h == 12 || h == 14 ? x : z);
            return ((h & 1) == 0 ? u : -u) +
                    ((h & 2) == 0 ? v : -v);
        } catch (Exception e) {
            ErrorLogger.reportException("3B", "IN", "grad(int hash, double x, double y, double z)", e);
            return 0;
        }
    }
}
