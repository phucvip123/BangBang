package com.short_tank;


import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static ConfigLoader instance = null;
    public static ConfigLoader gI(){
        if(instance == null) instance = new ConfigLoader("config.properties");
        return instance;
    }

    private Properties properties = new Properties();
    public Boolean isRunninng = true;
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

    public int getMaxRoom() {
        return Integer.parseInt(properties.getProperty("max_room"));
    }
    public int getMaxMembersInRoom() {
        return Integer.parseInt(properties.getProperty("max_members_in_room"));
    }

}
