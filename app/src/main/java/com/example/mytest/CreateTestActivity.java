package com.example.mytest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        test = Select.getTest();
        boolean isCompleteMode = getIntent().getBooleanExtra("isCompleteMode", false);

        if (Authentication.getStudent() == null ) {
            findViewById(R.id.btnPublishTest).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btnPublishTest).setVisibility(View.GONE);
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
    public void onBackButtonClick(View view) {
        Intent intentProfile;
        if (Authentication.student != null) {
            intentProfile = new Intent(this, StudentProfileActivity.class);
        } else {
            intentProfile = new Intent(this, TeacherProfileActivity.class);
        }
        startActivity(intentProfile);
        finish();
    }

    private String generateRoomNumber() {
        // Генерация уникального номера комнаты
        return String.valueOf(System.currentTimeMillis()).substring(8); // Возвращает последние 5 символов текущего времени
    }



    public void ClickOnRoom(View view) {
        // Получение текущего теста
        Test currentTest = Select.getTest();
        if (currentTest == null) {
            Toast.makeText(this, "Тест не выбран.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создание новой комнаты
        Room room = new Room();
        room.setTestId(currentTest.getId()); // Установка ID теста
        room.setTestName(currentTest.getTitle()); // Установка названия теста
        room.setRoomNumber(generateRoomNumber()); // Генерация кода комнаты


        // Добавление комнаты в базу данных
        roomRepository.addRoom(room);

        // Передача данных в RoomCodeActivity
        Intent intent = new Intent(this, RoomCodeActivity.class);
        intent.putExtra("ROOM_CODE", room.getRoomNumber());
        intent.putExtra("TEST_NAME", room.getTestName());
        startActivity(intent);
    }



}