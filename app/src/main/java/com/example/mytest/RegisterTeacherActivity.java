package com.example.mytest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.auth.Authentication;
import com.example.mytest.model.Teacher;
import com.example.mytest.repository.TeacherRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterTeacherActivity extends BaseActivity {

    private EditText editTextTeacherFirstName, editTextTeacherLastName, editTextEmail, editTextPassword;
    private TeacherRepository teacherRepository;
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);

        editTextTeacherFirstName = findViewById(R.id.editTextTeacherFirstName);
        editTextTeacherLastName = findViewById(R.id.editTextTeacherLastName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonSubmitTeacher = findViewById(R.id.buttonSubmitTeacher);

        teacherRepository = new TeacherRepository(FirebaseFirestore.getInstance());

        buttonSubmitTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = editTextTeacherFirstName.getText().toString().trim();
                String lastName = editTextTeacherLastName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (validateInput(firstName, lastName, email, password)) {
                    teacherRepository.getTeacherByEmail(email)
                            .thenAccept(find_teacher -> {
                                if (find_teacher == null) {
                                    Authentication.setStudent(null);
                                    Authentication.setAdmin(null);
                                    Teacher teacher = new Teacher(null, firstName, lastName, password, email, null);
                                    teacherRepository.addTeacher(teacher);
                                    Authentication.setTeacher(teacher);
                                    Intent intent = new Intent(RegisterTeacherActivity.this, TeacherProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    runOnUiThread(() ->
                                            Toast.makeText(RegisterTeacherActivity.this, "Такой учитель уже есть", Toast.LENGTH_SHORT).show());
                                }
                            });
                }
            }
        });
        editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });
    }

    private boolean validateInput(String firstName, String lastName, String email, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Введите корректный адрес электронной почты", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Проверка email на соответствие разрешённым доменам
        if (!isValidEmailDomain(email)) {
            Toast.makeText(this, "Email должен быть из разрешенных доменов", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Пароль должен содержать минимум 6 символов", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (firstName.length() < 3 || firstName.length() > 20) {
            Toast.makeText(this, "Имя должно содержать от 3 до 20 символов", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastName.length() < 3 || lastName.length() > 20) {
            Toast.makeText(this, "Фамилия должна содержать от 3 до 20 символов", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Метод проверки домена email
    private boolean isValidEmailDomain(String email) {
        String[] allowedDomains = {
                "@mail.ru", "@gmail.com", "@yahoo.com", "@hotmail.com",
                "@outlook.com", "@icloud.com", "@aol.com", "@yandex.ru",
                "@zoho.com", "@tut.by", "@list.ru"
        };

        for (String domain : allowedDomains) {
            if (email.endsWith(domain)) {
                return true;
            }
        }
        return false;
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

    public void ClickOnLoginPageTeacher(View view) {
        Intent intent = new Intent(this, LoginTeacherActivity.class);
        startActivity(intent);
        finish();
    }
}
