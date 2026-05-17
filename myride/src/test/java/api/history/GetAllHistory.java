package api.history;

import core.AuthUtils;
import core.BaseApiTest;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class GetAllHistory extends BaseApiTest {

    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("history fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("id", "history_type", "history_context", "created_at");

        // Validate Column
        TestUtils.validateColumn(data.get("data"), stringFields, "string", false);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "created_at", "date_type", "datetime", "nullable", false)
        );

        TestUtils.validateDateTime(data.get("data"), columnDateTime);
    }

    @Test(description = "TC-INT-HS-001 : User Can See All History With Valid Data")
    public void userCanSeeAllHistoryWithValidData() {
        String endpoint = "/api/v1/history";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("==== GET : All History ====");
        System.out.println("Status Code : " + response.getStatusCode());
        System.out.println("Response : ");
        System.out.println(response.asPrettyString());

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-HS-002 : User Can See All History With Custom Item Per Page")
    public void userCanSeeAllHistoryWithCustomItemPerPage() {
        String endpoint = "/api/v1/history";
        int itemPerPage = 2;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .queryParam("per_page_key", itemPerPage)
                .when()
                .get(endpoint)
                .then()
                .statusCode(200)
                .extract()
                .response();

        validateValidResponse(response);

        // Check if item per page query same with data.length
        List<Object> data = response.jsonPath().getList("data.data");

        Assert.assertEquals(data.size(), itemPerPage);
    }

    @Test(description = "TC-INT-HS-003 : User Cant See All History With Custom Invalid Item Per Page")
    public void userCantSeeAllHistoryWithCustomInvalidItemPerPage() {
        String endpoint = "/api/v1/history";
        String itemPerPage = "test";
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .queryParam("per_page_key", itemPerPage)
                .when()
                .get(endpoint)
                .then()
                .statusCode(400)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("per_page_key is not a valid page"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-HS-004 : User Cant See All History With Empty Data")
    public void userCantSeeAllHistoryWithEmptyData() {
        String endpoint = "/api/v1/history";
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint)
                .then()
                .statusCode(404)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("history not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-HS-005 : User Cant See All History With Invalid Auth")
    public void userCantSeeAllHistoryWithInvalidAuth() {
        String endpoint = "/api/v1/history";

        Response response = given()
                .contentType("application/json")
                .header("Accept", "application/json")
                .when()
                .get(endpoint)
                .then()
                .statusCode(401)
                .extract()
                .response();

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}