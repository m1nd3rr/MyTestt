package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.repository.ReportRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReportDetailActivity extends BaseActivity {
    private TextView complaintTitle, testName, complaintText;
    private Button sendMessageAuthor, completeButton;
    private String studentId; // Студент, создавший тест
    private  String reportId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_report_activity);

        complaintTitle = findViewById(R.id.complaint_title);
        testName = findViewById(R.id.test_title);
        complaintText = findViewById(R.id.complaint_text);
        completeButton = findViewById(R.id.complete);

        String testNameStr = getIntent().getStringExtra("test_name");
        String complaintTextStr = getIntent().getStringExtra("complaint_text");
        reportId = getIntent().getStringExtra("reportId");
        studentId = getIntent().getStringExtra("student_id");

        complaintTitle.setText("Жалоба на тест: " + testNameStr);
        testName.setText(testNameStr);
        complaintText.setText(complaintTextStr);

        completeButton.setOnClickListener(v -> {
            clearActivityAndRedirect();
        });
    }

    private void clearActivityAndRedirect() {
        ReportRepository reportRepository = new ReportRepository(FirebaseFirestore.getInstance());
        reportRepository.deleteReport(reportId);
        Intent intent = new Intent(ReportDetailActivity.this, AdminProfile.class);
        startActivity(intent);
        finish();
    }

    public void onBackReport(View view) {
        Intent intent = new Intent(this, AllReportsActivity.class);
        startActivity(intent);
        finish();
    }
}
