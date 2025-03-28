package com.example.mytest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.model.Student;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class AddStudentActivity extends BaseActivity {

    private EditText editTextFirstName, editTextLastName, editTextGroupNumber, editTextEmail, editTextPassword;
    private Button buttonAddStudent;
    private FirebaseFirestore db;
    private boolean isPasswordVisible = false;

    private final List<String> allowedDomains = Arrays.asList(
            "@mail.ru", "@gmail.com", "@yahoo.com", "@hotmail.com", "@outlook.com",
            "@icloud.com", "@aol.com", "@yandex.ru", "@zoho.com", "@tut.by", "@list.ru"
    );

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        db = FirebaseFirestore.getInstance();

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextGroupNumber = findViewById(R.id.editTextGroupNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonAddStudent = findViewById(R.id.buttonAddStudent);
        editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
        editTextGroupNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().matches("\\d*")) {
                    editTextGroupNumber.setError("Можно вводить только цифры");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        buttonAddStudent.setOnClickListener(view -> registerStudent());
    }

    private void registerStudent() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String groupNumber = editTextGroupNumber.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || groupNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!emailIsValid(email)) {
            Toast.makeText(this, "Email должен быть из разрешённого списка доменов", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        checkEmailExists(email, firstName, lastName, groupNumber, password);
    }

    private boolean emailIsValid(String email) {
        for (String domain : allowedDomains) {
            if (email.endsWith(domain)) {
                return true;
            }
        }
        return false;
    }

    private void checkEmailExists(String email, String firstName, String lastName, String groupNumber, String password) {
        db.collection("students").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        Toast.makeText(this, "Email уже зарегистрирован", Toast.LENGTH_SHORT).show();
                    } else {
                        addStudentToDatabase(firstName, lastName, groupNumber, email, password);
                    }
                });
    }

    private void addStudentToDatabase(String firstName, String lastName, String groupNumber, String email, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Создаем ссылку на новый документ (получаем его ID)
        String studentId = db.collection("students").document().getId();

        // Создаем объект студента с ID
        Student student = new Student(studentId, firstName, lastName, groupNumber, password, email, null); // photo = null

        // Теперь добавляем студента в Firestore с сохранением ID
        db.collection("students")
                .document(studentId)  // Устанавливаем ID вручную
                .set(student)         // Сохраняем объект
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Студент успешно добавлен!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddStudentActivity.this, AllStudentsActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show());
    }

    private void togglePasswordVisibility() {
        int selection = editTextPassword.getSelectionEnd();
        if (isPasswordVisible) {
            editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
        } else {
            editTextPassword.setTransformationMethod(null);
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        editTextPassword.setSelection(selection);
    }

    public void onBackStudentAdd(View view) {
        Intent intent = new Intent(this,AllStudentsActivity.class);
        startActivity(intent);
        finish();

    }
}
