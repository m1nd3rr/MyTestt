package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.StudentResultAdapter;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Result;
import com.example.mytest.repository.ResultRepository;
import com.example.mytest.repository.StudentRepository;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RoomCodeActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private List<Result> resultList = new ArrayList<>();
    private StudentResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_code);

        // Инициализация текстовых полей
        TextView roomCodeTextView = findViewById(R.id.tvRoomCode);
        TextView testNameTextView = findViewById(R.id.testNameTextView);
        ImageView imageView = findViewById(R.id.back);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, TeacherProfileActivity.class);
            startActivity(intent);
            finish();
        });
        // Получение данных из Intent
        String roomCode = getIntent().getStringExtra("ROOM_CODE");
        String testName = getIntent().getStringExtra("TEST_NAME");

        // Установка данных в текстовые поля
        roomCodeTextView.setText("Код комнаты: " + roomCode);
        testNameTextView.setText("Название теста: " + (testName != null ? testName : "Без названия"));

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.rvStudents);
        adapter = new StudentResultAdapter(resultList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Загрузка результатов и данных студентов
        loadResultsAndStudents();
    }

    private void loadResultsAndStudents() {
        ResultRepository resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
        StudentRepository studentRepository = new StudentRepository(FirebaseFirestore.getInstance());

        // Получаем roomId из переданного Intent
        String roomId = getIntent().getStringExtra("ROOM_ID"); // предполагаем, что передаём roomId через Intent

        // Слушатель для получения результатов по testId и roomId
        resultRepository.getResultsByTestIdAndRoomId(Select.getTest().getId(), roomId, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    // Обработка ошибок
                    return;
                }

                resultList.clear();

                if (queryDocumentSnapshots != null) {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Result result = document.toObject(Result.class);
                        studentRepository.getStudentById(result.getUserId())
                                .thenAccept(student -> {
                                    resultList.add(result);
                                    adapter.notifyDataSetChanged();
                                });
                    }
                }
            }
        });
    }
}