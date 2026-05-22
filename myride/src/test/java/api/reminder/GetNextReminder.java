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

public class GetNextReminder extends BaseApiTest {
    @Test(description = "TC-INT-RM-001 : User Can See Next Reminder With Valid Data")
    public void userCanSeeNextReminderWithValidData() {
        String endpoint = "/api/v1/reminder/next";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Next Reminder", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("reminder fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("reminder_title", "reminder_context", "reminder_body", "vehicle_plate_number");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
    }

    @Test(description = "TC-INT-RM-002 : User Cant See Next Reminder With Empty Data")
    public void userCantSeeNextReminderWithEmptyData() {
        String endpoint = "/api/v1/reminder/next";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Next Reminder", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("reminder not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-RM-003 : User Cant See Next Reminder With Invalid Auth")
    public void userCantSeeNextReminderWithInvalidAuth() {
        String endpoint = "/api/v1/reminder/next";

        Response response = templateResponseGet(endpoint, 401, "Next Reminder", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}