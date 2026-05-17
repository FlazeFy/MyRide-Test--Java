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
import static io.restassured.RestAssured.given;

public class GetTripPartner {
    @Test(description = "TC-INT-ST-006 : User Can See List Of Partner Trip")
    public void userCanSeeListOfTripPartner() {
        String endpoint = "/api/v1/stats/partner";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Trip Partner", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("stats fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof List);
        List<Map<String, Object>> data = jsonPath.getList("data");

        // Get List Key / Column
        List<String> stringFields = List.of("name","favorite_day","last_trip");
        List<String> intFields = List.of("total_trip","total_distance");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
        TestUtils.validateColumn(data, intFields, "number", false);
    }

    @Test(description = "TC-INT-ST-007 : User Cant See Partner Trip List When No Trip Data Exists")
    public void userCantSeePartnerTripListWhenNoTripDataExists() {
        String endpoint = "/api/v1/stats/partner";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Trip Partner", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("stats not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-ST-008 : User Cant See Partner Trip List With Invalid Auth")
    public void userCantSeePartnerTripListWithInvalidAuth() {
        String endpoint = "/api/v1/stats/partner";

        Response response = templateResponseGet(endpoint, 401, "Trip Partner", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}
