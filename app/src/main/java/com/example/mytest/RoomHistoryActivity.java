package com.example.mytest;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.RoomHistoryAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.model.Room;
import com.example.mytest.repository.RoomRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RoomHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RoomHistoryAdapter roomHistoryAdapter;
    private List<Room> roomList = new ArrayList<>();
    private RoomRepository roomRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_history);

        // Инициализация RoomRepository
        roomRepository = new RoomRepository(FirebaseFirestore.getInstance());

        recyclerView = findViewById(R.id.recyclerViewRoomHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Инициализация адаптера
        roomHistoryAdapter = new RoomHistoryAdapter(roomList, this);
        recyclerView.setAdapter(roomHistoryAdapter);

        // Загружаем комнаты, созданные текущим преподавателем
        fetchRooms();
    }

    private void fetchRooms() {
        String teacherId = Authentication.getTeacherId(); // Получаем teacherId

        if (teacherId != null) {
            roomRepository.getRoomsByTeacherId(teacherId).thenAccept(rooms -> {
                runOnUiThread(() -> {
                    if (rooms != null && !rooms.isEmpty()) {
                        roomList.clear();
                        roomList.addAll(rooms);
                        roomHistoryAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(RoomHistoryActivity.this, "Нет созданных комнат.", Toast.LENGTH_SHORT).show();
                    }
                });
            }).exceptionally(ex -> {
                runOnUiThread(() -> Toast.makeText(RoomHistoryActivity.this, "Ошибка при загрузке данных.", Toast.LENGTH_SHORT).show());
                Log.e("RoomHistoryActivity", "Error fetching rooms", ex);
                return null;
            });
        } else {
            Toast.makeText(RoomHistoryActivity.this, "Преподаватель не авторизован.", Toast.LENGTH_SHORT).show();
        }
    }
}
