package api.fuel;

import core.AuthUtils;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static core.TestUtils.templateResponseGet;

public class GetLastFuel {
    @Test(description = "TC-INT-FL-011 : User Can See Last Fuel With Valid Data")
    public void userCanSeeLastFuelWithValidData() {
        String endpoint = "/api/v1/fuel/last";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Last Fuel", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("fuel fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);
        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("vehicle_plate_number","vehicle_type","fuel_brand","created_at");
        List<String> stringNullableFields = List.of("fuel_type");
        List<String> intFields = List.of("fuel_volume","fuel_price_total");
        List<String> intNullableFields = List.of("fuel_ron");


        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
        TestUtils.validateColumn(data, stringNullableFields, "string", true);
        TestUtils.validateColumn(data, intFields, "number", false);
        TestUtils.validateColumn(data, intNullableFields, "number", true);
    }

    @Test(description = "TC-INT-FL-012 : User Cant See Last Fuel With Empty Data")
    public void userCantSeeLastFuelWithEmptyData() {
        String endpoint = "/api/v1/fuel/last";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Last Fuel", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("fuel not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-FL-013 : User Cant See Last Fuel With Invalid Auth")
    public void userCantSeeLastFuelWithInvalidAuth() {
        String endpoint = "/api/v1/fuel/last";

        Response response = templateResponseGet(endpoint, 401, "Last Fuel", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}
