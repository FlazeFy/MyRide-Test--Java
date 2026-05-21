package api.service;

import core.AuthUtils;
import core.BaseApiTest;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;
import static core.TestUtils.templateResponseGet;

public class GetServiceByVehicleId extends BaseApiTest {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("service fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof List);

        List<Map<String, Object>> data = jsonPath.getList("data");

        // Get List Key / Column
        List<String> stringFields = List.of("service_category", "service_location", "created_at");
        List<String> stringNullableFields = List.of("service_note", "remind_at");
        List<String> intNullableFields = List.of("service_price_total");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
        TestUtils.validateColumn(data, stringNullableFields, "string", true);
        TestUtils.validateColumn(data, intNullableFields, "number", false);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "created_at", "date_type", "datetime", "nullable", false),
                Map.of("column_name", "remind_at", "date_type", "datetime", "nullable", true)
        );

        TestUtils.validateDateTime(data, columnDateTime);
    }

    @Test(description = "TC-INT-SV-016 : User Can See Service By Vehicle Id With Valid Data")
    public void userCanSeeServiceByVehicleIdWithValidData() {
        String vehicleId = "7d53371a-e363-2ad3-25fe-180dae88c062";

        String endpoint = "/api/v1/service/vehicle/" + vehicleId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Service By Vehicle Id", token);

        validateValidResponse(response);
    }

    @Test(description = "TC-INT-SV-017 : User Cant See Service By Vehicle Id With Empty Data")
    public void userCantSeeServiceByVehicleIdWithEmptyData() {
        String vehicleId = "88a003eb-d1a6-6b3f-2015-1e11d3186975";

        String endpoint = "/api/v1/service/vehicle/" + vehicleId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Service By Vehicle Id", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("service not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-SV-018 : User Cant See Service By Vehicle Id With Not Found Vehicle")
    public void userCantSeeServiceByVehicleIdWithNotFoundVehicle() {
        String vehicleId = "88a003eb-d1a6-6b3f-2015-1e11d3186911";

        String endpoint = "/api/v1/service/vehicle/" + vehicleId;
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Service By Vehicle Id", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("vehicle not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-SV-019 : User Cant See Service By Vehicle Id With Invalid Auth")
    public void userCantSeeServiceByVehicleIdWithInvalidAuth() {
        String vehicleId = "7d53371a-e363-2ad3-25fe-180dae88c062";

        String endpoint = "/api/v1/service/vehicle/" + vehicleId;

        Response response = templateResponseGet(endpoint, 401, "Service By Vehicle Id", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}