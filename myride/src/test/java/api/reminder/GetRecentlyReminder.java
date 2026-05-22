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

public class GetRecentlyReminder extends BaseApiTest {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("reminder fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("id", "vehicle_plate_number", "reminder_title", "reminder_context", "reminder_body", "remind_at");

        // Validate Column
        TestUtils.validateColumn(data.get("data"), stringFields, "string", false);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "remind_at", "date_type", "datetime", "nullable", false)
        );

        TestUtils.validateDateTime(data.get("data"), columnDateTime);
    }

    @Test(description = "TC-INT-RM-004 : User Can See Recently Reminder With Valid Data")
    public void userCanSeeRecentlyReminderWithValidData() {
        String endpoint = "/api/v1/reminder/recently";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Recently Reminder", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-RM-005 : User Can See Recently Reminder With Custom Item Per Page")
    public void userCanSeeRecentlyReminderWithCustomItemPerPage() {
        int itemPerPage = 1;

        String endpoint = "/api/v1/reminder/recently?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Recently Reminder", token);

        validateValidResponse(response);

        // Check if item per page query same with data.length
        List<Object> data = response.jsonPath().getList("data.data");

        Assert.assertEquals(data.size(), itemPerPage);
    }

    @Test(description = "TC-INT-RM-006 : User Cant See Recently Reminder With Custom Invalid Item Per Page")
    public void userCantSeeRecentlyReminderWithCustomInvalidItemPerPage() {
        String itemPerPage = "test";

        String endpoint = "/api/v1/reminder/recently?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "Recently Reminder", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("per_page_key is not a valid page"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-RM-007 : User Cant See Recently Reminder With Empty Data")
    public void userCantSeeRecentlyReminderWithEmptyData() {
        String endpoint = "/api/v1/reminder/recently";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Recently Reminder", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("reminder not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-RM-008 : User Cant See Recently Reminder With Invalid Auth")
    public void userCantSeeRecentlyReminderWithInvalidAuth() {
        String endpoint = "/api/v1/reminder/recently";

        Response response = templateResponseGet(endpoint, 401, "Recently Reminder", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}