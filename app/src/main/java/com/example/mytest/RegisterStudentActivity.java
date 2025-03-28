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
import com.example.mytest.model.Student;
import com.example.mytest.repository.StudentRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterStudentActivity extends BaseActivity {

    private EditText editTextFirstName, editTextLastName, editTextGroupNumber, editTextEmail, editTextPassword;
    private StudentRepository studentRepository;
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        studentRepository = new StudentRepository(FirebaseFirestore.getInstance());

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextGroupNumber = findViewById(R.id.editTextGroupNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonRegister = findViewById(R.id.buttonRegister);

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

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = editTextFirstName.getText().toString().trim();
                String lastName = editTextLastName.getText().toString().trim();
                String groupNumber = editTextGroupNumber.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (validateInput(firstName, lastName, groupNumber, email, password)) {
                    studentRepository.getStudentByEmail(email)
                            .thenAccept(find_student -> {
                                if (find_student == null) {
                                    Authentication.setTeacher(null);
                                    Authentication.setAdmin(null);
                                    Student student = new Student(null, firstName, lastName, groupNumber, password, email, null);
                                    studentRepository.addStudent(student);
                                    Authentication.setStudent(student);
                                    Intent intent = new Intent(RegisterStudentActivity.this, StudentProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    runOnUiThread(() ->
                                            Toast.makeText(RegisterStudentActivity.this, "Такой студент уже есть", Toast.LENGTH_SHORT).show());
                                }
                            });
                }
            }
        });
    }

    private boolean validateInput(String firstName, String lastName, String groupNumber, String email, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || groupNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
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

        if (!groupNumber.matches("\\d+")) {
            Toast.makeText(this, "Номер группы должен содержать только цифры", Toast.LENGTH_SHORT).show();
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

    public void ClickOnLoginPageStudent(View view) {
        Intent intent = new Intent(RegisterStudentActivity.this, LoginStudentActivity.class);
        startActivity(intent);
        finish();
    }
}
