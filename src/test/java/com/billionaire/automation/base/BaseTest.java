package com.billionaire.automation.base;

import java.io.File;
import java.lang.reflect.Method;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.billionaire.automation.reports.ExtentManager;
import com.billionaire.automation.utils.JiraUtil;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseTest {

    protected WebDriver driver;

    protected static ExtentReports extent = ExtentManager.getExtent();
    protected static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        // 🔥 Create Extent test per method
        ExtentTest extentTest = extent.createTest(method.getName());
        test.set(extentTest);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {

        if (result.getStatus() == ITestResult.FAILURE) {
            test.get().fail(result.getThrowable());
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.get().pass("Test passed");
        }

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {

        // 🔥 EXTENT FILE GENERATED HERE
        if (extent != null) {
            extent.flush();
        }

        String bugKey = System.getProperty("LAST_JIRA_KEY");

        if (bugKey == null) {
            System.out.println("ℹ️ No Jira created, skip extent attach");
            return;
        }

        String extentPath =
                System.getProperty("user.dir") + "/test-output/ExtentReport.html";

        File extentFile = new File(extentPath);

        if (!extentFile.exists()) {
            System.out.println("❌ Extent report not found");
            return;
        }

        JiraUtil.attachFileToIssue(bugKey, extentPath);
        System.out.println("📎 Extent report attached to " + bugKey);
    }
}
