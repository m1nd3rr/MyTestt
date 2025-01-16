package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.PassingAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Answer;
import com.example.mytest.model.Question;
import com.example.mytest.model.Result;
import com.example.mytest.repository.AnswerRepository;
import com.example.mytest.repository.QuestionRepository;
import com.example.mytest.repository.ResultRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PassingTestActivity extends AppCompatActivity {
    QuestionRepository questionRepository;
    List<Question> questionList = new ArrayList<>();
    List<Answer> answerList = new ArrayList<>();
    AnswerRepository answerRepository;
    PassingAdapter passingAdapter;
    RecyclerView recyclerView;
    EditText text;

    int i = 0;
    int rightAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passing_test);
        answerRepository = new AnswerRepository(FirebaseFirestore.getInstance());
        questionRepository = new QuestionRepository(FirebaseFirestore.getInstance());
        questionRepository.getAllQuestionByTestId(Select.getTest().getId())
                .thenAccept(list -> {
                    questionList.addAll(list);
                    startPassing();
                });
    }

    private void startPassing() {
        recyclerView = findViewById(R.id.rvPassing);
        text = findViewById(R.id.text_question);
        TextView textView = findViewById(R.id.textPassing);
        textView.setText(questionList.get(i).getTitle());

        answerRepository.getAllAnswerById(questionList.get(i).getId())
                .thenAccept(list -> {
                    answerList.clear();
                    answerList.addAll(list);
                    switch (questionList.get(i).getType()) {
                        case "text":
                            textQuestion();
                            break;
                        default:
                            anyOtherQuestion();
                    }
                });
    }

    private void textQuestion() {
        text.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void anyOtherQuestion() {
        text.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        passingAdapter = new PassingAdapter(answerList,this,questionList.get(i));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(passingAdapter);
    }

    public void nextQuestion(View view) {
        boolean result = false;
        if (!questionList.get(i).getType().equals("text")) {
            result = passingAdapter.getPassingResult();
        } else {
            for (Answer answer : answerList) {
                if (text.getText().toString().equals(answer.getContent())) {
                    result = true;
                    break;
                }
            }
        }

        if (result) {
            rightAnswer++;
        }

        if (i < questionList.size() - 1) {
            i++;
            startPassing();
        } else {
            int totalQuestions = questionList.size();
            int wrongAnswers = totalQuestions - rightAnswer;
            showResultsDialog(rightAnswer, wrongAnswers);

            onTestCompleted();
        }
    }

    private void showResultsDialog(int correctAnswers, int wrongAnswers) {
        new AlertDialog.Builder(this)
                .setTitle("Результаты теста")
                .setMessage("Количество верных ответов: " + correctAnswers + "\nКоличество неверных ответов: " + wrongAnswers)
                .setPositiveButton("ОК", (dialog, which) -> {
                    Intent intent = new Intent(this, StudentProfileActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
    private void onTestCompleted() {
        ResultRepository resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
        Result resultAll = new Result();
        resultAll.setUserId(Authentication.student.getId());
        resultAll.setTestId(Select.getTest().getId());
        resultAll.setCountAnswer(questionList.size());
        resultAll.setCorrectAnswer(rightAnswer);
        resultAll.setCompleted(true); // Установите в true, если тест завершен

        resultRepository.addResult(resultAll);
    }
    public void backButton(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение выхода")
                .setMessage("Вы действительно хотите выйти?")
                .setPositiveButton("Выйти", (dialog, which) -> {
                    Intent intent = new Intent(this, StudentProfileActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Остаться", (dialog, which) -> dialog.dismiss())
                .show();
    }


}