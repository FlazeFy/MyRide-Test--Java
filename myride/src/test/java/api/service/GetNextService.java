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

public class GetNextService extends BaseApiTest {
    @Test(description = "TC-INT-SV-013 : User Can See Next Service With Valid Data")
    public void userCanSeeNextServiceWithValidData() {
        String endpoint = "/api/v1/service/next";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Next Service", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("service fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("service_category", "service_location", "vehicle_plate_number", "remind_at");

        // In the database, the remind_at column is nullable.
        // However, for this feature, it is always filled,
        // because the data is retrieved based on whether remind_at is defined or not
        List<String> stringNullableFields = List.of("service_note");
        List<String> intNullableFields = List.of("service_price_total");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
        TestUtils.validateColumn(data, stringNullableFields, "string", true);
        TestUtils.validateColumn(data, intNullableFields, "number", true);

        // Validate Contain
        TestUtils.validateContain(data, List.of("Routine", "Repair", "Inspection", "Emergency"), "service_category", false);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of(
                        "column_name", "remind_at",
                        "date_type", "datetime",
                        "nullable", false
                )
        );

        TestUtils.validateDateTime(data, columnDateTime);
    }

    @Test(description = "TC-INT-SV-014 : User Cant See Next Service With Empty Data")
    public void userCantSeeNextServiceWithEmptyData() {
        String endpoint = "/api/v1/service/next";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Next Service", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("service not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-SV-015 : User Cant See Next Service With Invalid Auth")
    public void userCantSeeNextServiceWithInvalidAuth() {
        String endpoint = "/api/v1/service/next";

        Response response = templateResponseGet(endpoint, 401, "Next Service", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}