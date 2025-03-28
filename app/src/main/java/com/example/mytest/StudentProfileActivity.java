package com.example.mytest;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Pair;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Student;
import com.example.mytest.model.Test;
import com.example.mytest.repository.ResultRepository;
import com.example.mytest.repository.RoomRepository;
import com.example.mytest.repository.StudentRepository;
import com.example.mytest.repository.TestRepository;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import android.Manifest;

import java.util.ArrayList;

public class StudentProfileActivity extends BaseActivity {
    private StudentRepository studentRepository;
    private RoomRepository roomRepository;
    private ResultRepository resultRepository;
    TestRepository testRepository;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ImageView messageIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        roomRepository = new RoomRepository(FirebaseFirestore.getInstance());
        resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
        testRepository = new TestRepository(FirebaseFirestore.getInstance());
        studentRepository = new StudentRepository(FirebaseFirestore.getInstance());
        setCompleteTest();
        setCreateTestUser();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        TextView textView = findViewById(R.id.userName);
        TextView textView1 = findViewById(R.id.userGroup);
        textView1.setText(Authentication.getStudent().getGroupNumber());
        textView.setText(Authentication.getStudent().getFirstName());
        ImageView photoUser = findViewById(R.id.profile_image);
        if (Authentication.getStudent().getPhoto() != null) {
            Glide.with(this).load(Authentication.getStudent().getPhoto()).apply(new RequestOptions()
                    .centerCrop()
                    .circleCrop()).into(photoUser);
        }


        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_home){
                return true;
            }
            else if (item.getItemId() == R.id.navigation_search){
                Intent intent = new Intent(this, TestsActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        resultRepository.getResultByStudentId(Authentication.student.getId())
                .thenAccept(pair -> {
                    createPieDiagram(pair);
                });
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            ImageView photo = findViewById(R.id.profile_image);
                            Glide.with(this).load(imageUri).apply(new RequestOptions()
                                    .centerCrop()
                                    .circleCrop()).into(photo);

                            String userId = Authentication.getStudent().getId();
                            CloudinaryUploader uploader = new CloudinaryUploader(this);
                            uploader.uploadImage(imageUri, userId, new CloudinaryUploader.UploadCallback() {
                                @Override
                                public void onUploadComplete(String imageUrl) {
                                    if (imageUrl != null) {
                                        Authentication.getStudent().setPhoto(imageUrl);
                                        studentRepository.updateStudent(Authentication.getStudent());
                                    }
                                }
                            });
                        }
                    }
                });
    }


    public void createPieDiagram(Pair<Integer, Integer> pair) {
        PieChart pieChart = findViewById(R.id.pieChart);
        int correctAnswers = pair.first;
        int totalQuestions = pair.second;

        float percentageCorrect = (correctAnswers / (float) totalQuestions) * 100;
        float percentageIncorrect = 100 - percentageCorrect;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(percentageCorrect, ""));
        entries.add(new PieEntry(percentageIncorrect, ""));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.parseColor("#1ED8A6"), Color.LTGRAY);
        dataSet.setValueTextSize(0f);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setRotationEnabled(false);

        pieChart.setHoleRadius(90f);
        pieChart.setHoleColor(Color.TRANSPARENT);

        pieChart.setCenterText(Math.round(percentageCorrect) + "%");
        pieChart.setCenterTextSize(24f);
        pieChart.setCenterTextColor(Color.BLACK);

        pieChart.animateY(1000);
        pieChart.invalidate();
    }


    public void ClickMama(View view) {
        showEnterCodeDialog();
    }

    private void showEnterCodeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите код комнаты");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String roomCode = input.getText().toString();
                if (!roomCode.isEmpty()) {
                    fetchTestByRoomCode(roomCode);
                } else {
                    Toast.makeText(StudentProfileActivity.this, "Введен неправильный код", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void fetchTestByRoomCode(String roomCode) {
        roomRepository.getTestByRoomNumber(roomCode)
                .thenAccept(test -> {
                    if (test != null) {
                        Select.setTest(test);
                        Intent intent = new Intent(this, PassingTestActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid room code", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setCompleteTest(){
        TextView textView = findViewById(R.id.completeTest);
        resultRepository.getAllResultByStudentId(Authentication.student.getId()).thenAccept(list->{
            textView.setText(String.valueOf(list.size()));
        });
    }

    public void CreateTest(View view) {

        Test test = new Test();
        test.setStudentId(Authentication.getStudent().getId());
        Intent intent = new Intent(this, CreateTestActivity.class);
        Select.setTest(testRepository.addTest(test));
        startActivity(intent);
        finish();
    }
    public void setCreateTestUser(){
        TextView textView = findViewById(R.id.setCreateTestUser);
        testRepository.getAllTestByStudentId(Authentication.student.getId()).thenAccept(list ->{
            textView.setText(String.valueOf(list.size()));
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
    public void ClickEditStudent(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_student, null);
        builder.setView(dialogView);

        EditText editFirstName = dialogView.findViewById(R.id.editFirstName);
        EditText editLastName = dialogView.findViewById(R.id.editLastName);
        EditText editGroupNumber = dialogView.findViewById(R.id.editTextGroupNumber);
        EditText editPassword = dialogView.findViewById(R.id.editPassword);
        EditText editEmail = dialogView.findViewById(R.id.editEmail);

        Student currentStudent = Authentication.getStudent();
        editFirstName.setText(currentStudent.getFirstName());
        editLastName.setText(currentStudent.getLastName());
        editGroupNumber.setText(currentStudent.getGroupNumber());
        editPassword.setText(currentStudent.getPassword());
        editEmail.setText(currentStudent.getEmail());

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
            String groupNumber = editGroupNumber.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String email = editEmail.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || groupNumber.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
                return;
            }

            currentStudent.setFirstName(firstName);
            currentStudent.setLastName(lastName);
            currentStudent.setGroupNumber(groupNumber);
            currentStudent.setPassword(password);
            currentStudent.setEmail(email);

            studentRepository.updateStudent(currentStudent);

            TextView userName = findViewById(R.id.userName);
            userName.setText(currentStudent.getFirstName());
            dialog.dismiss();
        });
    }

    public void onBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void ClickShowCreateTest(View view) {
        Intent intent = new Intent(this,TestHistory.class);
        intent.putExtra("isCompleteMode", false);
        startActivity(intent);
        finish();
    }

    public void ClickShowCompleteTest(View view) {
        Intent intent = new Intent(this,TestHistory.class);
        intent.putExtra("isCompleteMode", true);
        startActivity(intent);
        finish();
    }
}