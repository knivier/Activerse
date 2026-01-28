package ActiverseUtils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * WorldManager - Handles saving and loading world data
 * Manages world.json and player.json files in Worlds/[WorldName]/ directories
 * This is a significantly harder class to implement than the others, so don't mess with it unless you know what you're doing
 * 
 * @author Knivier
 * @version 1.4.1
 */
public class WorldManager {
    private static final String WORLDS_DIR = "Worlds";
    
    /**
     * Creates the Worlds directory if it doesn't exist
     */
    public static void ensureWorldsDirectory() {
        File worldsDir = new File(WORLDS_DIR);
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
        File worldsDir = new File(WORLDS_DIR);
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
        return WORLDS_DIR + File.separator + worldName;
    }
    
    /**
     * Gets the path to world.json
     */
    public static String getWorldJsonPath(String worldName) {
        return getWorldPath(worldName) + File.separator + "world.json";
    }
    
    /**
     * Gets the path to player.json
     */
    public static String getPlayerJsonPath(String worldName) {
        return getWorldPath(worldName) + File.separator + "player.json";
    }
    
    /**
     * Saves world data to world.json
     */
    public static void saveWorldData(String worldName, long seed, int tileSize, int worldWidth, int worldHeight) {
        ensureWorldsDirectory();
        String worldPath = getWorldPath(worldName);
        File worldDir = new File(worldPath);
        if (!worldDir.exists()) {
            worldDir.mkdirs();
        }
        
        try {
            String json = String.format(
                "{\n" +
                "  \"seed\": %d,\n" +
                "  \"tileSize\": %d,\n" +
                "  \"worldWidth\": %d,\n" +
                "  \"worldHeight\": %d\n" +
                "}",
                seed, tileSize, worldWidth, worldHeight
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
        ensureWorldsDirectory();
        String worldPath = getWorldPath(worldName);
        File worldDir = new File(worldPath);
        if (!worldDir.exists()) {
            worldDir.mkdirs();
        }
        
        try {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"x\": ").append(playerData.x).append(",\n");
            json.append("  \"y\": ").append(playerData.y).append(",\n");
            json.append("  \"health\": ").append(playerData.health).append(",\n");
            json.append("  \"stamina\": ").append(playerData.stamina).append(",\n");
            json.append("  \"hunger\": ").append(playerData.hunger).append(",\n");
            json.append("  \"inventory\": [\n");
            
            for (int i = 0; i < playerData.inventory.size(); i++) {
                ItemData item = playerData.inventory.get(i);
                json.append("    {\n");
                json.append("      \"name\": \"").append(escapeJson(item.name)).append("\",\n");
                json.append("      \"type\": \"").append(escapeJson(item.type)).append("\",\n");
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
            String path = getWorldPath(worldName) + File.separator + "blocks.json";
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"blocks\": [\n");
            
            int index = 0;
            for (BlockData block : modifiedBlocks.values()) {
                json.append("    {\n");
                json.append("      \"x\": ").append(block.x).append(",\n");
                json.append("      \"y\": ").append(block.y).append(",\n");
                json.append("      \"type\": \"").append(escapeJson(block.type)).append("\",\n");
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
            String path = getWorldPath(worldName) + File.separator + "blocks.json";
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
    
    private static String escapeJson(String str) {
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    private static WorldData parseWorldJson(String json) {
        WorldData data = new WorldData();
        try {
            // Simple JSON parsing
            data.seed = extractLong(json, "seed");
            data.tileSize = extractInt(json, "tileSize");
            data.worldWidth = extractInt(json, "worldWidth");
            data.worldHeight = extractInt(json, "worldHeight");
            return data;
        } catch (Exception e) {
            ErrorLogger.reportException("WM", "PARSE", "parseWorldJson", e);
            return null;
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
            
            // Parse inventory array
            data.inventory = new ArrayList<>();
            int inventoryStart = json.indexOf("\"inventory\": [");
            if (inventoryStart >= 0) {
                int arrayStart = json.indexOf("[", inventoryStart);
                int arrayEnd = json.lastIndexOf("]");
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
    
    // Data classes
    public static class WorldData {
        public long seed;
        public int tileSize;
        public int worldWidth;
        public int worldHeight;
    }
    
    public static class PlayerData {
        public int x;
        public int y;
        public float health;
        public float stamina;
        public float hunger;
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
