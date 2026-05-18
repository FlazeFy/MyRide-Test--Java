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

public class GetTotalTripByContext {
    @Test(description = "TC-INT-ST-002 : User Can See Total Trip By Context With Valid Context And Valid Data")
    public void userCanSeeTotalTripByContextWithValidContextAndValidData() {
        String endpoint = "/api/v1/stats/total/trip/trip_category";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Total Trip By Context", token);

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

    @Test(description = "TC-INT-ST-003 : User Cant See Total Trip By Context With Invalid Context")
    public void userCantSeeTotalTripByContextWithInvalidContext() {
        String endpoint = "/api/v1/stats/total/trip/trip_categories";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "Total Trip By Context", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("trip_categories is not available"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-ST-004 : User Cant See Total Trip By Context With Valid Context And Empty Data")
    public void userCantSeeTotalTripByContextWithValidContextAndEmptyData() {
        String endpoint = "/api/v1/stats/total/trip/trip_category";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Total Trip By Context", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("stats not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-ST-005 : User Cant See Total Trip By Context With Invalid Auth")
    public void userCantSeeTotalTripByContextWithInvalidAuth() {
        String endpoint = "/api/v1/stats/total/trip/trip_category";

        Response response = templateResponseGet(endpoint, 401, "Total Trip By Context", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}
