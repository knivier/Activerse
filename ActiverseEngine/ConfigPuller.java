package ActiverseEngine;

import ActiverseUtils.ErrorLogger;

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
    }
    
    /**
     * Gets a string property value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default
     */
    public static String getString(String key, String defaultValue) {
        loadProperties();
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Gets an integer property value
     * @param key Property key
     * @param defaultValue Default value if property not found or invalid
     * @return Property value or default
     */
    public static int getInt(String key, int defaultValue) {
        loadProperties();
        String value = properties.getProperty(key);
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
        String value = properties.getProperty(key);
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
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            ErrorLogger.report("PROP", "IN", "getDouble(String key, double defaultValue)", "Invalid double value for " + key + ": " + value + ".");
            return defaultValue;
        }
    }
    
    /**
     * Reloads the properties file from disk
     */
    public static void reload() {
        properties = null;
        loadProperties();
    }
}
