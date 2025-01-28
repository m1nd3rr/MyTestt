package com.example.mytest.repository;

import android.util.Pair;

import com.example.mytest.model.Result;
import com.example.mytest.model.Room;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
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
    private final RoomRepository roomRepository;
    public ResultRepository(FirebaseFirestore db) {
        resultCollection = db.collection("result");
        roomRepository = new RoomRepository(db);
    }

    public Result addResult(Result result) {
        String resultId = resultCollection.document().getId();
        result.setId(resultId);
        result.setTime(Timestamp.now());
        result.setCompleted(true);

        // Получаем roomId по testId
        roomRepository.getAllRoomsByTestId(result.getTestId()).thenAccept(rooms -> {
            if (!rooms.isEmpty()) {
                // Предполагаем, что тест привязан хотя бы к одной комнате
                Room room = rooms.get(0); // Если есть несколько комнат, выберите нужную
                result.setRoomId(room.getId()); // Устанавливаем roomId в результат
            }

            // Сохраняем результат с roomId
            resultCollection.document(resultId).set(result);
        });

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

    public ListenerRegistration getResultsByTestIdAndRoomId(String testId, String roomId, EventListener<QuerySnapshot> listener) {
        return resultCollection.whereEqualTo("testId", testId)
                .whereEqualTo("roomId", roomId) // Фильтрация по roomId
                .addSnapshotListener(listener);
    }

    public ListenerRegistration getResultsByRoomId(String roomId, EventListener<QuerySnapshot> listener) {
        return resultCollection.whereEqualTo("roomId", roomId)
                .addSnapshotListener(listener);
    }




    public CompletableFuture<Boolean> getStudentResultByTestId(String questionId,String userId,String resultId) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        resultCollection.whereEqualTo("id", resultId).whereEqualTo("userId", userId).get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                Result result = document.toObject(Result.class);
                future.complete(result.getStudentAnswers().get(questionId));
            }
        });

        return future;
    }






}