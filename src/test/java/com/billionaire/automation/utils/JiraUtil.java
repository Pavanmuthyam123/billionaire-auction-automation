package com.billionaire.automation.utils;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import io.restassured.response.Response;

public class JiraUtil {

    private static String getAuth() {
        String email = ConfigReader.get("jira.email");
        String token = ConfigReader.get("jira.token");
        return Base64.getEncoder()
                .encodeToString((email + ":" + token).getBytes());
    }

    // Atlassian Document Format
    private static JSONObject adfText(String text) {
        return new JSONObject()
                .put("type", "doc")
                .put("version", 1)
                .put("content", new JSONArray()
                        .put(new JSONObject()
                                .put("type", "paragraph")
                                .put("content", new JSONArray()
                                        .put(new JSONObject()
                                                .put("type", "text")
                                                .put("text", text)
                                        )
                                )
                        )
                );
    }

    public static String createBug(
            String summary,
            String steps,
            String expected,
            String actual,
            String severityOptionId,
            String priority
    ) {

        String jiraUrl   = ConfigReader.get("jira.url");
        String projectId = ConfigReader.get("jira.projectId");

        JSONObject fields = new JSONObject()
                .put("project", new JSONObject().put("id", projectId))
                .put("summary", summary)
                .put("issuetype", new JSONObject().put("name", "Bug"))
                .put("priority", new JSONObject().put("name", priority))
                .put("customfield_10073", adfText(steps))
                .put("customfield_10074", adfText(expected))
                .put("customfield_10075", adfText(actual))
                .put("customfield_10076",
                        new JSONObject().put("id", severityOptionId));

        JSONObject payload = new JSONObject().put("fields", fields);

        Response response =
                given()
                        .baseUri(jiraUrl)
                        .basePath("/rest/api/3/issue")
                        .header("Authorization", "Basic " + getAuth())
                        .contentType("application/json")
                        .body(payload.toString())
                .when()
                        .post()
                .then()
                        .statusCode(201)
                        .extract()
                        .response();

        String bugKey = response.jsonPath().getString("key");
        System.out.println("✅ Jira Bug Created: " + bugKey);

        return bugKey;
    }

    public static void attachFileToIssue(String issueKey, String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("❌ File not found: " + filePath);
            return;
        }

        given()
                .baseUri(ConfigReader.get("jira.url"))
                .basePath("/rest/api/3/issue/" + issueKey + "/attachments")
                .header("Authorization", "Basic " + getAuth())
                .header("X-Atlassian-Token", "no-check")
                .multiPart("file", file)
        .when()
                .post()
        .then()
                .statusCode(200);

        System.out.println("📎 File attached to " + issueKey);
    }
}
