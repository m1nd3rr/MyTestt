package com.example.mytest.repository;

import com.example.mytest.model.Report;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReportRepository {
    private final FirebaseFirestore db;
    private static final String COLLECTION_NAME = "reports";

    public ReportRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public CompletableFuture<Void> deleteReport(String reportId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        db.collection(COLLECTION_NAME)
                .document(reportId)
                .delete()
                .addOnSuccessListener(aVoid -> future.complete(null))
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }


    public CompletableFuture<Void> submitComplaint(Report report) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        db.collection(COLLECTION_NAME)
                .document(report.getId())
                .set(report)
                .addOnSuccessListener(aVoid -> future.complete(null))
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }

    public CompletableFuture<List<Report>> getAllReports() {
        CompletableFuture<List<Report>> future = new CompletableFuture<>();
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Report> reports = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> reports.add(doc.toObject(Report.class)));
                    future.complete(reports);
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }
    public Task<QuerySnapshot> getAllReportss() {
        return db.collection("reports")
                .get();
    }

    public CompletableFuture<List<Report>> getComplaintsByTestId(String testId) {
        CompletableFuture<List<Report>> future = new CompletableFuture<>();
        db.collection(COLLECTION_NAME)
                .whereEqualTo("testId", testId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Report> complaints = new ArrayList<>();
                    queryDocumentSnapshots.forEach(doc -> complaints.add(doc.toObject(Report.class)));
                    future.complete(complaints);
                })
                .addOnFailureListener(future::completeExceptionally);
        return future;
    }
}
