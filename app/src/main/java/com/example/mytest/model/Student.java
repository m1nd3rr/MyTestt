package com.example.mytest.model;

public class Student {
    private String id;
    private String firstName;
    private String lastName;
    private String groupNumber;
    private String password;
    private String email;
    private String photo;

    public Student(String id, String firstName, String lastName, String groupNumber, String password, String email, String photo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupNumber = groupNumber;
        this.password = password;
        this.email = email;
        this.photo = photo;
    }
    public Student(String firstName, String lastName, String groupNumber, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupNumber = groupNumber;
        this.password = password;
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Student() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}