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

import com.example.mytest.R;
import com.example.mytest.adapter.StudentAdapter;
import com.example.mytest.model.Report;
import com.example.mytest.model.Student;
import com.example.mytest.repository.StudentRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllStudentsActivity extends BaseActivity {
    private StudentRepository studentRepository;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private EditText searchInput;
    private List<Student> originalStudents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students);
        searchInput = findViewById(R.id.search_input);
        studentRepository = new StudentRepository(FirebaseFirestore.getInstance());
        recyclerView = findViewById(R.id.recycler_view_students);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadStudents();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterStudents(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    private void loadStudents() {
        studentRepository.getAllStudent().thenAccept(studentList -> {
            originalStudents = studentList;
            adapter = new StudentAdapter(studentList, student -> {
                studentRepository.deleteStudent(student);
                adapter.removeStudent(student);
                Toast.makeText(this, "Студент удален", Toast.LENGTH_SHORT).show();
            });
            recyclerView.setAdapter(adapter);
        }).exceptionally(throwable -> {
            Toast.makeText(this, "Ошибка загрузки студентов", Toast.LENGTH_SHORT).show();
            return null;
        });
    }
    private void filterStudents(String query) {
        List<Student> filteredList = new ArrayList<>();
        for (Student student : originalStudents) {
            // Ищем по названию теста (или другому полю, например, тексту жалобы)
            if (student.getFirstName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(student);
            }
        }
        adapter.updateList(filteredList); // Обновляем список в адаптере
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }
    public void OnAdd(View view) {
        Intent intent = new Intent(AllStudentsActivity.this, AddStudentActivity.class);
        startActivity(intent);
        finish();
    }


    public void OnBack(View view) {
        Intent intent = new Intent(AllStudentsActivity.this, AdminProfile.class);
        startActivity(intent);
        finish();
    }
}
