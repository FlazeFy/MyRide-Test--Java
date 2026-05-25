package api.auth;

import core.AuthUtils;
import core.BaseApiTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static core.TestUtils.templateResponsePost;

public class PostSignOut extends BaseApiTest {
    @Test(description = "TC-INT-AU-005 : User Can Sign Out With Valid Token")
    public void userCanSignOutWithValidToken() {
        String endpoint = "/api/v1/logout";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponsePost(endpoint, 200, "Sign Out", null, token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("logout success"));
    }

    @Test(description = "TC-INT-AU-006 : User Cant Sign Out With Empty Token")
    public void userCantSignOutWithEmptyToken() {
        String endpoint = "/api/v1/logout";

        Response response = templateResponsePost(endpoint, 401, "Sign Out", null, null);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));
    }

    @Test(description = "TC-INT-AU-007 : User Cant Sign Out With Invalid Token")
    public void userCantSignOutWithInvalidToken() {
        String endpoint = "/api/v1/logout";
        String token = "123123";

        Response response = templateResponsePost(endpoint, 401, "Sign Out", null, token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));
    }
}