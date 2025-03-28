package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.ReportAdapter;
import com.example.mytest.model.Report;
import com.example.mytest.repository.ReportRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllReportsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private ReportRepository reportRepository;
    private EditText searchInput;
    private List<Report> originalReports;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reports);

        recyclerView = findViewById(R.id.recyclerViewComplaints);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchInput = findViewById(R.id.search_input);
        reportRepository = new ReportRepository(FirebaseFirestore.getInstance());

        loadComplaints();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterReports(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void loadComplaints() {
        reportRepository.getAllReports().thenAccept(reports -> {
            originalReports = reports;
            reportAdapter = new ReportAdapter(reports, this::openReportDetail);
            recyclerView.setAdapter(reportAdapter);
        }).exceptionally(e -> {
            runOnUiThread(() -> Toast.makeText(this, "Ошибка загрузки жалоб", Toast.LENGTH_SHORT).show());
            return null;
        });
    }

    private void filterReports(String query) {
        List<Report> filteredList = new ArrayList<>();
        for (Report report : originalReports) {
            // Ищем по названию теста (или другому полю, например, тексту жалобы)
            if (report.getNameReport().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(report);
            }
        }
        reportAdapter.updateList(filteredList); // Обновляем список в адаптере
    }

    private void openReportDetail(Report report) {
        Intent intent = new Intent(this, ReportDetailActivity.class);
        intent.putExtra("test_name", report.getTestName());
        intent.putExtra("complaint_text", report.getComplaintText());
        intent.putExtra("timestamp", report.getTimestamp());
        intent.putExtra("reportId", report.getId());
        startActivity(intent);
        finish();
    }

    public void onBackk(View view) {
        Intent intent = new Intent(this, AdminProfile.class);
        startActivity(intent);
        finish();
    }
}