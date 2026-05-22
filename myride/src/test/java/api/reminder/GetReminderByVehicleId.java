package api.reminder;

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

public class GetReminderByVehicleId extends BaseApiTest {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("reminder fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof List);

        List<Map<String, Object>> data = jsonPath.getList("data");

        // Get List Key / Column
        List<String> stringFields = List.of("reminder_title", "reminder_context", "reminder_body", "remind_at");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "remind_at", "date_type", "datetime", "nullable", false)
        );

        TestUtils.validateDateTime(data, columnDateTime);
    }

    @Test(description = "TC-INT-RM-009 : User Can See Reminder By Vehicle Id With Valid Data")
    public void userCanSeeReminderByVehicleIdWithValidData() {
        String vehicleId = "7d53371a-e363-2ad3-25fe-180dae88c062";

        String endpoint = "/api/v1/reminder/vehicle/" + vehicleId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Reminder By Vehicle Id", token);

        validateValidResponse(response);
    }

    @Test(description = "TC-INT-RM-010 : User Cant See Reminder By Vehicle Id With Empty Data")
    public void userCantSeeReminderByVehicleIdWithEmptyData() {
        String vehicleId = "88a003eb-d1a6-6b3f-2015-1e11d3186975";

        String endpoint = "/api/v1/reminder/vehicle/" + vehicleId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Reminder By Vehicle Id", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("reminder not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-RM-011 : User Cant See Reminder By Vehicle Id With Not Found Vehicle")
    public void userCantSeeReminderByVehicleIdWithNotFoundVehicle() {
        String vehicleId = "88a003eb-d1a6-6b3f-2015-1e11d3186911";

        String endpoint = "/api/v1/reminder/vehicle/" + vehicleId;
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Reminder By Vehicle Id", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("vehicle not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-RM-012 : User Cant See Reminder By Vehicle Id With Invalid Auth")
    public void userCantSeeReminderByVehicleIdWithInvalidAuth() {
        String vehicleId = "7d53371a-e363-2ad3-25fe-180dae88c062";

        String endpoint = "/api/v1/reminder/vehicle/" + vehicleId;

        Response response = templateResponseGet(endpoint, 401, "Reminder By Vehicle Id", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}