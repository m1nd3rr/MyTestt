package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.StudentTestAdapter;
import com.example.mytest.adapter.TestAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Student;
import com.example.mytest.model.Test;
import com.example.mytest.repository.QuestionRepository;
import com.example.mytest.repository.TestRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TestDetailActivity extends AppCompatActivity {
    private TextView testName, questionCount, authorName;
    private RecyclerView otherTestsRecyclerView;
    private Button startTestButton, reportButton;
    private String testId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_tests);

        // Инициализация UI-элементов
        testName = findViewById(R.id.testName);
        authorName = findViewById(R.id.authorName);
        otherTestsRecyclerView = findViewById(R.id.horizontalRecyclerView);
        startTestButton = findViewById(R.id.startTest);
        reportButton = findViewById(R.id.reportTest);

        // Получение выбранного теста
        Test selectedTest = Select.getTest();
        Student studentName = Authentication.getStudent();

        // Установка данных теста
        testName.setText(selectedTest.getTitle());
        TestRepository testRepository = new TestRepository(FirebaseFirestore.getInstance());
        authorName.setText(studentName.getFirstName());

        // Загрузка других тестов автора
        loadOtherTests(selectedTest.getStudentId());

        // Обработка кнопки "Пройти тест"
        startTestButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, PassingTestActivity.class);
            startActivity(intent);
        });

        // Обработка кнопки "Пожаловаться"
        reportButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra("test_name", Select.getTest().getTitle());  // Название теста
            intent.putExtra("test_id", Select.getTest().getId());       // ID теста
            startActivity(intent);
            finish();
        });

    }

    private void loadOtherTests(String studentId) {
        TestRepository testRepository = new TestRepository(FirebaseFirestore.getInstance());

        // Получаем тесты, созданные данным автором
        testRepository.getAllTestByStudentId(studentId)
                .thenAccept(tests -> {
                    // Если тестов нет, передаем пустой список
                    if (tests == null) {
                        tests = new ArrayList<>();
                    }
                    StudentTestAdapter adapter = new StudentTestAdapter(tests, TestDetailActivity.this);
                    otherTestsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                    otherTestsRecyclerView.setAdapter(adapter);
                })
                .exceptionally(throwable -> {
                    throwable.printStackTrace();
                    return null;
                });
    }

    public void questionsList(View view) {
        Intent intent = new Intent(this, QuestionListActivity.class);
        intent.putExtra("testId", Select.getTest().getId()); // Передача ID теста
        startActivity(intent);
    }

    public void startPassing(View view) {
        Intent intent = new Intent(this,PassingTestActivity.class);
        startActivity(intent);
    }

    public void onBackB(View view) {
        Intent intent = new Intent(this, StudentProfileActivity.class);
        startActivity(intent);
        finish();
    }
}
