package com.shot_tank;


import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private Properties properties = new Properties();
    private static ConfigLoader instance;
    public Boolean isRunninng = true;
    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader("config.properties");
        }
        return instance;
    }
    public ConfigLoader(String filename) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                throw new RuntimeException("Cannot find config file: " + filename);
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage());
        }
    }

    public String getHost() {
        return properties.getProperty("host");
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("port"));
    }

}
