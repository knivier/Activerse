package ActiverseEngine;

import ActiverseUtils.ErrorLogger;
import ActiverseUtils.IniUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigPuller - Utility for loading configuration from Activerse.properties
 * Provides centralized configuration management for ActiverseEngine applications
 * 
 * @author Knivier
 * @version 1.4.1
 */
public class ConfigPuller {
    private static Properties properties = null;
    private static final String CONFIG_FILE = "Activerse.properties";

    // Global system settings (game-wide, not world specific).
    // Loaded from the working directory (e.g. project root) so users can edit it.
    private static final String SYSTEM_INI_FILE = "system.ini";
    private static Properties systemIniProperties = null;
    
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
                properties.load(input);
            } catch (IOException e) {
                ErrorLogger.reportException("PROP", "IN.OUT.IO", "loadProperties()", e);
            }
        }

        if (systemIniProperties == null) {
            systemIniProperties = IniUtils.loadIni(SYSTEM_INI_FILE);
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
        String v = null;

        // Allow both spellings for dynamic lighting since the repo uses inconsistent keys.
        if (key != null && isDynamicLightingAlias(key)) {
            v = systemIniProperties.getProperty(key);
            if (v == null) v = systemIniProperties.getProperty(dynamicLightingAliasFor(key));
            if (v != null) return v;

            v = properties.getProperty(key);
            if (v == null) v = properties.getProperty(dynamicLightingAliasFor(key));
            return v != null ? v : defaultValue;
        }

        v = systemIniProperties.getProperty(key);
        if (v == null) v = properties.getProperty(key);
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

        // Prefer system.ini overrides.
        if (isDynamicLightingAlias(key)) {
            String v = systemIniProperties.getProperty(key);
            if (v == null) v = systemIniProperties.getProperty(dynamicLightingAliasFor(key));
            if (v != null) return v;

            v = properties.getProperty(key);
            if (v == null) v = properties.getProperty(dynamicLightingAliasFor(key));
            return v;
        }

        String v = systemIniProperties.getProperty(key);
        if (v == null) v = properties.getProperty(key);
        return v;
    }
    
    /**
     * Reloads the properties file from disk
     */
    public static void reload() {
        properties = null;
        systemIniProperties = null;
        loadProperties();
    }
}
