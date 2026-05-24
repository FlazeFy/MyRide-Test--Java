package api.driver;

import core.AuthUtils;
import core.BaseApiTest;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import static core.TestUtils.templateResponseGet;

public class GetAllDriverName extends BaseApiTest {
    @Test(description = "TC-INT-DR-001 : User Can See All Driver Name With Valid Data")
    public void userCanSeeAllDriverNameWithValidData() {
        String endpoint = "/api/v1/driver/name";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Driver Name", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("driver fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof List);

        List<Object> data = jsonPath.getList("data");

        // Get List Key / Column
        List<String> stringFields = List.of("id", "username", "fullname");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
    }

    @Test(description = "TC-INT-DR-002 : User Cant See All Driver Name With Empty Data")
    public void userCantSeeAllDriverNameWithEmptyData() {
        String endpoint = "/api/v1/driver/name";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Driver Name", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("driver not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-DR-003 : User Cant See All Driver Name With Invalid Auth")
    public void userCantSeeAllDriverNameWithInvalidAuth() {
        String endpoint = "/api/v1/driver/name";

        Response response = templateResponseGet(endpoint, 401, "All Driver Name", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}