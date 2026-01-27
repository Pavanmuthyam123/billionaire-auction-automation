package com.billionaire.automation.tests;

import java.time.Duration;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.billionaire.automation.base.BaseTest;
import com.billionaire.automation.dataprovider.LoginDataProvider;
import com.billionaire.automation.utils.ExcelUtil;
import com.billionaire.automation.utils.ScreenshotUtil;

public class LoginTest extends BaseTest {

    // ================== REGRESSION (EXCEL DRIVEN) ==================
    @Test(
        dataProvider = "loginData",
        dataProviderClass = LoginDataProvider.class,
        groups = {"regression"}
    )
    public void loginFromExcel(
            int rowIndex,
            String email,
            String password,
            String expectedMessage,
            String testType) throws Exception {

        // 1️⃣ Open Login Page
        driver.get("https://billionaireauction.com/login");

        // 2️⃣ Enter Email
        if (!email.equalsIgnoreCase("NA")) {
            driver.findElement(By.id("email")).sendKeys(email);
        }

        // 3️⃣ Enter Password
        if (!password.equalsIgnoreCase("NA")) {
            driver.findElement(By.id("password")).sendKeys(password);
        }

        // 4️⃣ Click Login
        Thread.sleep(800);
        driver.findElement(By.xpath("//button[@type='submit']")).click();
        Thread.sleep(1200);

        // ================= CAPTURE UI MESSAGE =================
        String actualMessage = "";

        // 5️⃣ EMAIL HTML validation
        WebElement emailField = driver.findElement(By.id("email"));
        String emailValidation = emailField.getAttribute("validationMessage");

        if (emailValidation != null && !emailValidation.trim().isEmpty()) {

            actualMessage = emailValidation;

        } else {

            // 6️⃣ PASSWORD HTML validation
            WebElement passwordField = driver.findElement(By.id("password"));
            String passwordValidation = passwordField.getAttribute("validationMessage");

            if (passwordValidation != null && !passwordValidation.trim().isEmpty()) {

                actualMessage = passwordValidation;

            } else {

                // 7️⃣ BACKEND / TOAST MESSAGE
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement toast = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Invalid') or contains(text(),'Please')]")
                    )
                );
                actualMessage = toast.getText();
            }
        }

        // ================= REPORTING =================
        Reporter.log("UI_ERROR=" + actualMessage, true);
        Reporter.log("SCREENSHOT=" + ScreenshotUtil.capture(driver), true);

        // ================= WRITE BACK TO EXCEL =================
        Workbook wb = ExcelUtil.getWorkbook();
        Sheet sheet = wb.getSheet("LoginData");

        // Column mapping
        // H = Result (7)
        // I = ActualMessage (8)
        sheet.getRow(rowIndex).createCell(8).setCellValue(actualMessage);

        if (actualMessage.equalsIgnoreCase(expectedMessage)) {
            sheet.getRow(rowIndex).createCell(7).setCellValue("PASS");
        } else {
            sheet.getRow(rowIndex).createCell(7).setCellValue("FAIL");
        }

        ExcelUtil.saveWorkbook(wb);

        // ================= ASSERT =================
        Assert.assertEquals(actualMessage, expectedMessage);
    }

    // ================== SMOKE TEST ==================
    @Test(groups = {"smoke"})
    public void applicationShouldLaunch() {

        driver.get("https://billionaireauction.com/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));

        Assert.assertTrue(
            driver.getCurrentUrl().contains("billionaireauction.com"),
            "Login page did not load"
        );
    }
}
