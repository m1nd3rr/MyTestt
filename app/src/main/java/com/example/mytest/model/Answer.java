package com.example.mytest.model;

public class Answer {
    private String id;
    private String content;
    private boolean correct;
    private String questionId;
    private int sortNumber;
    private String text;


    public Answer() {
    }

    public Answer(String id, String content, boolean correct, String questionId, int sortNumber, String text) {
        this.id = id;
        this.content = content;
        this.correct = correct;
        this.questionId = questionId;
        this.sortNumber = sortNumber;
        this.text = text;

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(int sortNumber) {
        this.sortNumber = sortNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
