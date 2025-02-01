package com.example.mytest;

import static com.example.mytest.auth.Authentication.getTeacher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytest.adapter.TestAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Teacher;
import com.example.mytest.model.Test;
import com.example.mytest.repository.TeacherRepository;
import com.example.mytest.repository.TestRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TeacherProfileActivity extends AppCompatActivity {

    TestRepository testRepository;
    private TeacherRepository teacherRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);
        testRepository = new TestRepository(FirebaseFirestore.getInstance());
        teacherRepository = new TeacherRepository(FirebaseFirestore.getInstance());
        setCreateTest();
        updateTestCount();

        TextView textView = findViewById(R.id.userName);
        textView.setText(getTeacher().getFirstName());
        ImageView photoUser = findViewById(R.id.profile_image_teacher);
        if (getTeacher().getPhoto() != null) {
            Glide.with(this).load(getTeacher().getPhoto()).apply(new RequestOptions()
                    .centerCrop()
                    .circleCrop()).into(photoUser);
        }

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            ImageView photo = findViewById(R.id.profile_image_teacher);
                            Glide.with(this).load(imageUri).apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()).into(photo);

                            String userId = getTeacher().getId();
                            CloudinaryUploader uploader = new CloudinaryUploader(this);
                            uploader.uploadImage(imageUri, userId, new CloudinaryUploader.UploadCallback() {
                                @Override
                                public void onUploadComplete(String imageUrl) {
                                    if (imageUrl != null) {
                                        getTeacher().setPhoto(imageUrl);
                                        teacherRepository.updateTeacher(getTeacher());
                                    }
                                }
                            });
                        }
                    }
                });

    }

    public void ClickOnCreateTest(View view) {
        Test test = new Test();
        test.setTeacherId(getTeacher().getId());
        Intent intent = new Intent(this, CreateTestActivity.class);
        Select.setTest(testRepository.addTest(test));
        startActivity(intent);
        finish();
    }

    public void ClickOnTestHistory(View view) {
        Intent intent = new Intent(this,TestHistory.class);
        startActivity(intent);
        finish();
    }
    private void setCreateTest() {
        Teacher teacher = getTeacher(); // Метод, который должен вернуть текущего учителя
        if (teacher != null) {
            String teacherId = teacher.getId();
            // Дальше продолжайте логику
        } else {
            Log.e("TeacherProfileActivity", "Teacher object is null, cannot create test.");
            // Обработайте ошибку
        }
    }

    private void updateTestCount() {
        String teacherId = getTeacher().getId(); // Получаем ID текущего преподавателя
        TextView createTestTextView = findViewById(R.id.createTest);

        // Вызываем метод репозитория
        testRepository.getAllTestByTeacherId(teacherId).thenAccept(tests -> {
            // Обновляем текстовое поле на основе результата
            if (tests != null && !tests.isEmpty()) {
                createTestTextView.setText(String.valueOf(tests.size()));
            } else {
                createTestTextView.setText("0"); // Если тестов нет, отображаем 0
            }
        }).exceptionally(throwable -> {
            // Обрабатываем возможные ошибки
            Log.e("TeacherProfileActivity", "Ошибка при получении тестов", throwable);
            createTestTextView.setText("0");
            return null;
        });
    }



    public void onClickOpenGallery(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 1);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void ClickEditTeacher(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_teacher, null);
        builder.setView(dialogView);

        EditText editFirstName = dialogView.findViewById(R.id.editFirstName);
        EditText editLastName = dialogView.findViewById(R.id.editLastName);
        EditText editPassword = dialogView.findViewById(R.id.editPassword);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);

        if (editFirstName == null || editLastName == null || editPassword == null || editEmail == null) {
            throw new IllegalStateException("Ошибка в макете диалога: элемент не найден.");
        }

        Teacher currentTeacher = getTeacher();
        editFirstName.setText(currentTeacher.getFirstName());
        editLastName.setText(currentTeacher.getLastName());
        editPassword.setText(currentTeacher.getPassword());
        editEmail.setText(currentTeacher.getEmail());

        editPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editPassword.getRight() - editPassword.getCompoundDrawables()[2].getBounds().width())) {
                    if (editPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                        editPassword.setTransformationMethod(null);
                        editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                    } else {
                        editPassword.setTransformationMethod(new PasswordTransformationMethod());
                        editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                    }
                    editPassword.setSelection(editPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        builder.setTitle("Редактировать данные")
                .setPositiveButton("Сохранить", null)
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
                return;
            }

            currentTeacher.setFirstName(firstName);
            currentTeacher.setLastName(lastName);
            currentTeacher.setPassword(password);
            currentTeacher.setEmail(email);

            teacherRepository.updateTeacher(currentTeacher);

            TextView userName = findViewById(R.id.userName);
            userName.setText(currentTeacher.getFirstName());
            dialog.dismiss();
        });
    }

    public void ClickOnRoomHistory(View view) {
        Intent intent = new Intent(this, RoomHistoryActivity.class);
        startActivity(intent);
    }

    public void onBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}