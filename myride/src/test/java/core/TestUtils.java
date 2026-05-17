package core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.testng.Assert;

import java.util.List;
import java.util.Map;

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
}