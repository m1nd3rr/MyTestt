package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.StudentTestAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Student;
import com.example.mytest.model.Test;
import com.example.mytest.repository.TestRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TestDetailActivity extends BaseActivity {
    private TextView testName, questionCount, authorName;
    private RecyclerView otherTestsRecyclerView;
    private Button startTestButton, reportButton;
    private String testId;
    private   Test selectedTest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_tests);

        testName = findViewById(R.id.testName);
        authorName = findViewById(R.id.authorName);
        otherTestsRecyclerView = findViewById(R.id.horizontalRecyclerView);
        startTestButton = findViewById(R.id.startTest);
        reportButton = findViewById(R.id.reportTest);

        if(getIntent().getStringExtra("test_id")==null){
            selectedTest = Select.getTest();
            load();
        }else {
            TestRepository testRepository = new TestRepository(FirebaseFirestore.getInstance());
            testRepository.getById(getIntent().getStringExtra("test_id")).thenAccept(test -> {
               selectedTest = test;
               Select.setTest(test);
               load();
            });
        }
    }
    private void load(){
        Student studentName = Authentication.getStudent();

        testName.setText(selectedTest.getTitle());
        authorName.setText(studentName.getFirstName());

        loadOtherTests(selectedTest.getStudentId());

        startTestButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, PassingTestActivity.class);
            startActivity(intent);
            finish();
        });

        reportButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra("test_name", selectedTest.getTitle());
            intent.putExtra("test_id", selectedTest.getId());
            startActivity(intent);
            finish();
        });
    }
    private void loadOtherTests(String studentId) {
        TestRepository testRepository = new TestRepository(FirebaseFirestore.getInstance());

        testRepository.getAllTestByStudentId(studentId)
                .thenAccept(tests -> {
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
        intent.putExtra("testId", Select.getTest().getId());
        startActivity(intent);
        finish();
    }

    public void startPassing(View view) {
        Intent intent = new Intent(this, PassingTestActivity.class);
        startActivity(intent);
        finish();
    }

    public void onBackB(View view) {
        Intent intent = new Intent(this, StudentProfileActivity.class);
        startActivity(intent);
        finish();
    }
}

