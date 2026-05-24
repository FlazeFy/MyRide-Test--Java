package api.driver;

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

public class GetAllDriver extends BaseApiTest {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("driver fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("id", "username", "fullname", "email", "phone", "created_at");
        List<String> stringNullableFields = List.of("telegram_user_id", "notes", "updated_at");
        List<String> intFields = List.of("total_trip");
        List<String> intBoolFields = List.of("telegram_is_valid");

        // Validate Column
        TestUtils.validateColumn(data.get("data"), stringFields, "string", false);
        TestUtils.validateColumn(data.get("data"), stringNullableFields, "string", true);
        TestUtils.validateColumn(data.get("data"), intFields, "number", false);
        TestUtils.validateColumn(data.get("data"), intBoolFields, "bool_number", false);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "created_at", "date_type", "datetime", "nullable", false),
                Map.of("column_name", "updated_at", "date_type", "datetime", "nullable", true)
        );

        TestUtils.validateDateTime(data.get("data"), columnDateTime);
    }

    @Test(description = "TC-INT-DR-004 : User Can See All Driver With Valid Data")
    public void userCanSeeAllDriverWithValidData() {
        String endpoint = "/api/v1/driver";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Driver", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-DR-005 : User Can See All Driver With Custom Item Per Page")
    public void userCanSeeAllDriverWithCustomItemPerPage() {
        int itemPerPage = 1;

        String endpoint = "/api/v1/driver?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Driver", token);

        validateValidResponse(response);

        // Check if item per page query same with data.length
        List<Object> data = response.jsonPath().getList("data.data");

        Assert.assertEquals(data.size(), itemPerPage);
    }

    @Test(description = "TC-INT-DR-006 : User Cant See All Driver With Custom Invalid Item Per Page")
    public void userCantSeeAllDriverWithCustomInvalidItemPerPage() {
        String itemPerPage = "test";

        String endpoint = "/api/v1/driver?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "All Driver", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("per_page_key is not a valid page"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-DR-007 : User Cant See All Driver With Empty Data")
    public void userCantSeeAllDriverWithEmptyData() {
        String endpoint = "/api/v1/driver";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Driver", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("driver not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-DR-008 : User Cant See All Driver With Invalid Auth")
    public void userCantSeeAllDriverWithInvalidAuth() {
        String endpoint = "/api/v1/driver";

        Response response = templateResponseGet(endpoint, 401, "All Driver", null);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}