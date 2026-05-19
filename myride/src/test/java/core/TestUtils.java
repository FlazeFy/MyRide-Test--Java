package core;

import io.restassured.response.Response;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class TestUtils {

    public static Object[][] getTestData(String filePath, String sheetName) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Sheet sheet = workbook.getSheet(sheetName);
        int rows = sheet.getPhysicalNumberOfRows();
        int cols = sheet.getRow(0).getPhysicalNumberOfCells();

        Object[][] data = new Object[rows - 1][cols];

        for (int i = 1; i < rows; i++) {
            Row row = sheet.getRow(i);
            for (int j = 0; j < cols; j++) {
                Cell cell = row.getCell(j);
                data[i - 1][j] = getCellValue(cell);
            }
        }

        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            fis.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return data;
    }

    private static Object getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> (cell.getNumericCellValue() % 1 == 0)
                    ? (int) cell.getNumericCellValue()
                    : cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            default -> "";
        };
    }


    // API Integration
    public static void validateColumn(Object data, List<String> fields, String dataType, boolean nullable) {
        // Convert object to list
        List<Map<String, Object>> dataArray;
        dataArray = data instanceof List ? (List<Map<String, Object>>) data : List.of((Map<String, Object>) data);

        // Loop item
        for (Map<String, Object> item : dataArray) {
            // Validate object
            Assert.assertNotNull(item);

            // Loop fields
            for (String field : fields) {
                // Validate field exists
                Assert.assertTrue(item.containsKey(field), "Missing field: " + field);

                Object value = item.get(field);

                // Nullable validation
                if (nullable && value == null) {
                    Assert.assertNull(value);
                    continue;
                }

                // Validate datatype
                switch (dataType) {
                    case "string":
                        Assert.assertTrue(value instanceof String, field + " is not String");
                        break;

                    case "number":
                        Assert.assertTrue(value instanceof Number, field + " is not Number");

                        // Validate integer or decimal
                        if (value instanceof Integer || value instanceof Long) {
                            Assert.assertEquals(((Number) value).doubleValue() % 1, 0.0);
                        } else {
                            Assert.assertNotEquals(((Number) value).doubleValue() % 1, 0.0);
                        }
                        break;

                    case "boolean":
                        Assert.assertTrue(value instanceof Boolean, field + " is not Boolean");
                        break;

                    case "bool_number":
                        Assert.assertTrue(value instanceof Number, field + " is not Number");
                        int numberValue = ((Number) value).intValue();

                        Assert.assertTrue(numberValue == 0 || numberValue == 1, field + " is not 0 or 1");
                        break;

                    default:
                        Assert.fail("Unsupported data type: " + dataType);
                }
            }
        }
    }

    public static void templatePagination(String url, int max, String token) {
        List<Integer> pages = new ArrayList<>();

        if (max <= 4) {
            // Hit all pages
            for (int i = 1; i <= max; i++) {
                pages.add(i);
            }
        } else {
            // Hit page 2, middle, last
            int middle = (int) Math.ceil((double) max / 2);

            pages = List.of(2, middle, max);
        }

        pages = pages.stream().distinct().toList();
        for (Integer page : pages) {
            Response response;
            if (token != null) {
                response = given()
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token)
                        .queryParam("page", page)
                        .when()
                        .get(url)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();

            } else {
                response = given()
                        .contentType("application/json")
                        .queryParam("page", page)
                        .when()
                        .get(url)
                        .then()
                        .statusCode(200)
                        .extract()
                        .response();
            }

            Assert.assertEquals(response.getStatusCode(), 200);
        }
    }

    public static void validateDateTime(Object data, List<Map<String, Object>> obj) {
        List<Map<String, Object>> dataArray;

        if (data instanceof List) {
            dataArray = (List<Map<String, Object>>) data;
        } else {
            dataArray = List.of((Map<String, Object>) data);
        }

        for (Map<String, Object> item : dataArray) {
            for (Map<String, Object> field : obj) {
                String colName = field.get("column_name").toString();
                String dateType = field.get("date_type").toString();
                boolean nullable = (boolean) field.get("nullable");

                Object value = item.get(colName);

                if (!nullable || value != null) {
                    Assert.assertNotNull(value);
                    String dateValue = value.toString();

                    if (dateType.equals("datetime")) {
                        Assert.assertTrue(dateValue.matches(
                                "^(?:\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?Z?|\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})$"),
                                colName + " must be a valid datetime (ISO 8601 or SQL format)"
                        );
                    } else if (dateType.equals("time")) {
                        Assert.assertTrue(dateValue.matches("^\\d{2}:\\d{2}:\\d{2}$"), colName + " must be a valid time");
                    } else if (dateType.equals("date")) {
                        Assert.assertTrue(dateValue.matches("^\\d{4}-\\d{2}-\\d{2}$"), colName + " must be a valid date");
                    }
                }
            }
        }
    }

    public static Response templateResponseGet(String endpoint, int expectedStatusCode, String endpointName, String token) {
        String contentType = "application/json";
        Response response;

        if (token != null) {
            response = given()
                .contentType(contentType)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint)
                .then()
                .statusCode(expectedStatusCode)
                .extract().response();
        } else {
            response = given()
                .contentType(contentType)
                .when()
                .get(endpoint)
                .then()
                .statusCode(expectedStatusCode)
                .extract().response();
        }

        System.out.println("==== GET : "+ endpointName +" ====");
        System.out.println("Status Code : " + response.getStatusCode());
        System.out.println("Response : ");
        System.out.println(response.asPrettyString());

        return response;
    }

    public static boolean isContainedInList(ArrayList<String> listMessage, String target) {
        for (int i = 0; i < listMessage.size(); i++) {
            if (listMessage.get(i).equals(target)) return true;
        }

        return false;
    }
}