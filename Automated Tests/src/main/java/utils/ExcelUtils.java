package utils;

import java.io.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
    private static String filePath = "TestResults.xlsx"; 
    private static Workbook workbook;
    private static Sheet sheet;

    public static void openExcel(String sheetName) throws IOException {
        FileInputStream file = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(file);
        sheet = workbook.getSheet(sheetName);
    }

    public static void writeResult(int row, int col, String result) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filePath);
        sheet.getRow(row).createCell(col).setCellValue(result);
        workbook.write(fileOut);
        fileOut.close();
    }
}
