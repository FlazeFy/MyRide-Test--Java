package core;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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

}