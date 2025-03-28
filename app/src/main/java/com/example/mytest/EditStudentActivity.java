package com.example.mytest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.mytest.model.Student;
import com.example.mytest.repository.StudentRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditStudentActivity extends BaseActivity {
    private ImageView imgAvatar;
    private EditText edtFirstName, edtLastName, edtEmail, editTextPassword, edtGroupNumber;
    private Uri avatarUri;
    private StudentRepository studentRepository;
    private Student currentStudent;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        imgAvatar = findViewById(R.id.student_avatar);
        edtFirstName = findViewById(R.id.edit_first_name);
        edtLastName = findViewById(R.id.edit_last_name);
        edtEmail = findViewById(R.id.edit_email);
        editTextPassword = findViewById(R.id.editTextPassword);
        edtGroupNumber = findViewById(R.id.edit_group_number);

        studentRepository = new StudentRepository(FirebaseFirestore.getInstance());
        //currentStudent = Authentication.getStudent();


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


        loadStudentData();
        setupImagePicker();

        imgAvatar.setOnClickListener(view -> openGallery());
        findViewById(R.id.save_button).setOnClickListener(view -> saveStudentData());

    }

    private void loadStudentData() {
        Intent intent = getIntent();
        if (intent != null) {
            edtFirstName.setText(intent.getStringExtra("first_name"));
            edtLastName.setText(intent.getStringExtra("last_name"));
            edtEmail.setText(intent.getStringExtra("email"));
            editTextPassword.setText(intent.getStringExtra("password"));
            edtGroupNumber.setText(intent.getStringExtra("group_number"));

            currentStudent = new Student();
            currentStudent.setId(intent.getStringExtra("student_id"));

            String photoUrl = intent.getStringExtra("photo");
            if (photoUrl != null) {
                Glide.with(this).load(photoUrl).into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.ava);
            }
        } else {
            Toast.makeText(this, "Ошибка загрузки данных студента", Toast.LENGTH_SHORT).show();
            finish();  // Закрываем активность, если intent не передал данные
        }
    }



    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        avatarUri = result.getData().getData();
                        Glide.with(this).load(avatarUri).into(imgAvatar);
                    }
                }
        );
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
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

    private void saveStudentData() {
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String groupNumber = edtGroupNumber.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || groupNumber.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        currentStudent.setFirstName(firstName);
        currentStudent.setLastName(lastName);
        currentStudent.setEmail(email);
        currentStudent.setPassword(password);
        currentStudent.setGroupNumber(groupNumber);

        studentRepository.updateStudent(currentStudent);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_student_id", currentStudent.getId());
        resultIntent.putExtra("updated_first_name", firstName);
        resultIntent.putExtra("updated_last_name", lastName);
        resultIntent.putExtra("updated_email", email);
        resultIntent.putExtra("updated_password", password);
        resultIntent.putExtra("updated_group_number", groupNumber);
        if (currentStudent.getPhoto() != null) {
            resultIntent.putExtra("updated_photo", currentStudent.getPhoto());
        }
        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onBackStudent(View view) {
        Intent intent = new Intent(this,AllStudentsActivity.class);
        startActivity(intent);
        finish();
    }
}
