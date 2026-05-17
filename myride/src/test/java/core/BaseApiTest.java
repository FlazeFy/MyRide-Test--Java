package core;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://127.0.0.1:8000";
    }
}