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

public class GetAllDriverVehicle extends BaseApiTest {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("driver fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("username", "fullname", "email", "phone");
        List<String> stringNullableFields = List.of("telegram_user_id", "vehicle_list");
        List<String> intBoolFields = List.of("telegram_is_valid");

        // Validate Column
        TestUtils.validateColumn(data.get("data"), stringFields, "string", false);
        TestUtils.validateColumn(data.get("data"), stringNullableFields, "string", true);
        TestUtils.validateColumn(data.get("data"), intBoolFields, "bool_number", false);
    }

    @Test(description = "TC-INT-DR-009 : User Can See All Driver Vehicle With Valid Data")
    public void userCanSeeAllDriverVehicleWithValidData() {
        String endpoint = "/api/v1/driver/vehicle";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Driver Vehicle", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-DR-010 : User Can See All Driver Vehicle With Custom Item Per Page")
    public void userCanSeeAllDriverVehicleWithCustomItemPerPage() {
        int itemPerPage = 1;

        String endpoint = "/api/v1/driver/vehicle?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Driver Vehicle", token);

        validateValidResponse(response);

        // Check if item per page query same with data.length
        List<Object> data = response.jsonPath().getList("data.data");

        Assert.assertEquals(data.size(), itemPerPage);
    }

    @Test(description = "TC-INT-DR-011 : User Cant See All Driver Vehicle With Custom Invalid Item Per Page")
    public void userCantSeeAllDriverVehicleWithCustomInvalidItemPerPage() {
        String itemPerPage = "test";

        String endpoint = "/api/v1/driver/vehicle?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "All Driver Vehicle", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("per_page_key is not a valid page"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-DR-012 : User Cant See All Driver Vehicle With Empty Data")
    public void userCantSeeAllDriverVehicleWithEmptyData() {
        String endpoint = "/api/v1/driver/vehicle";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Driver Vehicle", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("driver not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-DR-013 : User Cant See All Driver Vehicle With Invalid Auth")
    public void userCantSeeAllDriverVehicleWithInvalidAuth() {
        String endpoint = "/api/v1/driver/vehicle";

        Response response = templateResponseGet(endpoint, 401, "All Driver Vehicle", null);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}