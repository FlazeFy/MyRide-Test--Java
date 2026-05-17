package core;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;

import static io.restassured.RestAssured.given;

public class AuthUtils {
    public static String integrationLoginAPI(String username, String password) {
        JSONObject payload = new JSONObject();
        payload.put("username", username);
        payload.put("password", password);

        Response response = given()
                .contentType("application/json")
                .body(payload.toJSONString())
                .when()
                .post("/api/v1/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();

        Assert.assertNotNull(jsonPath.get("token"));

        return jsonPath.getString("token");
    }
}