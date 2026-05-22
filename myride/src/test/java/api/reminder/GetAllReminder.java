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

public class GetAllReminder extends BaseApiTest {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("reminder fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("id", "vehicle_plate_number", "reminder_title", "reminder_context", "reminder_body", "created_at");

        List<String> stringNullableFields = List.of("remind_at");

        // Validate Column
        TestUtils.validateColumn(data.get("data"), stringFields, "string", false);
        TestUtils.validateColumn(data.get("data"), stringNullableFields, "string", true);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "created_at", "date_type", "datetime", "nullable", false),
                Map.of("column_name", "remind_at", "date_type", "datetime", "nullable", true)
        );

        TestUtils.validateDateTime(data.get("data"), columnDateTime);
    }

    @Test(description = "TC-INT-RM-024 : User Can See All Reminder With Valid Data")
    public void userCanSeeAllReminderWithValidData() {
        String endpoint = "/api/v1/reminder";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Reminder", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-RM-025 : User Can See All Reminder With Custom Item Per Page")
    public void userCanSeeAllReminderWithCustomItemPerPage() {
        int itemPerPage = 2;

        String endpoint = "/api/v1/reminder?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Reminder", token);

        validateValidResponse(response);

        // Check if item per page query same with data.length
        List<Object> data = response.jsonPath().getList("data.data");

        Assert.assertEquals(data.size(), itemPerPage);
    }

    @Test(description = "TC-INT-RM-026 : User Cant See All Reminder With Custom Invalid Item Per Page")
    public void userCantSeeAllReminderWithCustomInvalidItemPerPage() {
        String itemPerPage = "test";

        String endpoint = "/api/v1/reminder?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "All Reminder", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("per_page_key is not a valid page"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-RM-027 : User Cant See All Reminder With Empty Data")
    public void userCantSeeAllReminderWithEmptyData() {
        String endpoint = "/api/v1/reminder";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Reminder", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("reminder not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-RM-028 : User Cant See All Reminder With Invalid Auth")
    public void userCantSeeAllReminderWithInvalidAuth() {
        String endpoint = "/api/v1/reminder";

        Response response = templateResponseGet(endpoint, 401, "All Reminder", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-RM-029 : User Can See All Reminder With Custom Search")
    public void userCanSeeAllReminderWithCustomSearch() {
        String search = "Drop";

        String endpoint = "/api/v1/reminder?search=" + search;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Reminder", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-RM-030 : User Cant See All Reminder With Failed Custom Search")
    public void userCantSeeAllReminderWithFailedCustomSearch() {
        String search = "Lorem Sponge";

        String endpoint = "/api/v1/reminder?search=" + search;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Reminder", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("reminder not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}