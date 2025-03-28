package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.TeacherAdapter;
import com.example.mytest.model.Student;
import com.example.mytest.model.Teacher;
import com.example.mytest.repository.TeacherRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllTeacherActivity extends BaseActivity {
    private TeacherRepository teacherRepository;
    private RecyclerView recyclerView;
    private TeacherAdapter adapter;
    private EditText searchInput;
    private List<Teacher> originalTeachers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_teacher_activity);
        searchInput = findViewById(R.id.search_input);
        teacherRepository = new TeacherRepository(FirebaseFirestore.getInstance());
        recyclerView = findViewById(R.id.recycler_view_teachers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTeachers();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterTeachers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void loadTeachers() {
        teacherRepository.getAllTeacher().thenAccept(teacherList -> {
            originalTeachers = teacherList;
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

    private void filterTeachers(String query) {
        List<Teacher> filteredList = new ArrayList<>();
        for (Teacher teacher : originalTeachers) {
            // Ищем по названию теста (или другому полю, например, тексту жалобы)
            if (teacher.getFirstName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(teacher);
            }
        }
        adapter.updateList(filteredList); // Обновляем список в адаптере
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTeachers();
    }
    public void OnAdd(View view) {
        Intent intent = new Intent(AllTeacherActivity.this, AddTeacherActivity.class);
        startActivity(intent);
        finish();
    }

    public void OnBack(View view) {
        Intent intent = new Intent(AllTeacherActivity.this, AdminProfile.class);
        startActivity(intent);
        finish();
    }
}
