package com.billionaire.automation.utils;

import java.io.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

    private static final String PATH =
        System.getProperty("user.dir") +
        "/src/test/resources/testdata/LoginTestData.xlsx";

    // 🔥 READ MODE (DataProvider)
    public static Workbook getWorkbook() throws Exception {
        FileInputStream fis = new FileInputStream(PATH);
        Workbook wb = new XSSFWorkbook(fis);
        fis.close(); // IMPORTANT
        return wb;
    }

    // 🔥 WRITE MODE (Test)
    public static synchronized void saveWorkbook(Workbook wb) throws Exception {
        FileOutputStream fos = new FileOutputStream(PATH);
        wb.write(fos);
        fos.flush();
        fos.close();
        wb.close();
    }

    // 🔥 SAFE STRING READ
    public static String getCellValue(Row row, int index) {
        if (row == null) return "";
        Cell cell = row.getCell(index);
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }
}
