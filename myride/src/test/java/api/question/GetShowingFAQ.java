package api.question;

import core.BaseApiTest;
import core.TestUtils;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static core.TestUtils.templateResponseGet;

public class GetShowingFAQ extends BaseApiTest {
    @Test(description = "TC-INT-QS-001 : User Can See FAQ With Valid Data")
    public void userCanSeeFAQWithValidData() {
        String endpoint = "/api/v1/question/faq";

        Response response = templateResponseGet(endpoint, 200, "Showing FAQ", null);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("faq fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof List);
        List<Map<String, Object>> data = jsonPath.getList("data");

        // Get List Key / Column
        List<String> stringFields = List.of("faq_question","faq_answer");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
    }

    @Test(description = "TC-INT-QS-002 : User Cant See FAQ With Empty Data")
    public void userCantSeeFAQWithEmptyData() {
        RestAssured.baseURI = devEmptyCaseTestURL;

        String endpoint = "/api/v1/question/faq";

        Response response = templateResponseGet(endpoint, 404, "Showing FAQ", null);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("faq not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));

        RestAssured.baseURI = devBaseURL;
    }
}
