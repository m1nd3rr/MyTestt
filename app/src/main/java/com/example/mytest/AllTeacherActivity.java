package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.TeacherAdapter;
import com.example.mytest.repository.TeacherRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class AllTeacherActivity extends AppCompatActivity {
    private TeacherRepository teacherRepository;
    private RecyclerView recyclerView;
    private TeacherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_teacher_activity);

        teacherRepository = new TeacherRepository(FirebaseFirestore.getInstance());
        recyclerView = findViewById(R.id.recycler_view_teachers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTeachers();
    }

    private void loadTeachers() {
        teacherRepository.getAllTeacher().thenAccept(teacherList -> {
            adapter = new TeacherAdapter(teacherList, teacher -> {
                teacherRepository.deleteTeacher(teacher);
                adapter.removeTeacher(teacher);
                Toast.makeText(this, "Преподаватель удален", Toast.LENGTH_SHORT).show();
            });
            recyclerView.setAdapter(adapter);
        }).exceptionally(throwable -> {
            Toast.makeText(this, "Ошибка загрузки преподавателей", Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    public void OnBack(View view) {
        Intent intent = new Intent(AllTeacherActivity.this, AdminProfile.class);
        startActivity(intent);
        finish();
    }
}
