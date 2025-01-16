package com.example.mytest.model;

import com.google.firebase.Timestamp;

public class Result {
    private String id;
    private String userId;
    private String testId;
    private int correctAnswer;
    private Timestamp time;
    private int countAnswer;
    private String roomId;
    private Student student;
    private boolean  isCompleted;

    public Result(String id, String userId, String testId, int correctAnswer, Timestamp time, int countAnswer, String roomId, Student student, boolean isCompleted) {
        this.id = id;
        this.userId = userId;
        this.testId = testId;
        this.correctAnswer = correctAnswer;
        this.time = time;
        this.countAnswer = countAnswer;
        this.roomId = roomId;
        this.student = student;
        this.isCompleted = isCompleted;
    }

    public Result() {
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getCountAnswer() {
        return countAnswer;
    }

    public void setCountAnswer(int countAnswer) {
        this.countAnswer = countAnswer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
