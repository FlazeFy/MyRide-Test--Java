package api.fuel;

import core.AuthUtils;
import core.TestUtils;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static core.TestUtils.templateResponseGet;

public class GetFuelSummary {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("fuel fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);
        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> intFields = List.of("total_fuel_price","total_fuel_volume","total_refueling");

        // Validate Column
        TestUtils.validateColumn(data, intFields, "number", false);
    }
    
    @Test(description = "TC-INT-FL-008 : User Can See Fuel Summary With Valid Data")
    public void userCanSeeFuelSummaryWithValidData() {
        String endpoint = "/api/v1/fuel/summary/01-2026";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Fuel Summary", token);

        validateValidResponse(response);
    }

    @Test(description = "TC-INT-FL-009 : User Can See Fuel Summary With Empty Data")
    public void userCanSeeFuelSummaryWithEmptyData() {
        String endpoint = "/api/v1/fuel/summary/01-2022";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Fuel Summary", token);

        validateValidResponse(response);
    }

    @Test(description = "TC-INT-FL-010 : User Cant See Fuel Summary With Invalid Auth")
    public void userCantSeeFuelSummaryWithInvalidAuth() {
        String endpoint = "/api/v1/fuel/summary/01-2026";

        Response response = templateResponseGet(endpoint, 401, "Fuel Summary", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}
