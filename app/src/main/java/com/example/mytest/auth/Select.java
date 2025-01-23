package com.example.mytest.auth;

import com.example.mytest.model.Question;
import com.example.mytest.model.Result;
import com.example.mytest.model.Test;

public class Select {
    private static Question question;
    public static Test test;
    public static Result result;

    public static Result getResult() {return result;
    }

    public static void setResult(Result result) {
        Select.result = result;
    }

    public static Question getQuestion() {
        return question;
    }

    public static void setQuestion(Question question) {
        Select.question = question;
    }

    public static Test getTest() {
        return test;
    }

    public static void setTest(Test test) {
        Select.test = test;
    }
}
