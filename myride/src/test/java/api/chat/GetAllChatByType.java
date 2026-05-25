package api.chat;

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

public class GetAllChatByType extends BaseApiTest {
    public void validateValidResponse(Response response) {
        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("chat fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof Map);

        Map<String, Object> data = jsonPath.getMap("data");

        // Get List Key / Column
        List<String> stringFields = List.of("question", "answer", "created_at");
        List<String> intBoolFields = List.of("is_success");

        // Validate Column
        TestUtils.validateColumn(data.get("data"), stringFields, "string", false);
        TestUtils.validateColumn(data.get("data"), intBoolFields, "bool_number", false);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "created_at", "date_type", "datetime", "nullable", false)
        );

        TestUtils.validateDateTime(data.get("data"), columnDateTime);
    }

    @Test(description = "TC-INT-CT-001 : User Can See All Chat By Type With Valid Data")
    public void userCanSeeAllChatByTypeWithValidData() {
        String chatType = "ai";

        String endpoint = "/api/v1/chat/" + chatType;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Chat By Type", token);

        validateValidResponse(response);

        // Check if all page accessible
        int lastPage = response.jsonPath().getInt("data.last_page");

        TestUtils.templatePagination(endpoint, lastPage, token);
    }

    @Test(description = "TC-INT-CT-002 : User Can See All Chat By Type With Custom Item Per Page")
    public void userCanSeeAllChatByTypeWithCustomItemPerPage() {
        String chatType = "ai";
        int itemPerPage = 2;

        String endpoint = "/api/v1/chat/" + chatType + "?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Chat By Type", token);

        validateValidResponse(response);

        // Check if item per page query same with data.length
        List<Object> data = response.jsonPath().getList("data.data");

        Assert.assertEquals(data.size(), itemPerPage);
    }

    @Test(description = "TC-INT-CT-003 : User Cant See All Chat By Type With Custom Invalid Item Per Page")
    public void userCantSeeAllChatByTypeWithCustomInvalidItemPerPage() {
        String chatType = "ai";
        String itemPerPage = "test";

        String endpoint = "/api/v1/chat/" + chatType + "?per_page_key=" + itemPerPage;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "All Chat By Type", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("per_page_key is not a valid page"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-CT-004 : User Cant See All Chat By Type With Empty Data")
    public void userCantSeeAllChatByTypeWithEmptyData() {
        String chatType = "ai";

        String endpoint = "/api/v1/chat/" + chatType;
        String token = AuthUtils.integrationLoginAPI("testerempty", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Chat By Type", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("chat not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-CT-005 : User Cant See All Chat With Invalid Auth")
    public void userCantSeeAllChatWithInvalidAuth() {
        String chatType = "ai";

        String endpoint = "/api/v1/chat/" + chatType;

        Response response = templateResponseGet(endpoint, 401, "All Chat By Type", null);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("you need to include the authorization token from login"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }

    @Test(description = "TC-INT-CT-006 : User Cant See All Chat By Type With Invalid Type")
    public void userCantSeeAllChatByTypeWithInvalidType() {
        String chatType = "telegram";

        String endpoint = "/api/v1/chat/" + chatType;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 400, "All Chat By Type", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("Chat type : " + chatType + " is not available"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}