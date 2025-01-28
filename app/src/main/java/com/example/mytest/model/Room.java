package com.example.mytest.model;

public class Room {
    private String id;
    private String testId;
    private String roomNumber;
    private String teacherId;
    private String testName;

    public Room(String id, String testId, String roomNumber, String teacherId, String testName) {
        this.id = id;
        this.testId = testId;
        this.roomNumber = roomNumber;
        this.teacherId = teacherId;
        this.testName = testName;
    }

    public Room() {
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getTeacherId() {
        return teacherId;  // Геттер для teacherId
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;  // Сеттер для teacherId
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }
}