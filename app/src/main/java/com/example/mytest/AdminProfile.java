package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.repository.AdminRepository;
import com.example.mytest.repository.ReportRepository;
import com.example.mytest.repository.StudentRepository;
import com.example.mytest.repository.TeacherRepository;
import com.example.mytest.repository.TestRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminProfile extends AppCompatActivity {
    private AdminRepository adminRepository;
    private StudentRepository studentRepository;
    private TeacherRepository teacherRepository;
    private TestRepository testRepository;
    private TextView allReports;
    private ReportRepository reportRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Инициализация репозиториев
        adminRepository = new AdminRepository(FirebaseFirestore.getInstance());
        studentRepository = new StudentRepository(FirebaseFirestore.getInstance());
        teacherRepository = new TeacherRepository(FirebaseFirestore.getInstance());
        testRepository = new TestRepository(FirebaseFirestore.getInstance());
        reportRepository = new ReportRepository(FirebaseFirestore.getInstance());
        allReports = findViewById(R.id.allReports);


        loadAllStudents();
        loadAllTeachers();
        loadAllTests();
        updateComplaintCount();

    }

    public void loadAllStudents() {
        TextView textViewStudents = findViewById(R.id.allStudent);
        studentRepository.getAllStudent().thenAccept(studentList -> {
            runOnUiThread(() -> {
                int studentCount = studentList.size();
                textViewStudents.setText("Всего студентов: " + studentCount);
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Ошибка загрузки студентов", Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }
    private void updateComplaintCount() {
        reportRepository.getAllReportss()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        int complaintCount = task.getResult().size(); // Получаем общее количество жалоб
                        allReports.setText("Всего жалоб: " + complaintCount); // Обновляем TextView с текстом
                    } else {
                        allReports.setText("Всего жалоб: 0"); // Если ошибка или нет жалоб, устанавливаем 0
                    }
                });
    }



    public void loadAllTeachers() {
        TextView textViewTeachers = findViewById(R.id.allTeacher);
        teacherRepository.getAllTeacher().thenAccept(teacherList -> {
            runOnUiThread(() -> {
                int teacherCount = teacherList.size();
                textViewTeachers.setText("Всего преподавателей: " + teacherCount);
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Ошибка загрузки преподавателей", Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }

    public void loadAllTests() {
        TextView textViewTests = findViewById(R.id.allTests); // Добавьте TextView в XML с id allTests
        testRepository.getAllTest().thenAccept(testList -> {
            runOnUiThread(() -> {
                int testCount = testList.size();
                textViewTests.setText("Всего тестов: " + testCount); // Отображаем количество тестов
            });
        }).exceptionally(throwable -> {
            runOnUiThread(() -> {
                Toast.makeText(this, "Ошибка загрузки тестов", Toast.LENGTH_SHORT).show();
            });
            return null;
        });
    }

    public void openAllStudents(View view) {
        Intent intent = new Intent(this, AllStudentsActivity.class);
        startActivity(intent);
    }

    public void openAllTeachers(View view) {
        Intent intent = new Intent(this, AllTeacherActivity.class);
        startActivity(intent);
    }

    public void openAllTests(View view) {
        Intent intent = new Intent(this, AllTestsActivity.class);
        startActivity(intent);
    }

    public void onBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void AllReports(View view) {
        Intent intent = new Intent(this, AllReportsActivity.class);
        startActivity(intent);
    }
}
