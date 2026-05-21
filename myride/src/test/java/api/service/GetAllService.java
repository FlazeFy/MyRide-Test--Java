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

public class GetAllService extends BaseApiTest {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("service fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("id", "vehicle_plate_number", "vehicle_type", "service_category", "service_location", "created_at");
        List<String> stringNullableFields = List.of("updated_at", "service_note", "remind_at");
        List<String> intNullableFields = List.of("service_price_total");

        // Validate Column
        TestUtils.validateColumn(data.get("data"), stringFields, "string", false);
        TestUtils.validateColumn(data.get("data"), stringNullableFields, "string", true);
        TestUtils.validateColumn(data.get("data"), intNullableFields, "number", true);

        // Validate Contain
        TestUtils.validateContain(data.get("data"), List.of("Routine", "Repair", "Inspection", "Emergency"), "service_category", false);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "created_at", "date_type", "datetime", "nullable", false),
                Map.of("column_name", "updated_at", "date_type", "datetime", "nullable", true),
                Map.of("column_name", "remind_at", "date_type", "datetime", "nullable", true)
        );

        TestUtils.validateDateTime(data.get("data"), columnDateTime);
    }

    @Test(description = "TC-INT-SV-004 : User Can See All Service With Valid Data")
    public void userCanSeeAllServiceWithValidData() {
        String endpoint = "/api/v1/service";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Service", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-SV-005 : User Can See All Service With Custom Item Per Page")
    public void userCanSeeAllServiceWithCustomItemPerPage() {
        int itemPerPage = 2;

        String endpoint = "/api/v1/service?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Service", token);

        validateValidResponse(response);

        // Check if item per page query same with data.length
        List<Object> data = response.jsonPath().getList("data.data");

        Assert.assertEquals(data.size(), itemPerPage);
    }

    @Test(description = "TC-INT-SV-006 : User Cant See All Service With Custom Invalid Item Per Page")
    public void userCantSeeAllServiceWithCustomInvalidItemPerPage() {
        String itemPerPage = "test";

        String endpoint = "/api/v1/service?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "All Service", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("per_page_key is not a valid page"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-SV-007 : User Cant See All Service With Empty Data")
    public void userCantSeeAllServiceWithEmptyData() {
        String endpoint = "/api/v1/service";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Service", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("service not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-SV-008 : User Cant See All Service With Invalid Auth")
    public void userCantSeeAllServiceWithInvalidAuth() {
        String endpoint = "/api/v1/service";

        Response response = templateResponseGet(endpoint, 401, "All Service", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-SV-009 : User Cant See All Service With Custom Invalid Vehicle Id (UUID)")
    public void userCantSeeAllServiceWithCustomInvalidVehicleIdUUID() {
        String serviceId = "1";

        String endpoint = "/api/v1/service?vehicle_id=" + serviceId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "All Service", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("vehicle_id must be a valid UUID"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-SV-010 : User Cant See All Service With Custom Invalid Vehicle Id (Not Found)")
    public void userCantSeeAllServiceWithCustomInvalidVehicleIdNotFound() {
        String serviceId = "da79e9ba-bc19-2186-2f4d-c755ec841234";

        String endpoint = "/api/v1/service?vehicle_id=" + serviceId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Service", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("service not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-SV-011 : User Can See All Service With Custom Search")
    public void userCanSeeAllServiceWithCustomSearch() {
        String search = "brake";

        String endpoint = "/api/v1/service?search=" + search;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Service", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-SV-012 : User Cant See All Service With Failed Custom Search")
    public void userCantSeeAllServiceWithFailedCustomSearch() {
        String search = "not found";

        String endpoint = "/api/v1/service?search=" + search;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Service", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("service not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}