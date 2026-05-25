package api.dictionary;

import core.AuthUtils;
import core.BaseApiTest;
import core.TestUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import static core.TestUtils.templateResponseGet;

public class GetAllDictionaryByType extends BaseApiTest {
    @Test(description = "TC-INT-DC-001 : User Can See All Dictionary Valid Data By Category")
    public void userCanSeeAllDictionaryValidDataByCategory() {
        String context = "inventory_storage";

        String endpoint = "/api/v1/dictionary/type/" + context;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 200, "All Dictionary By Type", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("dictionary fetched"));

        // Get Item Holder
        Assert.assertNotNull(jsonPath.get("data"));
        Assert.assertTrue(jsonPath.get("data") instanceof List);

        List<Object> data = jsonPath.getList("data");

        // Get List Key / Column
        List<String> stringFields = List.of("dictionary_name", "dictionary_type");

        // Validate Column
        TestUtils.validateColumn(data, stringFields, "string", false);
    }

    @Test(description = "TC-INT-DC-002 : User Cant See Total Trip By Context With Invalid Context")
    public void userCantSeeTotalTripByContextWithInvalidContext() {
        String context = "inventory_storage_old";

        String endpoint = "/api/v1/dictionary/type/" + context;
        String token = AuthUtils.integrationLoginAPI("flazen.edu", "nopass123");

        Response response = templateResponseGet(endpoint, 404, "All Dictionary By Type", token);

        JsonPath jsonPath = response.jsonPath();

        // Validate message
        Assert.assertTrue(jsonPath.getString("message").contains("dictionary not found"));

        // Get Item Holder
        Assert.assertNull(jsonPath.get("data"));
    }
}