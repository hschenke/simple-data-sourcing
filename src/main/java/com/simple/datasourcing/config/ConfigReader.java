package com.simple.datasourcing.config;

import java.io.*;
import java.util.*;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.out.println("application.properties nicht gefunden!");
            }
        } catch (IOException ex) {

        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}