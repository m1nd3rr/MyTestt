package com.example.mytest.repository;

import android.util.Log;

import com.example.mytest.auth.Authentication;
import com.example.mytest.model.Room;
import com.example.mytest.model.Test;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoomRepository {
    private final CollectionReference roomCollection;
    private final TestRepository testRepository;
    private Query testCollection;

    public RoomRepository(FirebaseFirestore db){
        roomCollection = db.collection("room");
        testRepository = new TestRepository(db);
    }

    public Room addRoom(Room room) {
        String teacherId = Authentication.getTeacherId();  // Получаем teacherId

        if (teacherId != null) {
            // Получаем уникальный идентификатор для нового документа
            String roomId = FirebaseFirestore.getInstance().collection("room").document().getId();
            room.setId(roomId);
            room.setRoomNumber(roomId.substring(0, 5));  // Пример создания номера комнаты
            room.setTeacherId(teacherId);  // Устанавливаем teacherId в комнату

            // Сохраняем документ в Firestore
            FirebaseFirestore.getInstance()
                    .collection("room")  // Коллекция для комнат
                    .document(roomId)     // Создаем новый документ с уникальным идентификатором
                    .set(room)             // Сохраняем объект комнаты
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("RoomRepository", "Room added successfully");
                        } else {
                            Log.e("RoomRepository", "Error adding room", task.getException());
                        }
                    });

            return room;
        } else {
            Log.e("RoomRepository", "Teacher is not authenticated.");
            return null;
        }
    }

    private String generateRoomNumber() {
        return String.valueOf(System.currentTimeMillis()).substring(8); // Возвращает последние 5 символов текущего времени
    }



    public void deleteRoom(Room room) {
        roomCollection.document(room.getId()).delete();
    }

    public void updateRoom(Room room) {
        roomCollection.document(room.getId()).set(room);
    }

    // Получение всех комнат
    public CompletableFuture<List<Room>> getAllRoom() {
        CompletableFuture<List<Room>> future = new CompletableFuture<>();
        List<Room> roomList = new ArrayList<>();

        roomCollection.get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                Room room = document.toObject(Room.class);
                roomList.add(room);
            }
            future.complete(roomList);
        });

        return future;
    }

    // Получение комнат по номеру
    public CompletableFuture<Test> getTestByRoomNumber(String roomNumber) {
        CompletableFuture<Test> future = new CompletableFuture<>();
        roomCollection.whereEqualTo("roomNumber", roomNumber).get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                Room room = document.toObject(Room.class);
                testRepository.getById(room.getTestId())
                        .thenAccept(future::complete);
            }
        });
        return future;
    }

    // Получение всех комнат, связанных с тестом
    public CompletableFuture<List<Room>> getAllRoomsByTestId(String testId) {
        CompletableFuture<List<Room>> future = new CompletableFuture<>();
        List<Room> roomList = new ArrayList<>();

        roomCollection.whereEqualTo("testId", testId).get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {
                Room room = document.toObject(Room.class);
                roomList.add(room);
            }
            future.complete(roomList);
        });

        return future;
    }

    // Получение всех комнат, созданных преподавателем
    public CompletableFuture<List<Room>> getRoomsByTeacherId(String teacherId) {
        CompletableFuture<List<Room>> future = new CompletableFuture<>();
        roomCollection.whereEqualTo("teacherId", teacherId)  // Проверьте правильность полей
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Room> rooms = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Room room = document.toObject(Room.class);
                            rooms.add(room);
                        }
                        future.complete(rooms);
                    } else {
                        future.completeExceptionally(task.getException());
                    }
                });
        return future;
    }


}