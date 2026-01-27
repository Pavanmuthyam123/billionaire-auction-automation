package com.billionaire.automation.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getExtent() {

        if (extent == null) {

            String reportPath =
                System.getProperty("user.dir") + "/test-output/ExtentReport.html";

            ExtentSparkReporter spark =
                new ExtentSparkReporter(reportPath);

            spark.config().setReportName("Billionaire Auction Automation");
            spark.config().setDocumentTitle("Regression Test Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Project", "Billionaire Auction");
            extent.setSystemInfo("Tester", "Pavan");
            extent.setSystemInfo("Environment", "QA");
        }

        return extent;
    }
}
