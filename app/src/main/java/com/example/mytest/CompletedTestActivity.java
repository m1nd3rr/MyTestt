package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.QuestionAdapter;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Question;
import com.example.mytest.model.Test;
import com.example.mytest.repository.QuestionRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CompletedTestActivity extends AppCompatActivity {
    private QuestionRepository questionRepository;
    private QuestionAdapter questionAdapter;
    private List<Question> questionList = new ArrayList<>();
    private EditText etTestTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_tests);
        boolean isCompleteMode = true;

        etTestTitle = findViewById(R.id.etTestTitle);
        RecyclerView rvQuestionsList = findViewById(R.id.rvQuestionsList);

        Test selectedTest = Select.getTest(); // Получаем выбранный тест
        if (selectedTest != null) {
            etTestTitle.setText(selectedTest.getTitle());
            etTestTitle.setEnabled(false); // Поле только для чтения
        }

        questionRepository = new QuestionRepository(FirebaseFirestore.getInstance());
        questionRepository.getAllQuestionByTestId(selectedTest.getId())
                .thenAccept(list -> {
                    questionList.addAll(list);
                    questionAdapter = new QuestionAdapter(questionList, this, isCompleteMode);
                    rvQuestionsList.setLayoutManager(new LinearLayoutManager(this));
                    rvQuestionsList.setAdapter(questionAdapter);
                });
    }

    public void onBackButtonClick(View view) {
        finish(); // Возврат на предыдущий экран
    }

    public void ClickOnRoom(View view) {
        // Показывает результаты теста
        //Intent intent = new Intent(this, TestResultsActivity.class); // Новая Activity для отображения результатов
        //startActivity(intent);
    }
}

