package com.example.mytest.model;

public class Report {
    private String id;
    private String testId;
    private String userId;
    private String complaintText;
    private long timestamp;
    private String nameReport;
    private String testName;

    public Report() {
    }

    public Report(String id, String testId, String userId, String complaintText, long timestamp, String nameReport, String testName) {
        this.id = id;
        this.testId = testId;
        this.userId = userId;
        this.complaintText = complaintText;
        this.timestamp = timestamp;
        this.nameReport = nameReport;
        this.testName = testName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComplaintText() {
        return complaintText;
    }

    public void setComplaintText(String complaintText) {
        this.complaintText = complaintText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getNameReport() {
        return nameReport;
    }

    public void setNameReport(String nameReport) {
        this.nameReport = nameReport;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}
