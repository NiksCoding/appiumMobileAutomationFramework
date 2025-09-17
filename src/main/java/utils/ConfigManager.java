package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static Properties properties = new Properties();
    private static boolean isLoaded = false;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            // Try to load from classpath first
            InputStream inputStream = ConfigManager.class.getClassLoader()
                    .getResourceAsStream("config.properties");

            if (inputStream != null) {
                properties.load(inputStream);
                inputStream.close();
                isLoaded = true;
                logger.info("Configuration loaded from classpath");
            } else {
                // Fallback to file system
                String configPath = System.getProperty("config.path", "src/test/resources/config.properties");
                try (FileInputStream fileInputStream = new FileInputStream(configPath)) {
                    properties.load(fileInputStream);
                    isLoaded = true;
                    logger.info("Configuration loaded from file: {}", configPath);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration properties", e);
            throw new RuntimeException("Configuration loading failed", e);
        }
    }

    public static String getProperty(String key) {
        if (!isLoaded) {
            loadProperties();
        }

        // Check system properties first (for runtime overrides)
        String systemProperty = System.getProperty(key);
        if (systemProperty != null && !systemProperty.isEmpty()) {
            return systemProperty;
        }

        String value = properties.getProperty(key);
        if (value == null) {
            logger.warn("Property '{}' not found in configuration", key);
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property '{}': {}", key, value);
            return defaultValue;
        }
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        logger.debug("Property '{}' set to '{}'", key, value);
    }
}
