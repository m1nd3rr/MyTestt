package com.example.mytest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.model.Teacher;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class AddTeacherActivity extends BaseActivity {

    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    private Button buttonAddTeacher;
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
        setContentView(R.layout.activity_add_teacher);

        db = FirebaseFirestore.getInstance();

        editTextFirstName = findViewById(R.id.editTextTeacherFirstName);
        editTextLastName = findViewById(R.id.editTextTeacherLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonAddTeacher = findViewById(R.id.buttonAddTeacher);
        editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
        buttonAddTeacher.setOnClickListener(view -> registerTeacher());
    }

    private void registerTeacher() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
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

        checkEmailExists(email, firstName, lastName, password);
    }

    private boolean emailIsValid(String email) {
        for (String domain : allowedDomains) {
            if (email.endsWith(domain)) {
                return true;
            }
        }
        return false;
    }

    private void checkEmailExists(String email, String firstName, String lastName, String password) {
        db.collection("teacher").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        Toast.makeText(this, "Email уже зарегистрирован", Toast.LENGTH_SHORT).show();
                    } else {
                        addTeacherToDatabase(email,firstName, lastName, password);
                    }
                });
    }

    private void addTeacherToDatabase(String firstName, String lastName, String email, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String teacherId = db.collection("teacher").document().getId();

        Teacher teacher = new Teacher(teacherId, firstName, lastName, password, email, null); // photo = null

        db.collection("teacher")
                .document(teacherId)
                .set(teacher)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Преподаватель успешно добавлен!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddTeacherActivity.this, AllTeacherActivity.class);
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

    public void onBackTeacher(View view) {
        Intent intent = new Intent(this,AllTeacherActivity.class);
        startActivity(intent);
        finish();
    }
}
