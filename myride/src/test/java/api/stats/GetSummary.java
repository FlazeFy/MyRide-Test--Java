package api.stats;

import core.BaseApiTest;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class GetSummary extends BaseApiTest {
    @Test(description = "TC-INT-ST-001 : User Can See Summary With Valid Data")
    public void successGetSummaryWithValidData() {
        String endpoint = "/api/v1/stats/summary";

        Response response = given()
                .contentType("application/json")
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("==== GET : Summary ====");
        System.out.println("Status Code : " + response.getStatusCode());
        System.out.println("Response : ");
        System.out.println(response.asPrettyString());

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("stats fetched"));

        // Validate data exists
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);
        Map<String, Object> data = jsonPath.getMap("data");
        Assert.assertFalse(data.isEmpty());

        // Validate column list
        List<String> intFields = List.of("total_user", "total_vehicle", "total_service", "total_wash", "total_trip");

        // Validate columns
        TestUtils.validateColumn(data, intFields, "number", false);
    }
}