package org.example.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyPropertiesReader {

    private Properties properties;

    public MyPropertiesReader() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
