package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    public static Properties loadProperties(String env) {
        Properties prop = new Properties();

        if (env == null || env.isEmpty()) env = "staging";
        String path = System.getProperty("user.dir") + "/src/test/resources/configs/" + env + ".properties";

        try (FileInputStream fis = new FileInputStream(path)) {
            prop.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties", e);
        }
        return prop;
    }

}