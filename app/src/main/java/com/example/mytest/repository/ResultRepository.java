package com.example.mytest.repository;

import android.util.Log;
import android.util.Pair;

import com.example.mytest.model.Result;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ResultRepository {
    private final CollectionReference resultCollection;
    public ResultRepository(FirebaseFirestore db) {
        resultCollection = db.collection("result");
    }

    public Result addResult(Result result) {
        String resultId = resultCollection.document().getId();
        result.setId(resultId);
        result.setTime(Timestamp.now());
        result.setCompleted(true);
        resultCollection.document(resultId).set(result);
        return result;
    }
    public CompletableFuture<Result> updateResult(Result result) {
        CompletableFuture<Result> future = new CompletableFuture<>();

        // Ensure the result has a valid ID
        if (result.getId() == null || result.getId().isEmpty()) {
            future.completeExceptionally(new IllegalArgumentException("Result ID cannot be null or empty"));
            return future;
        }

        // Update the result document in Firestore
        resultCollection.document(result.getId()).set(result).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(result);
            } else {
                future.completeExceptionally(task.getException());
            }
        });

        return future;
    }

    public CompletableFuture<List<Result>> getAllResultByStudentId(String id) {
        CompletableFuture<List<Result>> future = new CompletableFuture<>();
        List<Result> resultList = new ArrayList<>();

        resultCollection.whereEqualTo("userId", id).get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                Result result = document.toObject(Result.class);
                resultList.add(result);
            }
            future.complete(resultList);
        });

        return future;
    }

    public CompletableFuture<Pair<Integer, Integer>> getResultByStudentId(String id) {
        CompletableFuture<Pair<Integer, Integer>> future = new CompletableFuture<>();

        resultCollection.whereEqualTo("userId", id).get().addOnCompleteListener(task -> {
            int correct = 0, count = 0;

            for (QueryDocumentSnapshot document : task.getResult()) {
                Result result = document.toObject(Result.class);
                correct += result.getCorrectAnswer();
                count += result.getCountAnswer();
            }
            Pair<Integer, Integer> res = new Pair<>(correct, count);
            future.complete(res);
        });

        return future;
    }

    public ListenerRegistration getResultsByTestId(String testId, EventListener<QuerySnapshot> listener) {
        return resultCollection.whereEqualTo("testId", testId)
                .addSnapshotListener(listener);
    }
    public CompletableFuture<List<Result>> getResultsByRoomId(String roomId) {
        CompletableFuture<List<Result>> future = new CompletableFuture<>();
        List<Result> resultList = new ArrayList<>();

        resultCollection.whereEqualTo("roomId", roomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Result result = document.toObject(Result.class);
                    resultList.add(result);
                }
                future.complete(resultList);
            } else {
                future.completeExceptionally(task.getException());
            }
        });

        return future;
    }



}