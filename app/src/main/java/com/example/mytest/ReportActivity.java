package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.model.Report;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Student;
import com.example.mytest.model.Test;
import com.example.mytest.repository.ReportRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class ReportActivity extends AppCompatActivity {
    private EditText commentEditText, topicEditText;
    private Button submitButton;
    private ReportRepository reportRepository;
    private Test selectedTest;
    private Student currentStudent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        commentEditText = findViewById(R.id.commentEditText);
        submitButton = findViewById(R.id.submitButton);
        topicEditText = findViewById(R.id.topicText);

        selectedTest = Select.getTest();
        currentStudent = Authentication.getStudent();

        reportRepository = new ReportRepository(FirebaseFirestore.getInstance());

        submitButton.setOnClickListener(view -> submitComplaint());
    }

    private void submitComplaint() {
        String commentText = commentEditText.getText().toString().trim();
        String topicText = topicEditText.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, опишите свою жалобу", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем название теста
        String testName = selectedTest != null ? selectedTest.getTitle() : "Неизвестный тест";

        // Создаем объект жалобы с testName
        Report complaint = new Report(
                UUID.randomUUID().toString(),
                selectedTest.getId(),
                currentStudent.getId(),
                commentText,
                System.currentTimeMillis(),
                topicText,
                testName
        );

        // Отправляем жалобу в репозиторий
        reportRepository.submitComplaint(complaint)
                .thenRun(() -> {
                    Toast.makeText(this, "Жалоба отправлена", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .exceptionally(e -> {
                    Toast.makeText(this, "Ошибка отправки жалобы", Toast.LENGTH_SHORT).show();
                    return null;
                });
    }


    public void onBackB(View view) {
        Intent intent = new Intent(this, TestDetailActivity.class);
        startActivity(intent);
        finish();
    }
}