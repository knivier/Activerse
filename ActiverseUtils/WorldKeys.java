package ActiverseUtils;

/**
 * Packed 64-bit keys for chunk coordinates and world cell origins (pixel-snapped).
 */
public final class WorldKeys {

    private WorldKeys() {}

    public static long packChunk(int cx, int cy) {
        return ((long) cx << 32) | (cy & 0xffffffffL);
    }

    public static int unpackChunkX(long key) {
        return (int) (key >> 32);
    }

    public static int unpackChunkY(long key) {
        return (int) key;
    }

    public static long packWorldCell(int worldPx, int worldPy) {
        return ((long) worldPx << 32) | (worldPy & 0xffffffffL);
    }

    public static int unpackWorldCellX(long key) {
        return (int) (key >> 32);
    }

    public static int unpackWorldCellY(long key) {
        return (int) key;
    }
}
