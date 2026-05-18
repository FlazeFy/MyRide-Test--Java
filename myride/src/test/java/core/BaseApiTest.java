package core;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseApiTest {
    protected String devBaseURL;
    protected String devEmptyCaseTestURL;

    @BeforeClass
    public void setup() {
        devBaseURL = "http://127.0.0.1:8000";
        devEmptyCaseTestURL = "http://127.0.0.1:8100";

        RestAssured.baseURI = devBaseURL;
    }
}