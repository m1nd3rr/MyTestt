package com.example.mytest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytest.adapter.AnswerAdapter;
import com.example.mytest.adapter.QuestionAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Question;
import com.example.mytest.model.Room;
import com.example.mytest.model.Test;
import com.example.mytest.repository.QuestionRepository;
import com.example.mytest.repository.RoomRepository;
import com.example.mytest.repository.TestRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CreateTestActivity extends AppCompatActivity {

    QuestionRepository questionRepository;
    RoomRepository roomRepository;
    TestRepository testRepository;
    QuestionAdapter questionAdapter;
    List<Question> questionList = new ArrayList<>();
    EditText editText;
    Test test;
    TextView tvSelectTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        test = Select.getTest();
        if (test.getDuration() == null) {
            test.setDuration(null); // Установка бесконечного времени по умолчанию
        }
        boolean isCompleteMode = getIntent().getBooleanExtra("isCompleteMode", false);
        tvSelectTime = findViewById(R.id.tvSelectTime);
        tvSelectTime.setText(test.getDuration() == null ? "∞ Без времени" : test.getDuration() + " минут");

//        if (Authentication.getStudent() == null ) {
//            if(Authentication.getAdmin() == null){
//                findViewById(R.id.btnPublishTest).setVisibility(View.VISIBLE);
//            }
//        }
//        else {
//            findViewById(R.id.btnPublishTest).setVisibility(View.GONE);
//        }

        if(Authentication.getStudent() != null || Authentication.getAdmin()!= null){
            findViewById(R.id.btnPublishTest).setVisibility(View.GONE);
        }else {
            findViewById(R.id.btnPublishTest).setVisibility(View.VISIBLE);
        }
        questionRepository = new QuestionRepository(FirebaseFirestore.getInstance());
        roomRepository = new RoomRepository(FirebaseFirestore.getInstance());
        testRepository = new TestRepository(FirebaseFirestore.getInstance());
        RecyclerView recyclerView = findViewById(R.id.rvQuestionsList);
        editText = findViewById(R.id.etTestTitle);


        editText.setText(test.getTitle());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                test.setTitle(String.valueOf(s));
                testRepository.updateTest(test);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        questionRepository.getAllQuestionByTestId(test.getId())
                .thenAccept(list -> {
                    questionList.addAll(list);
                    questionAdapter = new QuestionAdapter(questionList,this, isCompleteMode);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(questionAdapter);
                    questionAdapter.notifyItemInserted(list.size());
                });

        findViewById(R.id.btnCreateQuestion).setOnClickListener(view -> {
            Question question = new Question();
            question.setTestId(test.getId());

            Intent intentQuestion = new Intent(CreateTestActivity.this, QuestionSettingActivity.class);
            Select.setQuestion(question);
            startActivity(intentQuestion);
            finish();
        });

        findViewById(R.id.btnCreateTest).setOnClickListener(view -> {
            if (questionList.isEmpty()) {
                Toast.makeText(this, "Нельзя создать тест без вопросов.", Toast.LENGTH_SHORT).show();
                return;
            }
            test.setTitle(editText.getText().toString());
            testRepository.updateTest(test);

            Intent intentProfile;
            if (Authentication.student != null) {
                intentProfile = new Intent(this, StudentProfileActivity.class);
            } else {
                intentProfile = new Intent(this, TeacherProfileActivity.class);
            }
            startActivity(intentProfile);
            finish();
        });
    }

    public void showTimePicker(View view) {
        final String[] timeOptions = {"∞", "5 минут", "10 минут", "15 минут", "20 минут", "30 минут", "45 минут"};
        final Integer[] timeValues = {null, 5, 10, 15, 20, 30, 45}; // null означает бесконечность

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите время")
                .setItems(timeOptions, (dialog, which) -> {
                    Integer selectedValue = timeValues[which];
                    if (selectedValue == null) {
                        tvSelectTime.setText("∞");
                    } else {
                        tvSelectTime.setText(timeOptions[which]);
                    }
                    test.setDuration(selectedValue); // null для бесконечности
                    testRepository.updateTest(test);
                });
        builder.create().show();
    }


    public void onBackButtonClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Внимание")
                .setMessage("Если вы выйдете, то данные теста будут удалены. Вы уверены?")
                .setPositiveButton("Выйти", (dialog, which) -> {
                    testRepository.deleteTestById(test.getId());
                    Intent intentProfile;
                    if (Authentication.student != null) {
                        intentProfile = new Intent(this, StudentProfileActivity.class);
                    } else {
                        intentProfile = new Intent(this, TeacherProfileActivity.class);
                    }
                    startActivity(intentProfile);
                    finish();
                })
                .setNegativeButton("Продолжить", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }



    private String generateRoomNumber() {
        return String.valueOf(System.currentTimeMillis()).substring(8);
    }



    public void ClickOnRoom(View view) {
        Test currentTest = Select.getTest();
        if (currentTest == null) {
            Toast.makeText(this, "Тест не выбран.", Toast.LENGTH_SHORT).show();
            return;
        }

        Room room = new Room();
        room.setTestId(currentTest.getId());
        room.setTestName(currentTest.getTitle());
        room.setRoomNumber(generateRoomNumber());


        roomRepository.addRoom(room);

        Intent intent = new Intent(this, RoomCodeActivity.class);
        intent.putExtra("ROOM_CODE", room.getRoomNumber());
        intent.putExtra("TEST_NAME", room.getTestName());
        startActivity(intent);
    }



}