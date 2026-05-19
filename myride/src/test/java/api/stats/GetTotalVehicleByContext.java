package api.stats;

import core.AuthUtils;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static core.TestUtils.templateResponseGet;

public class GetTotalVehicleByContext {
    @Test(description = "TC-INT-ST-014 : User Can See Total Vehicle By Context With Valid Context And Valid Data")
    public void userCanSeeTotalVehicleByContextWithValidContextAndValidData() {
        String endpoint = "/api/v1/stats/total/vehicle/vehicle_merk";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Total Vehicle By Context", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("stats fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof List);
        List<Map<String, Object>> data = jsonPath.getList("data");

        // Get List Key / Column
        List<String> stringFields = List.of("context");
        List<String> intFields = List.of("total");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
        TestUtils.validateColumn(data, intFields, "number", false);
    }

    @Test(description = "TC-INT-ST-015 : User Cant See Total Vehicle By Context With Invalid Context")
    public void userCantSeeTotalVehicleByContextWithInvalidContext() {
        String endpoint = "/api/v1/stats/total/vehicle/vehicle_brand";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "Total Vehicle By Context", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("vehicle_brand is not available"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-ST-016 : User Cant See Total Vehicle By Context With Valid Context And Empty Data")
    public void userCantSeeTotalVehicleByContextWithValidContextAndEmptyData() {
        String endpoint = "/api/v1/stats/total/vehicle/vehicle_merk";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Total Vehicle By Context", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("stats not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-ST-017 : User Cant See Total Vehicle By Context With Invalid Auth")
    public void userCantSeeTotalVehicleByContextWithInvalidAuth() {
        String endpoint = "/api/v1/stats/total/vehicle/vehicle_merk";

        Response response = templateResponseGet(endpoint, 401, "Total Vehicle By Context", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}
