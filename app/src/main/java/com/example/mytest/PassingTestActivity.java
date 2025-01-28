package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;

public class PassingTestActivity extends AppCompatActivity {
    QuestionRepository questionRepository;
    List<Question> questionList = new ArrayList<>();
    List<Answer> answerList = new ArrayList<>();
    AnswerRepository answerRepository;
    PassingAdapter passingAdapter;
    RecyclerView recyclerView;
    EditText text;
    private TextView tvTimer;
    private TextView tvQuestionCount;
    private CountDownTimer countDownTimer;
    private Result resultAll;
    int totalQuestions = 0;
    private int currentQuestionIndex = 0;
    private boolean isTestInterrupted = false;
    int i = 0;
    int rightAnswer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_passing_test);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        totalQuestions = questionList.size(); // Получите список вопросов


        int durationMinutes = Select.test.getDuration() != null ? Select.test.getDuration() : 0;
        startTimer(durationMinutes * 60 * 1000);
        answerRepository = new AnswerRepository(FirebaseFirestore.getInstance());
        questionRepository = new QuestionRepository(FirebaseFirestore.getInstance());
        questionRepository.getAllQuestionByTestId(Select.getTest().getId())
                .thenAccept(list -> {
                    questionList.addAll(list);
                    ResultRepository resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
                    resultAll = new Result();
                    resultAll.setUserId(Authentication.student.getId());
                    resultAll.setTestId(Select.getTest().getId());
                    resultAll.setCountAnswer(questionList.size());
                    resultAll.setCompleted(false);
                    resultAll.setStudentAnswers(new HashMap<>());

                    resultAll=resultRepository.addResult(resultAll);
                    startPassing();
                    updateQuestionCount();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) return; // Если активность завершена, ничего не делаем
        isTestInterrupted = true; // Устанавливаем флаг, что тест прерван
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Остановка таймера
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTestInterrupted) {
            showTestInterruptedDialog(); // Показываем ошибку
        }
    }

    private void showTestInterruptedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage("Тест был прерван. Вы не можете продолжить.")
                .setCancelable(false)
                .setPositiveButton("На главный экран", (dialog, which) -> {
                    Intent intent = new Intent(this, StudentProfileActivity.class);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private void startTimer(long durationMillis) {
        if (durationMillis <= 0) {
            tvTimer.setText("∞");
            return; // Не запускаем таймер
        }

        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                Toast.makeText(PassingTestActivity.this, "Время на прохождение теста вышло!", Toast.LENGTH_SHORT).show();
                endTest();
            }
        }.start();
    }
    private void updateQuestionCount() {
        if (questionList != null && !questionList.isEmpty()) {
            String questionCountText = (i + 1) + "/" + questionList.size();
            tvQuestionCount.setText(questionCountText);
        } else {
            tvQuestionCount.setText("0/0"); // На случай, если список пуст
        }
    }


    private void endTest() {
        // Остановка таймера
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        int totalQuestions = questionList.size();
        int wrongAnswers = totalQuestions - rightAnswer;
        // Подсчёт результатов

// Отображение результатов в диалоговом окне
        showResultsDialog(rightAnswer, wrongAnswers);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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

        // Проверка типа вопроса
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

        // Увеличение счётчика правильных ответов
        if (result) {
            rightAnswer++;
        }

        // Обновление результатов студента
        resultAll.getStudentAnswers().put(questionList.get(i).getId(), result);
        ResultRepository resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
        resultRepository.updateResult(resultAll);

        // Переход к следующему вопросу или завершение теста
        if (i < questionList.size() - 1) {
            i++;
            updateQuestionCount(); // Обновление отображения счётчика вопросов
            startPassing();
        } else {
            // Завершение теста
            totalQuestions = questionList.size();
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
        resultAll.setCorrectAnswer(rightAnswer);
        resultAll.setCompleted(true);
        resultRepository.updateResult(resultAll);
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