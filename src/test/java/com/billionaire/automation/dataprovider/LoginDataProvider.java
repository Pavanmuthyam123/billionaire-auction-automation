package com.billionaire.automation.dataprovider;

import org.apache.poi.ss.usermodel.*;
import org.testng.annotations.DataProvider;

import com.billionaire.automation.utils.ExcelUtil;

import java.util.ArrayList;
import java.util.List;

public class LoginDataProvider {

    @DataProvider(name = "loginData")
    public static Object[][] getLoginData() throws Exception {

        Workbook wb = ExcelUtil.getWorkbook();
        Sheet sheet = wb.getSheet("LoginData");

        List<Object[]> data = new ArrayList<>();

        int lastRow = sheet.getLastRowNum();

        for (int i = 1; i <= lastRow; i++) {

            Row row = sheet.getRow(i);
            if (row == null) continue;

            String runFlag  = ExcelUtil.getCellValue(row, 6); // Run
            String testType = ExcelUtil.getCellValue(row, 5); // TestType

            if (!"Y".equalsIgnoreCase(runFlag)) continue;
            if (!"regression".equalsIgnoreCase(testType)) continue;

            data.add(new Object[] {
                i,
                ExcelUtil.getCellValue(row, 2), // Email
                ExcelUtil.getCellValue(row, 3), // Password
                ExcelUtil.getCellValue(row, 4), // ExpectedMessage
                testType
            });
        }

        wb.close(); // 🔥 MUST

        return data.toArray(new Object[0][]);
    }
}
