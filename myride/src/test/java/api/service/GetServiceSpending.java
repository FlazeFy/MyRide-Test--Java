package api.service;

import core.AuthUtils;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static core.TestUtils.templateResponseGet;

public class GetServiceSpending {
    @Test(description = "TC-INT-SV-001 : User Can See Service Spending With Valid Data")
    public void userCanSeeServiceSpendingWithValidData() {
        String endpoint = "/api/v1/service/spending";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Service Spending", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("service fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof List);
        List<Map<String, Object>> data = jsonPath.getList("data");

        // Get List Key / Column
        List<String> stringFields = List.of("vehicle_plate_number", "vehicle_type");
        List<String> intFields = List.of("total");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
        TestUtils.validateColumn(data, intFields, "number", false);
    }

    @Test(description = "TC-INT-SV-002 : User Cant See Service Spending With Empty Data")
    public void userCantSeeServiceSpendingWithEmptyData() {
        String endpoint = "/api/v1/service/spending";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Service Spending", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("service not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-SV-003 : User Cant See Service Spending With Invalid Auth")
    public void userCantSeeServiceSpendingWithInvalidAuth() {
        String endpoint = "/api/v1/service/spending";

        Response response = templateResponseGet(endpoint, 401, "Service Spending", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}
