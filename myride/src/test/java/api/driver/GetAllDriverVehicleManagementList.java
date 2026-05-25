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

public class GetAllDriverVehicleManagementList extends BaseApiTest {
    @Test(description = "TC-INT-DR-014 : User Can See All Driver Vehicle Management List With Valid Data")
    public void userCanSeeAllDriverVehicleManagementListWithValidData() {
        String endpoint = "/api/v1/driver/vehicle/list";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Driver Vehicle Management List", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("driver fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringVehicleFields = List.of("id", "vehicle_name", "vehicle_plate_number");
        List<String> stringNullableVehicleFields = List.of("deleted_at");
        List<String> stringDriverFields = List.of("id", "username", "fullname");
        List<String> stringAssignedFields = List.of("id", "vehicle_id", "vehicle_plate_number", "driver_id", "username", "fullname");

        // Validate Column
        if (data.get("vehicle") != null) {
            TestUtils.validateColumn(data.get("vehicle"), stringVehicleFields, "string", false);
            TestUtils.validateColumn(data.get("vehicle"), stringNullableVehicleFields, "string", true);
        }

        if (data.get("driver") != null) {
            TestUtils.validateColumn(data.get("driver"), stringDriverFields, "string", false);
        }

        if (data.get("assigned") != null) {
            TestUtils.validateColumn(data.get("assigned"), stringAssignedFields, "string", false);
        }
    }

    @Test(description = "TC-INT-DR-015 : User Cant See All Driver Vehicle Management List With Empty Data")
    public void userCantSeeAllDriverVehicleManagementListWithEmptyData() {
        String endpoint = "/api/v1/driver/vehicle/list";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Driver Vehicle Management List", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("driver not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-DR-016 : User Cant See All Driver Vehicle Management List With Invalid Auth")
    public void userCantSeeAllDriverVehicleManagementListWithInvalidAuth() {
        String endpoint = "/api/v1/driver/vehicle/list";

        Response response = templateResponseGet(endpoint, 401, "All Driver Vehicle Management List", null);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}