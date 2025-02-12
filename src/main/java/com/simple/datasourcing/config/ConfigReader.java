package com.simple.datasourcing.config;

import lombok.extern.slf4j.*;

import java.io.*;
import java.util.*;

@Slf4j
public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        var defaultProperties = "application.properties";
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(defaultProperties)) {
            if (input == null) log.error("{} not found", defaultProperties);
            else properties.load(input);
        } catch (IOException ex) {
            log.error("Exception on getting {}} : {}", defaultProperties, ex.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}