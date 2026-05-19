package api.stats;

import core.AuthUtils;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static core.TestUtils.isContainedInList;
import static core.TestUtils.templateResponseGet;

public class GetTotalTripMonthlyByYearAndVehicleId {
    @Test(description = "TC-INT-ST-010 : User Can See Total Trip Monthly By Year And Vehicle Id With Valid Year Vehicle And Valid Data")
    public void userCanSeeTotalTripMonthlyByYearAndVehicleIdWithValidYearVehicleAndValidData() {
        String vehicleId = "7d53371a-e363-2ad3-25fe-180dae88c062";
        int year = 2026;
        String endpoint = "/api/v1/stats/total/trip/monthly/"+year+"/"+vehicleId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "Total Trip Monthly By Year And Vehicle", token);

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

    @Test(description = "TC-INT-ST-011 : User Cant See Total Trip Monthly By Year And Vehicle Id With Invalid Vehicle Id")
    public void userCantSeeTotalTripMonthlyByYearAndVehicleIdWithInvalidVehicleId() {
        String vehicleId = "1";
        int year = 2024;
        String endpoint = "/api/v1/stats/total/trip/monthly/"+year+"/"+vehicleId;
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "Total Trip Monthly By Year And Vehicle", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(isContainedInList(jsonPath.get("message.id"), "The id field must be 36 characters."));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-ST-012 : User Can See Total Trip By Context With Valid Context And Empty Data")
    public void userCanSeeTripMonthlyByYearAndVehicleIdWithEmptyData() {
        String vehicleId = "2d98f524-de02-11ed-b5ea-0242ac120002";
        int year = 2020;
        String endpoint = "/api/v1/stats/total/trip/monthly/"+year+"/"+vehicleId;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "Total Trip Monthly By Year And Vehicle", token);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("stats not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-ST-013 : User Cant See Total Trip By Context With Invalid Auth")
    public void userCantSeeTripMonthlyByYearAndVehicleIdWithInvalidAuth() {
        String vehicleId = "2d98f524-de02-11ed-b5ea-0242ac120002";
        int year = 2024;
        String endpoint = "/api/v1/stats/total/trip/monthly/"+year+"/"+vehicleId;

        Response response = templateResponseGet(endpoint, 401, "Total Trip Monthly By Year And Vehicle", null);

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}
