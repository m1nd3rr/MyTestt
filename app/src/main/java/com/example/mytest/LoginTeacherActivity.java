package com.example.mytest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.auth.Authentication;
import com.example.mytest.repository.TeacherRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginTeacherActivity extends BaseActivity {

    private EditText editTextEmail, editTextPassword;
    private TeacherRepository teacherRepository;
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_teacher);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        teacherRepository = new TeacherRepository(FirebaseFirestore.getInstance());

        editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    v.performClick();
                    return true;
                }
            }
            return false;
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (validateInput(email, password)) {
                    teacherRepository.getTeacherByEmail(email)
                            .thenAccept(teacher -> {
                                if (teacher != null && teacher.getPassword().equals(password)) {
                                    Authentication.setStudent(null);
                                    Authentication.setAdmin(null);
                                    Authentication.setTeacher(teacher);
                                    Intent intent = new Intent(LoginTeacherActivity.this, TeacherProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    runOnUiThread(() ->
                                            Toast.makeText(LoginTeacherActivity.this, "Неверный email или пароль", Toast.LENGTH_SHORT).show());
                                }
                            })
                            .exceptionally(throwable -> {
                                runOnUiThread(() ->
                                        Toast.makeText(LoginTeacherActivity.this, "Ошибка при попытке входа", Toast.LENGTH_SHORT).show());
                                return null;
                            });
                }
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Введите корректный адрес электронной почты", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void togglePasswordVisibility() {
        Typeface currentTypeface = editTextPassword.getTypeface();
        int selection = editTextPassword.getSelectionEnd();

        if (isPasswordVisible) {
            editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
        } else {
            editTextPassword.setTransformationMethod(null);
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
        }

        isPasswordVisible = !isPasswordVisible;
        editTextPassword.setTypeface(currentTypeface);
        editTextPassword.setSelection(selection);
    }

    public void openLoginActivity(View view) {
        Intent intent = new Intent(this, RegisterTeacherActivity.class);
        startActivity(intent);
        finish();

    }
}
