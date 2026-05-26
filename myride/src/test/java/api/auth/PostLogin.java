package api.auth;

import core.BaseApiTest;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Map;
import static core.TestUtils.templateResponsePost;

public class PostLogin extends BaseApiTest {
    @Test(description = "TC-INT-AU-001 : User Can Login With Valid Data")
    public void userCanLoginWithValidData() {
        String endpoint = "/api/v1/login";

        Map<String, Object> payload = Map.of(
                "username", "flazen.edu",
                "password", "nopass123"
        );
        Response response = templateResponsePost(endpoint, 200, "Login", payload, null);
        JsonPath jsonPath = response.jsonPath();

        // Validate Object Body
        Assert.assertNotNull(jsonPath.get("token"));
        Assert.assertNotNull(jsonPath.get("role"));
        Assert.assertNotNull(jsonPath.get("message"));
        Assert.assertNotNull(jsonPath.get("status"));

        Assert.assertTrue(jsonPath.get("token") instanceof String);
        Assert.assertEquals(jsonPath.getString("status"), "success");
        Assert.assertTrue(jsonPath.get("role") instanceof Number);
        Assert.assertTrue(jsonPath.get("message") instanceof Map);

        // Get list key / column
        Map<String, Object> dataObj = jsonPath.getMap("message");

        List<String> stringFields = List.of("id", "username", "email", "created_at");
        List<String> stringNullableFields = List.of("updated_at", "telegram_user_id");
        List<String> intFields = List.of("telegram_is_valid");

        // Validate column
        TestUtils.validateColumn(dataObj, stringFields, "string", false);
        TestUtils.validateColumn(dataObj, stringNullableFields, "string", true);
        TestUtils.validateColumn(dataObj, intFields, "number", true);

        // Validate character length
        List<Map<String, Object>> columnPropsClothes = List.of(
                Map.of("column_name", "id", "data_type", "string", "max", 36, "min", 36, "nullable", false),
                Map.of("column_name", "username", "data_type", "string", "max", 36, "min", 6, "nullable", false),
                Map.of("column_name", "email", "data_type", "string", "max", 144, "min", 10, "nullable", false),
                Map.of("column_name", "telegram_user_id", "data_type", "string", "max", 36, "min", 10, "nullable", true)
        );

        TestUtils.validateMaxMin(dataObj, columnPropsClothes);

        // Validate datetime
        List<Map<String, Object>> columnDateTime = List.of(
                Map.of("column_name", "created_at", "date_type", "datetime", "nullable", false),
                Map.of("column_name", "updated_at", "date_type", "datetime", "nullable", true)
        );

        TestUtils.validateDateTime(dataObj, columnDateTime);
    }

    @Test(description = "TC-INT-AU-002 : User Cant Login With Wrong Password")
    public void userCantLoginWithWrongPassword() {
        String endpoint = "/api/v1/login";

        Map<String, Object> payload = Map.of(
                "username", "flazefy",
                "password", "nopass1234"
        );
        Response response = templateResponsePost(endpoint, 401, "Login", payload, null);
        JsonPath jsonPath = response.jsonPath();

        // Validate Object Body
        Assert.assertNotNull(jsonPath.get("message"));
        Assert.assertNotNull(jsonPath.get("status"));

        Assert.assertEquals(jsonPath.getString("status"), "failed");
        Assert.assertEquals(jsonPath.getString("message"), "wrong password or username");
    }

    @Test(description = "TC-INT-AU-003 : User Cant Login With Invalid Char Length Username")
    public void userCantLoginWithInvalidCharLengthUsername() {
        String endpoint = "/api/v1/login";

        Map<String, Object> payload = Map.of(
                "username", "fla",
                "password", "nopass1234"
        );
        Response response = templateResponsePost(endpoint, 400, "Login", payload, null);
        JsonPath jsonPath = response.jsonPath();

        // Validate Object Body
        Assert.assertNotNull(jsonPath.get("message"));
        Assert.assertNotNull(jsonPath.get("status"));

        Assert.assertEquals(jsonPath.getString("status"), "failed");
        Assert.assertTrue(jsonPath.get("message") instanceof Map);

        Assert.assertEquals(jsonPath.getString("message.username[0]"), "The username field must be at least 6 characters.");
    }

    @Test(description = "TC-INT-AU-004 : User Cant Login With Empty Username")
    public void userCantLoginWithEmptyUsername() {
        String endpoint = "/api/v1/login";

        Map<String, Object> payload = Map.of(
                "username", "",
                "password", "nopass1234"
        );
        Response response = templateResponsePost(endpoint, 400, "Login", payload, null);
        JsonPath jsonPath = response.jsonPath();

        // Validate Object Body
        Assert.assertNotNull(jsonPath.get("message"));
        Assert.assertNotNull(jsonPath.get("status"));

        Assert.assertEquals(jsonPath.getString("status"), "failed");
        Assert.assertTrue(jsonPath.get("message") instanceof Map);

        Assert.assertEquals(jsonPath.getString("message.username[0]"), "The username field is required.");
    }
}