package com.billionaire.automation.listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.billionaire.automation.utils.JiraUtil;

public class JiraFailureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {

        // 🚫 SMOKE → NO JIRA
        if (!isRegressionTest(result)) {
            System.out.println("🚫 Smoke failure – Jira skipped");
            return;
        }

        System.out.println("🔥 Regression failure – Jira creation started");

        String uiError = null;
        String screenshot = null;

        // 🔍 Read data sent from test
        for (String log : Reporter.getOutput(result)) {
            if (log.startsWith("UI_ERROR=")) {
                uiError = log.replace("UI_ERROR=", "").trim();
            }
            if (log.startsWith("SCREENSHOT=")) {
                screenshot = log.replace("SCREENSHOT=", "").trim();
            }
        }

        // 🔒 Safety: UI must be reached
        if (uiError == null || uiError.isEmpty()) {
            System.out.println("❌ UI error not captured – Jira blocked");
            return;
        }

        // ✅ CORRECT steps as per TEST CASE
        String steps =
                "1. Open login page\n" +
                "2. Enter valid email\n" +
                "3. Enter invalid password\n" +
                "4. Click Login";

        // ✅ CORRECT expected result
        String expected =
                "Password specific validation message should be displayed";

        // 🔥 Create Jira
        String bugKey = JiraUtil.createBug(
                result.getName(),
                steps,
                expected,
                uiError,
                "10022",   // Major
                "High"
        );

        System.setProperty("LAST_JIRA_KEY", bugKey);
        // 📸 Attach screenshot (toast visible)
        if (screenshot != null && !screenshot.isEmpty()) {
            JiraUtil.attachFileToIssue(bugKey, screenshot);
        }

        System.out.println("✅ Jira created successfully: " + bugKey);
    }

    // ✅ Regression group check
    private boolean isRegressionTest(ITestResult result) {
        for (String group : result.getMethod().getGroups()) {
            if ("regression".equalsIgnoreCase(group)) {
                return true;
            }
        }
        return false;
    }
}
