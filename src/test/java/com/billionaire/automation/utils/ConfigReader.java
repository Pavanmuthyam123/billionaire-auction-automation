package com.billionaire.automation.utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {

    private static Properties prop;

    static {
        try {
            prop = new Properties();
            FileInputStream fis = new FileInputStream(
                System.getProperty("user.dir") +
                "/src/test/resources/config.properties"
            );
            prop.load(fis);
        } catch (Exception e) {
            throw new RuntimeException("config.properties file not found");
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }
}
