package ActiverseEngine;

import ActiverseUtils.ErrorLogger;
import ActiverseUtils.IniUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * ConfigPuller — {@code game.*} from {@code settings.ini}, then bundled {@code Activerse.properties}.
 * Engine keys ({@code fps}, {@code show_debug}, {@code logging}, dynamic lighting) are read only from
 * {@code Activerse.properties}, never from {@code settings.ini}.
 *
 * @author Knivier
 * @version 1.6.0
 */
public class ConfigPuller {
    private static Properties properties = null;
    private static final String CONFIG_FILE = "Activerse.properties";

    private static final String SETTINGS_INI_FILE = "settings.ini";
    private static Properties settingsIniProperties = null;

    private static boolean isEnginePropertyKey(String key) {
        if (key == null) {
            return false;
        }
        if ("fps".equals(key) || "show_debug".equals(key) || "logging".equals(key)) {
            return true;
        }
        return isDynamicLightingAlias(key);
    }

    /**
     * Loads the properties file if not already loaded
     */
    private static void loadProperties() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream input = ConfigPuller.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (input == null) {
                    ErrorLogger.report("PROP", "IN.OUT.IO", "loadProperties()", "Activerse.properties not found. Using defaults.");
                    return;
                }
                properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            } catch (IOException e) {
                ErrorLogger.reportException("PROP", "IN.OUT.IO", "loadProperties()", e);
            }
        }

        if (settingsIniProperties == null) {
            settingsIniProperties = IniUtils.loadIni(SETTINGS_INI_FILE);
        }
    }

    /**
     * Gets a string property value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public static String getString(String key, String defaultValue) {
        loadProperties();
        String v = getStringInternal(key);
        return v != null ? v : defaultValue;
    }

    /**
     * Gets an integer property value
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Property value or default
     */
    public static int getInt(String key, int defaultValue) {
        loadProperties();
        String value = getStringInternal(key);
        if (value == null) return defaultValue;

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            ErrorLogger.report("PROP", "IN", "getInt(String key, int defaultValue)", "Invalid integer value for " + key + ": " + value + ".");
            return defaultValue;
        }
    }

    /**
     * Gets a boolean property value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        loadProperties();
        String value = getStringInternal(key);
        if (value == null) return defaultValue;

        return Boolean.parseBoolean(value.trim());
    }

    /**
     * Gets a double property value
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Property value or default
     */
    public static double getDouble(String key, double defaultValue) {
        loadProperties();
        String value = getStringInternal(key);
        if (value == null) return defaultValue;

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            ErrorLogger.report("PROP", "IN", "getDouble(String key, double defaultValue)", "Invalid double value for " + key + ": " + value + ".");
            return defaultValue;
        }
    }

    private static boolean isDynamicLightingAlias(String key) {
        if (key == null) return false;
        return key.equals("dynamic_lighting") || key.equals("dynamicLighting");
    }

    private static String dynamicLightingAliasFor(String key) {
        if ("dynamic_lighting".equals(key)) return "dynamicLighting";
        if ("dynamicLighting".equals(key)) return "dynamic_lighting";
        return key;
    }

    private static String getStringInternal(String key) {
        if (key == null) return null;

        loadProperties();

        if (isEnginePropertyKey(key)) {
            if (isDynamicLightingAlias(key)) {
                return getPropertyWithLightingAliases(properties, key);
            }
            return properties.getProperty(key);
        }

        if (isDynamicLightingAlias(key)) {
            String v = getPropertyWithLightingAliases(settingsIniProperties, key);
            if (v != null) return v;
            return getPropertyWithLightingAliases(properties, key);
        }

        String v = settingsIniProperties.getProperty(key);
        if (v != null) return v;
        return properties.getProperty(key);
    }

    private static String getPropertyWithLightingAliases(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null) {
            v = p.getProperty(dynamicLightingAliasFor(key));
        }
        return v;
    }

    /**
     * Persists engine toggles from the Settings menu to {@code Activerse.properties} on disk
     * ({@code src/Activerse.properties} when a {@code src} directory exists in the working directory,
     * otherwise {@code Activerse.properties} in the working directory). Does not modify {@code settings.ini}.
     */
    public static void saveEngineSettings(int fps, boolean showDebug, boolean logging, boolean dynamicLighting) {
        Path path = resolveWritableActiversePropertiesPath();
        Properties disk = new Properties();
        if (Files.isRegularFile(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                disk.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            } catch (IOException e) {
                ErrorLogger.reportException("PROP", "IN.OUT.IO", "saveEngineSettings load disk", e);
            }
        } else {
            loadProperties();
            for (String n : properties.stringPropertyNames()) {
                disk.setProperty(n, properties.getProperty(n));
            }
        }
        disk.setProperty("fps", Integer.toString(fps));
        disk.setProperty("show_debug", Boolean.toString(showDebug));
        disk.setProperty("logging", Boolean.toString(logging));
        disk.setProperty("dynamicLighting", Boolean.toString(dynamicLighting));
        disk.setProperty("dynamic_lighting", Boolean.toString(dynamicLighting));
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                disk.store(w, "Activerse Engine — engine-only (game keys live in settings.ini)");
            }
        } catch (IOException e) {
            ErrorLogger.reportException("PROP", "IN.OUT.IO", "saveEngineSettings store", e);
        }
        reload();
    }

    private static Path resolveWritableActiversePropertiesPath() {
        Path cwd = Paths.get(System.getProperty("user.dir", "."));
        Path srcFile = cwd.resolve("src").resolve("Activerse.properties");
        if (Files.isDirectory(cwd.resolve("src"))) {
            return srcFile;
        }
        return cwd.resolve("Activerse.properties");
    }

    /**
     * Reloads configuration from disk
     */
    public static void reload() {
        properties = null;
        settingsIniProperties = null;
        loadProperties();
    }
}
