package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.ReportAdapter;
import com.example.mytest.model.Report;
import com.example.mytest.repository.ReportRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AllReportsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private ReportRepository reportRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);

        recyclerView = findViewById(R.id.recyclerViewComplaints);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reportRepository = new ReportRepository(FirebaseFirestore.getInstance());

        // Загружаем жалобы
        loadComplaints();
    }

    private void loadComplaints() {
        reportRepository.getAllReports().thenAccept(reports -> {
            reportAdapter = new ReportAdapter(reports, this::openReportDetail);
            recyclerView.setAdapter(reportAdapter);
        }).exceptionally(e -> {
            runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки жалоб", Toast.LENGTH_SHORT).show());
            return null;
        });
    }

    private void openReportDetail(Report report) {
        Intent intent = new Intent(this, ReportDetailActivity.class);
        intent.putExtra("test_name", report.getTestName());
        intent.putExtra("complaint_text", report.getComplaintText());
        intent.putExtra("timestamp", report.getTimestamp());
        intent.putExtra("reportId", report.getId());
        startActivity(intent);
    }

    public void onBackk(View view) {
        Intent intent = new Intent(this,AdminProfile.class);
        startActivity(intent);
        finish();
    }
}
