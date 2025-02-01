package com.example.mytest.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Test implements Serializable {
    private String teacherId;
    private String title;
    //private transient Timestamp timestamp;я
    private String studentId;
    private Integer duration = null;

    private String id;


    public Test() {
    }

    public Test(String teacherId, String title, String studentId, Integer duration, String id) {
        this.teacherId = teacherId;
        this.title = title;
        this.studentId = studentId;
        this.duration = duration;
        this.id = id;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public Timestamp getTimestamp() {return timestamp;}
//
//    public void setTimestamp(Timestamp timestamp) {
//        this.timestamp = timestamp;
//    }


    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

}
