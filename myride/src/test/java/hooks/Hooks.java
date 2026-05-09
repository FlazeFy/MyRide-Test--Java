package hooks;

import core.BaseTest;
import core.ConfigReader;
import core.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import java.util.Properties;

public class Hooks {
    @Before
    public void beforeScenario() {
        String env = System.getProperty("env");
        env = (env == null || env.isEmpty()) ? "staging" : env;
        Properties config = ConfigReader.loadProperties(env);
        System.setProperty("baseUrl", config.getProperty("baseUrl"));

        DriverManager.initDriver("chrome");
        DriverManager.getDriver().manage().window().maximize();
        DriverManager.getDriver().get(config.getProperty("baseUrl"));
    }

    @After
    public void afterScenario() {
        DriverManager.quitDriver();
    }
}