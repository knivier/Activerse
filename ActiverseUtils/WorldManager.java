package ActiverseUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * WorldManager - Handles saving and loading world data
 * Manages world.json and player.json files in Worlds/[WorldName]/ directories.
 * <p>
 * The Worlds directory is now resolved relative to either the current working
 * directory or its parent. This allows the typical layout:
 * <pre>
 *   project-root/
 *     Worlds/
 *     src/
 * </pre>
 * While still supporting cases where the game is launched with the working
 * directory set to either the project root or the src directory.
 * This is a significantly harder class to implement than the others, so don't mess with it unless you know what you're doing
 * 
 * @author Knivier
 * @version 1.4.1
 */
public class WorldManager {
    private static final String WORLDS_DIR_NAME = "Worlds";
    
    /**
     * Resolves the root directory where all worlds are stored.
     * <p>
     * Resolution order:
     * - ./Worlds      (when running from project root)
     * - ../Worlds     (when running from src or a subdirectory)
     */
    private static File getWorldsRootDirectory() {
        // First try Worlds/ in the current working directory
        File direct = new File(WORLDS_DIR_NAME);
        if (direct.exists() && direct.isDirectory()) {
            return direct;
        }
        
        // Then try ../Worlds relative to current working directory
        File parent = new File(".." + File.separator + WORLDS_DIR_NAME);
        if (parent.exists() && parent.isDirectory()) {
            return parent;
        }
        
        // Fallback: prefer creating in ../Worlds if possible (so layout matches project-root/Worlds + src/)
        return parent;
    }
    
    /**
     * Creates the Worlds directory if it doesn't exist
     */
    public static void ensureWorldsDirectory() {
        File worldsDir = getWorldsRootDirectory();
        if (!worldsDir.exists()) {
            worldsDir.mkdirs();
        }
    }
    
    /**
     * Gets list of all world names (directories in Worlds/)
     */
    public static List<String> getWorldList() {
        ensureWorldsDirectory();
        List<String> worlds = new ArrayList<>();
        File worldsDir = getWorldsRootDirectory();
        if (worldsDir.exists() && worldsDir.isDirectory()) {
            File[] dirs = worldsDir.listFiles(File::isDirectory);
            if (dirs != null) {
                for (File dir : dirs) {
                    worlds.add(dir.getName());
                }
            }
        }
        return worlds;
    }
    
    /**
     * Gets the path to a world directory
     */
    public static String getWorldPath(String worldName) {
        File worldsDir = getWorldsRootDirectory();
        return new File(worldsDir, worldName).getPath();
    }

    /**
     * Builds a file path under a world directory.
     */
    private static String getWorldFilePath(String worldName, String filename) {
        return new File(getWorldPath(worldName), filename).getPath();
    }

    /**
     * Ensures both the global worlds root and the specific world directory exist.
     *
     * @return Directory object for the world
     */
    private static File ensureWorldDirectory(String worldName) {
        ensureWorldsDirectory();
        File worldDir = new File(getWorldPath(worldName));
        if (!worldDir.exists()) {
            worldDir.mkdirs();
        }
        return worldDir;
    }
    
    /**
     * Gets the path to world.json
     */
    public static String getWorldJsonPath(String worldName) {
        return getWorldFilePath(worldName, "world.json");
    }
    
    /**
     * Gets the path to player.json
     */
    public static String getPlayerJsonPath(String worldName) {
        return getWorldFilePath(worldName, "player.json");
    }
    
    /**
     * Saves world data to world.json
     */
    public static void saveWorldData(String worldName, long seed, int tileSize, int worldWidth, int worldHeight) {
        saveWorldData(worldName, seed, tileSize, worldWidth, worldHeight, 0L);
    }

    /**
     * Saves world data to world.json, including total game ticks elapsed.
     */
    public static void saveWorldData(String worldName, long seed, int tileSize, int worldWidth, int worldHeight, long totalGameTicks) {
        saveWorldData(worldName, seed, tileSize, worldWidth, worldHeight, totalGameTicks, false, 32,
                0L, false, 0L);
    }

    /**
     * Saves world.json including infinite-world flags and chunk size (tiles per chunk edge).
     */
    public static void saveWorldData(String worldName, long seed, int tileSize, int worldWidth, int worldHeight,
                                     long totalGameTicks, boolean infinite, int chunkTiles) {
        saveWorldData(worldName, seed, tileSize, worldWidth, worldHeight, totalGameTicks, infinite, chunkTiles,
                0L, false, 0L);
    }

    /**
     * Saves world.json including time-offset state used by console time controls.
     */
    public static void saveWorldData(String worldName, long seed, int tileSize, int worldWidth, int worldHeight,
                                     long totalGameTicks, boolean infinite, int chunkTiles,
                                     long dayNightDisplayOffset, boolean timeFrozen, long frozenDayNightTicks) {
        ensureWorldDirectory(worldName);

        try {
            String json = String.format(
                "{\n" +
                "  \"seed\": %d,\n" +
                "  \"tileSize\": %d,\n" +
                "  \"worldWidth\": %d,\n" +
                "  \"worldHeight\": %d,\n" +
                "  \"totalGameTicks\": %d,\n" +
                "  \"infinite\": %b,\n" +
                "  \"chunkTiles\": %d,\n" +
                "  \"dayNightDisplayOffset\": %d,\n" +
                "  \"timeFrozen\": %b,\n" +
                "  \"frozenDayNightTicks\": %d\n" +
                "}",
                seed, tileSize, worldWidth, worldHeight, totalGameTicks, infinite, chunkTiles,
                dayNightDisplayOffset, timeFrozen, frozenDayNightTicks
            );
            
            Files.write(Paths.get(getWorldJsonPath(worldName)), json.getBytes());
        } catch (IOException e) {
            ErrorLogger.reportException("WM", "IO", "saveWorldData", e);
        }
    }
    
    /**
     * Loads world data from world.json
     */
    public static WorldData loadWorldData(String worldName) {
        try {
            String path = getWorldJsonPath(worldName);
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return parseWorldJson(content);
        } catch (IOException e) {
            ErrorLogger.reportException("WM", "IO", "loadWorldData", e);
            return null;
        }
    }
    
    /**
     * Saves player data to player.json
     */
    public static void savePlayerData(String worldName, PlayerData playerData) {
        ensureWorldDirectory(worldName);
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"x\": ").append(playerData.x).append(",\n");
            json.append("  \"y\": ").append(playerData.y).append(",\n");
            json.append("  \"health\": ").append(playerData.health).append(",\n");
            json.append("  \"stamina\": ").append(playerData.stamina).append(",\n");
            json.append("  \"hunger\": ").append(playerData.hunger).append(",\n");
            json.append("  \"workbenchOwned\": ").append(playerData.workbenchOwned).append(",\n");
            json.append("  \"inventory\": [\n");
            
            for (int i = 0; i < playerData.inventory.size(); i++) {
                ItemData item = playerData.inventory.get(i);
                json.append("    {\n");
                json.append("      \"name\": \"");
                JsonUtils.appendEscaped(json, item.name);
                json.append("\",\n");
                json.append("      \"type\": \"");
                JsonUtils.appendEscaped(json, item.type);
                json.append("\",\n");
                json.append("      \"value\": ").append(item.value).append("\n");
                json.append("    }");
                if (i < playerData.inventory.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("  ]\n");
            json.append("}\n");
            
            Files.write(Paths.get(getPlayerJsonPath(worldName)), json.toString().getBytes());
        } catch (IOException e) {
            ErrorLogger.reportException("WM", "IO", "savePlayerData", e);
        }
    }
    
    /**
     * Loads player data from player.json
     */
    public static PlayerData loadPlayerData(String worldName) {
        try {
            String path = getPlayerJsonPath(worldName);
            File file = new File(path);
            if (!file.exists()) {
                return null;
            }
            
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return parsePlayerJson(content);
        } catch (IOException e) {
            ErrorLogger.reportException("WM", "IO", "loadPlayerData", e);
            return null;
        }
    }
    
    /**
     * Saves modified blocks (terrain tiles that were placed/destroyed)
     */
    public static void saveModifiedBlocks(String worldName, Map<String, BlockData> modifiedBlocks) {
        try {
            String path = getWorldFilePath(worldName, "blocks.json");
            int est = Math.max(128, modifiedBlocks.size() * 80 + 48);
            StringBuilder json = new StringBuilder(est);
            json.append("{\n");
            json.append("  \"blocks\": [\n");
            
            int index = 0;
            for (BlockData block : modifiedBlocks.values()) {
                json.append("    {\n");
                json.append("      \"x\": ").append(block.x).append(",\n");
                json.append("      \"y\": ").append(block.y).append(",\n");
                json.append("      \"type\": \"");
                JsonUtils.appendEscaped(json, block.type);
                json.append("\",\n");
                json.append("      \"solid\": ").append(block.solid).append("\n");
                json.append("    }");
                if (index < modifiedBlocks.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
                index++;
            }
            
            json.append("  ]\n");
            json.append("}\n");
            
            Files.write(Paths.get(path), json.toString().getBytes());
        } catch (IOException e) {
            ErrorLogger.reportException("WM", "IO", "saveModifiedBlocks", e);
        }
    }
    
    /**
     * Loads modified blocks
     */
    public static Map<String, BlockData> loadModifiedBlocks(String worldName) {
        try {
            String path = getWorldFilePath(worldName, "blocks.json");
            File file = new File(path);
            if (!file.exists()) {
                return new HashMap<>();
            }
            
            String content = new String(Files.readAllBytes(Paths.get(path)));
            return parseBlocksJson(content);
        } catch (IOException e) {
            ErrorLogger.reportException("WM", "IO", "loadModifiedBlocks", e);
            return new HashMap<>();
        }
    }
    
    private static WorldData parseWorldJson(String json) {
        WorldData data = new WorldData();
        try {
            data.seed           = extractLong(json, "seed");
            data.tileSize       = extractInt(json, "tileSize");
            data.worldWidth     = extractInt(json, "worldWidth");
            data.worldHeight    = extractInt(json, "worldHeight");
            data.totalGameTicks = extractLong(json, "totalGameTicks");
            data.infinite       = extractBooleanOptional(json, "infinite", false);
            data.chunkTiles     = extractIntOptional(json, "chunkTiles", 32);
            data.dayNightDisplayOffset = extractLongOptional(json, "dayNightDisplayOffset", 0L);
            data.timeFrozen = extractBooleanOptional(json, "timeFrozen", false);
            data.frozenDayNightTicks = extractLongOptional(json, "frozenDayNightTicks", 0L);
            if (data.chunkTiles <= 0) data.chunkTiles = 32;
            return data;
        } catch (Exception e) {
            ErrorLogger.reportException("WM", "PARSE", "parseWorldJson", e);
            return null;
        }
    }
    
    /**
     * Index of the {@code ]} that closes the array starting at {@code arrayStart} ({@code [}),
     * respecting strings and escapes. Returns {@code -1} if not found.
     */
    private static int indexOfClosingBracketForArray(String json, int arrayStart) {
        if (arrayStart < 0 || arrayStart >= json.length() || json.charAt(arrayStart) != '[') {
            return -1;
        }
        int depth = 0;
        boolean inString = false;
        boolean escape = false;
        for (int i = arrayStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                escape = false;
                continue;
            }
            if (inString) {
                if (c == '\\') {
                    escape = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
                continue;
            }
            if (c == '[') {
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Drops adjacent duplicate inventory rows (same name, type, value). Repairs saves that were
     * written with each slot duplicated once; does not affect normal stacks (resources use one row).
     */
    private static void normalizeAdjacentDuplicateInventoryEntries(List<ItemData> inv) {
        if (inv == null || inv.size() < 2) {
            return;
        }
        for (int i = 0; i + 1 < inv.size(); ) {
            ItemData a = inv.get(i);
            ItemData b = inv.get(i + 1);
            if (Objects.equals(a.name, b.name)
                    && Objects.equals(a.type, b.type)
                    && a.value == b.value) {
                inv.remove(i + 1);
            } else {
                i++;
            }
        }
    }

    private static PlayerData parsePlayerJson(String json) {
        PlayerData data = new PlayerData();
        try {
            data.x = extractInt(json, "x");
            data.y = extractInt(json, "y");
            data.health = extractFloat(json, "health");
            data.stamina = extractFloat(json, "stamina");
            data.hunger = extractFloat(json, "hunger");
            data.workbenchOwned = extractBooleanOptional(json, "workbenchOwned", false);
            
            // Parse inventory array
            data.inventory = new ArrayList<>();
            int inventoryStart = json.indexOf("\"inventory\": [");
            if (inventoryStart >= 0) {
                int arrayStart = json.indexOf("[", inventoryStart);
                int arrayEnd = arrayStart >= 0 ? indexOfClosingBracketForArray(json, arrayStart) : -1;
                if (arrayStart >= 0 && arrayEnd > arrayStart) {
                    String inventoryStr = json.substring(arrayStart + 1, arrayEnd);
                
                    // Split by items (look for { ... } patterns)
                    String[] items = inventoryStr.split("\\},\\s*\\{");
                    for (String itemStr : items) {
                        itemStr = itemStr.replace("{", "").replace("}", "").trim();
                        if (itemStr.isEmpty()) continue;
                    
                        ItemData item = new ItemData();
                        item.name = extractString(itemStr, "name");
                        item.type = extractString(itemStr, "type");
                        item.value = extractInt(itemStr, "value");
                        data.inventory.add(item);
                    }
                    normalizeAdjacentDuplicateInventoryEntries(data.inventory);
                }
            }
            
            return data;
        } catch (Exception e) {
            ErrorLogger.reportException("WM", "PARSE", "parsePlayerJson", e);
            return null;
        }
    }
    
    private static Map<String, BlockData> parseBlocksJson(String json) {
        Map<String, BlockData> blocks = new HashMap<>();
        try {
            int blocksStart = json.indexOf("\"blocks\": [");
            if (blocksStart >= 0) {
                int arrayStart = json.indexOf("[", blocksStart);
                int arrayEnd = json.lastIndexOf("]");
                String blocksStr = json.substring(arrayStart + 1, arrayEnd);
                
                String[] blockItems = blocksStr.split("\\},\\s*\\{");
                for (String blockStr : blockItems) {
                    blockStr = blockStr.replace("{", "").replace("}", "").trim();
                    if (blockStr.isEmpty()) continue;
                    
                    BlockData block = new BlockData();
                    block.x = extractInt(blockStr, "x");
                    block.y = extractInt(blockStr, "y");
                    block.type = extractString(blockStr, "type");
                    block.solid = extractBoolean(blockStr, "solid");
                    
                    String key = block.x + "," + block.y;
                    blocks.put(key, block);
                }
            }
        } catch (Exception e) {
            ErrorLogger.reportException("WM", "PARSE", "parseBlocksJson", e);
        }
        return blocks;
    }
    
    private static long extractLong(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(-?\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Long.parseLong(m.group(1));
        }
        return 0;
    }
    
    private static int extractInt(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(-?\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }
    
    private static float extractFloat(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(-?\\d+\\.?\\d*)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Float.parseFloat(m.group(1));
        }
        return 0;
    }
    
    private static String extractString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return "";
    }
    
    private static boolean extractBoolean(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        }
        return false;
    }

    /** Like extractBoolean but returns default if key is missing. */
    private static boolean extractBooleanOptional(String json, String key, boolean defaultValue) {
        String pattern = "\"" + key + "\"\\s*:\\s*(true|false)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        }
        return defaultValue;
    }

    private static int extractIntOptional(String json, String key, int defaultValue) {
        String pattern = "\"" + key + "\"\\s*:\\s*(-?\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return defaultValue;
    }

    private static long extractLongOptional(String json, String key, long defaultValue) {
        String pattern = "\"" + key + "\"\\s*:\\s*(-?\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return Long.parseLong(m.group(1));
        }
        return defaultValue;
    }

    // -------------------------------------------------------------------------
    // Per-chunk persistence (infinite worlds)
    // -------------------------------------------------------------------------

    private static final String CHUNKS_SUBDIR = "chunks";
    private static final String CHUNK_FORMAT_MARKER = ".chunks_format_v1";

    public static File getChunksDirectory(String worldName) {
        return new File(getWorldPath(worldName), CHUNKS_SUBDIR);
    }

    public static String getChunkJsonPath(String worldName, int cx, int cy) {
        return getChunksDirectory(worldName).getPath() + File.separator + cx + "_" + cy + ".json";
    }

    public static String getChunkBinaryPath(String worldName, int cx, int cy) {
        return getChunksDirectory(worldName).getPath() + File.separator + cx + "_" + cy + ".chunk";
    }

    private static final int CHUNK_BIN_MAGIC = 0x46424348; // 'FBCH'
    private static final byte CHUNK_BIN_VERSION = 2;
    /** Matches TerrariaSandbox / blocks.json removed marker. */
    private static final String REMOVED_BLOCK_TYPE = "__removed__";

    private static byte encodeEditKind(BlockData b) {
        if (b == null || REMOVED_BLOCK_TYPE.equals(b.type)) {
            return 0;
        }
        if ("torch".equals(b.type)) {
            return 1;
        }
        if ("grass".equals(b.type)) {
            return 2;
        }
        if ("dirt".equals(b.type)) {
            return 3;
        }
        if ("stone".equals(b.type)) {
            return 4;
        }
        if ("wall".equals(b.type)) {
            return 5;
        }
        return 6; // custom UTF type follows
    }

    private static BlockData decodeEdit(int px, int py, byte kind, DataInputStream in) throws IOException {
        BlockData bd = new BlockData();
        bd.x = px;
        bd.y = py;
        switch (kind) {
            case 0 -> {
                bd.type = REMOVED_BLOCK_TYPE;
                bd.solid = false;
            }
            case 1 -> {
                bd.type = "torch";
                bd.solid = false;
            }
            case 2 -> {
                bd.type = "grass";
                bd.solid = in.readBoolean();
            }
            case 3 -> {
                bd.type = "dirt";
                bd.solid = in.readBoolean();
            }
            case 4 -> {
                bd.type = "stone";
                bd.solid = in.readBoolean();
            }
            case 5 -> {
                bd.type = "wall";
                bd.solid = in.readBoolean();
            }
            case 6 -> {
                bd.type = in.readUTF();
                bd.solid = in.readBoolean();
            }
            default -> {
                bd.type = "grass";
                bd.solid = false;
            }
        }
        return bd;
    }

    private static void writeOneEdit(DataOutputStream out, BlockData b) throws IOException {
        out.writeInt(b.x);
        out.writeInt(b.y);
        byte k = encodeEditKind(b);
        out.writeByte(k);
        if (k >= 2 && k <= 5) {
            out.writeBoolean(b.solid);
        } else if (k == 6) {
            out.writeUTF(b.type != null ? b.type : "");
            out.writeBoolean(b.solid);
        }
    }

    /**
     * Saves chunk edits as compact binary ({@code .chunk}).
     */
    public static void saveChunkEditsLongMapBinary(String worldName, int cx, int cy, Map<Long, BlockData> edits) {
        if (worldName == null) {
            return;
        }
        ensureWorldsDirectory();
        File dir = getChunksDirectory(worldName);
        if (!dir.exists() && !dir.mkdirs()) {
            return;
        }
        String binPath = getChunkBinaryPath(worldName, cx, cy);
        String jsonPath = getChunkJsonPath(worldName, cx, cy);
        try {
            Files.deleteIfExists(Paths.get(jsonPath));
            if (edits == null || edits.isEmpty()) {
                Files.deleteIfExists(Paths.get(binPath));
                return;
            }
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(binPath))) {
                out.writeInt(CHUNK_BIN_MAGIC);
                out.writeByte(CHUNK_BIN_VERSION);
                out.writeInt(cx);
                out.writeInt(cy);
                out.writeInt(edits.size());
                for (BlockData b : edits.values()) {
                    if (b == null) {
                        continue;
                    }
                    writeOneEdit(out, b);
                }
            }
        } catch (IOException e) {
            ErrorLogger.reportException("WM", "IO", "saveChunkEditsLongMapBinary", e);
        }
    }

    private static Map<Long, BlockData> loadChunkEditsBinary(String worldName, int cx, int cy) {
        Map<Long, BlockData> map = new HashMap<>();
        try {
            String path = getChunkBinaryPath(worldName, cx, cy);
            File file = new File(path);
            if (!file.exists()) {
                return map;
            }
            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                int magic = in.readInt();
                if (magic != CHUNK_BIN_MAGIC) {
                    return map;
                }
                byte ver = in.readByte();
                if (ver != CHUNK_BIN_VERSION) {
                    return map;
                }
                in.readInt(); // cx
                in.readInt(); // cy
                int n = in.readInt();
                for (int i = 0; i < n; i++) {
                    int px = in.readInt();
                    int py = in.readInt();
                    byte kind = in.readByte();
                    BlockData bd = decodeEdit(px, py, kind, in);
                    map.put(WorldKeys.packWorldCell(px, py), bd);
                }
            }
        } catch (IOException e) {
            ErrorLogger.reportException("WM", "IO", "loadChunkEditsBinary", e);
        }
        return map;
    }

    /**
     * Loads chunk edits: prefers {@code .chunk} binary, falls back to legacy {@code .json}.
     */
    public static Map<Long, BlockData> loadChunkEditsLong(String worldName, int cx, int cy) {
        if (worldName == null) {
            return new HashMap<>();
        }
        Map<Long, BlockData> bin = loadChunkEditsBinary(worldName, cx, cy);
        if (!bin.isEmpty()) {
            return bin;
        }
        try {
            String path = getChunkJsonPath(worldName, cx, cy);
            File file = new File(path);
            if (!file.exists()) {
                return new HashMap<>();
            }
            String content = new String(Files.readAllBytes(Paths.get(path)));
            Map<String, BlockData> legacy = parseBlocksJson(content);
            Map<Long, BlockData> out = new HashMap<>();
            for (BlockData b : legacy.values()) {
                if (b != null) {
                    out.put(WorldKeys.packWorldCell(b.x, b.y), b);
                }
            }
            return out;
        } catch (IOException e) {
            ErrorLogger.reportException("WM", "IO", "loadChunkEditsLong", e);
            return new HashMap<>();
        }
    }

    /**
     * True after legacy {@code blocks.json} has been split into {@code chunks/} (or no edits existed).
     */
    public static boolean isChunkFormatMigrated(String worldName) {
        return new File(getWorldPath(worldName), CHUNK_FORMAT_MARKER).exists();
    }

    /**
     * Saves edits for one chunk as binary ({@code .chunk}). Accepts legacy string-key map for migration helpers.
     */
    public static void saveChunkEditsMap(String worldName, int cx, int cy, Map<String, BlockData> edits) {
        Map<Long, BlockData> m = new HashMap<>();
        if (edits != null) {
            for (BlockData b : edits.values()) {
                if (b != null) {
                    m.put(WorldKeys.packWorldCell(b.x, b.y), b);
                }
            }
        }
        saveChunkEditsLongMapBinary(worldName, cx, cy, m);
    }

    /**
     * Merges one edit when the chunk is not currently loaded (rare edge case).
     */
    public static void mergeChunkEditOnDisk(String worldName, int tileSize, int chunkTiles, BlockData bd) {
        if (worldName == null || bd == null) {
            return;
        }
        int chunkPx = chunkTiles * tileSize;
        int cx = Math.floorDiv(bd.x, chunkPx);
        int cy = Math.floorDiv(bd.y, chunkPx);
        Map<Long, BlockData> m = new HashMap<>(loadChunkEditsLong(worldName, cx, cy));
        m.put(WorldKeys.packWorldCell(bd.x, bd.y), bd);
        saveChunkEditsLongMapBinary(worldName, cx, cy, m);
    }

    /**
     * Splits monolithic {@code blocks.json} into per-chunk files and clears the legacy file.
     */
    public static void migrateLegacyBlocksJsonToChunkFiles(String worldName, int tileSize, int chunkTiles) {
        if (worldName == null) {
            return;
        }
        File root = new File(getWorldPath(worldName));
        if (!root.isDirectory()) {
            return;
        }
        File marker = new File(root, CHUNK_FORMAT_MARKER);
        if (marker.exists()) {
            return;
        }
        Map<String, BlockData> all = loadModifiedBlocks(worldName);
        if (all.isEmpty()) {
            try {
                if (!marker.createNewFile()) {
                    // best-effort
                }
            } catch (IOException ignored) {
            }
            return;
        }
        int chunkPx = chunkTiles * tileSize;
        Map<String, Map<Long, BlockData>> byChunk = new HashMap<>();
        for (BlockData b : all.values()) {
            if (b == null) {
                continue;
            }
            int cx = Math.floorDiv(b.x, chunkPx);
            int cy = Math.floorDiv(b.y, chunkPx);
            String ck = cx + "," + cy;
            byChunk.computeIfAbsent(ck, k -> new HashMap<>())
                    .put(WorldKeys.packWorldCell(b.x, b.y), b);
        }
        File chunksDir = getChunksDirectory(worldName);
        if (!chunksDir.exists() && !chunksDir.mkdirs()) {
            ErrorLogger.report("WM", "IO", "migrateLegacyBlocksJsonToChunkFiles",
                    "Could not create chunks directory for world: " + worldName);
            return;
        }
        for (Map.Entry<String, Map<Long, BlockData>> e : byChunk.entrySet()) {
            int comma = e.getKey().indexOf(',');
            if (comma < 0) {
                continue;
            }
            try {
                int cx = Integer.parseInt(e.getKey().substring(0, comma).trim());
                int cy = Integer.parseInt(e.getKey().substring(comma + 1).trim());
                saveChunkEditsLongMapBinary(worldName, cx, cy, e.getValue());
            } catch (NumberFormatException ignored) {
            }
        }
        saveModifiedBlocks(worldName, new HashMap<>());
        try {
            marker.createNewFile();
        } catch (IOException ignored) {
        }
    }
    
    // Data classes
    public static class WorldData {
        public long seed;
        public int tileSize;
        public int worldWidth;
        public int worldHeight;
        public long totalGameTicks;
        /** When true, terrain is generated in chunks around the player (see chunkTiles). */
        public boolean infinite;
        /** Edge length of one chunk in tile units (e.g. 32 = 32×32 tiles per chunk). */
        public int chunkTiles = 32;
        /** Additive offset on top of raw day/night ticks (console time set commands). */
        public long dayNightDisplayOffset;
        /** True when the day/night clock is frozen. */
        public boolean timeFrozen;
        /** Effective day/night ticks while frozen. */
        public long frozenDayNightTicks;
    }
    
    public static class PlayerData {
        public int x;
        public int y;
        public float health;
        public float stamina;
        public float hunger;
        /** Permanent unlock: advanced crafting stays available after save/load. */
        public boolean workbenchOwned;
        public List<ItemData> inventory = new ArrayList<>();
    }
    
    public static class ItemData {
        public String name;
        public String type;
        public int value;
    }
    
    public static class BlockData {
        public int x;
        public int y;
        public String type;
        public boolean solid;
    }
}
