package api.fuel;

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

public class GetAllFuel extends BaseApiTest {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("fuel fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("id", "vehicle_plate_number", "vehicle_type", "fuel_brand", "created_at");
        List<String> stringNullableFields = List.of("fuel_type", "fuel_bill");
        List<String> intFields = List.of("fuel_volume", "fuel_price_total");
        List<String> intNullableFields = List.of("fuel_ron");

        // Validate Column
        TestUtils.validateColumn(data.get("data"), stringFields, "string", false);
        TestUtils.validateColumn(data.get("data"), stringNullableFields, "string", true);
        TestUtils.validateColumn(data.get("data"), intFields, "number", false);
        TestUtils.validateColumn(data.get("data"), intNullableFields, "number", true);

        // Validate Contain
        TestUtils.validateContain(data.get("data"), List.of("Pertamina", "Vivo", "BP", "Shell", "Electric"), "fuel_brand", false);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "created_at", "date_type", "datetime", "nullable", true)
        );

        TestUtils.validateDateTime(data.get("data"), columnDateTime);
    }

    @Test(description = "TC-INT-FL-001 : User Can See All Fuel With Valid Data")
    public void userCanSeeAllFuelWithValidData() {
        String endpoint = "/api/v1/fuel";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Fuel", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-FL-002 : User Can See All Fuel With Custom Item Per Page")
    public void userCanSeeAllFuelWithCustomItemPerPage() {
        int itemPerPage = 2;

        String endpoint = "/api/v1/fuel?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Fuel", token);

        validateValidResponse(response);

        // Check if item per page query same with data.length
        List<Object> data = response.jsonPath().getList("data.data");

        Assert.assertEquals(data.size(), itemPerPage);
    }

    @Test(description = "TC-INT-FL-003 : User Cant See All Fuel With Custom Invalid Item Per Page")
    public void userCantSeeAllFuelWithCustomInvalidItemPerPage() {
        String itemPerPage = "test";

        String endpoint = "/api/v1/fuel?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "All Fuel", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("per_page_key is not a valid page"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-FL-004 : User Cant See All Fuel With Empty Data")
    public void userCantSeeAllFuelWithEmptyData() {
        String endpoint = "/api/v1/fuel";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Fuel", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("fuel not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-FL-005 : User Cant See All Fuel With Invalid Auth")
    public void userCantSeeAllFuelWithInvalidAuth() {
        String endpoint = "/api/v1/fuel";

        Response response = templateResponseGet(endpoint, 401, "All Fuel", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-FL-006 : User Cant See All Fuel With Custom Invalid Vehicle Id (UUID)")
    public void userCantSeeAllFuelWithCustomInvalidVehicleIdUUID() {
        String vehicleId = "1";

        String endpoint = "/api/v1/fuel?vehicle_id=" + vehicleId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "All Fuel", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("vehicle_id must be a valid UUID"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-FL-007 : User Cant See All Fuel With Custom Invalid Vehicle Id (Not Found)")
    public void userCantSeeAllFuelWithCustomInvalidVehicleIdNotFound() {
        String vehicleId = "da79e9ba-bc19-2186-2f4d-c755ec841234";

        String endpoint = "/api/v1/fuel?vehicle_id=" + vehicleId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Fuel", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("fuel not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-FL-032 : User Can See All Fuel With Valid Data With Custom Valid Vehicle Id")
    public void userCanSeeAllFuelWithValidDataWithCustomValidVehicleId() {
        String vehicleId = "7d53371a-e363-2ad3-25fe-180dae88c062";
        String endpoint = "/api/v1/fuel?vehicle_id=" + vehicleId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Fuel", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }
}