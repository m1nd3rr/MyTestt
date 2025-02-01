package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.QuestionStudentAdapter;
import com.example.mytest.model.Question;
import com.example.mytest.repository.QuestionRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class QuestionListActivity extends AppCompatActivity {
    private QuestionStudentAdapter questionAdapter;
    private List<Question> questionList = new ArrayList<>();
    private QuestionRepository questionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_bottom);

        RecyclerView rvQuestionsList = findViewById(R.id.rvTestList);

        String testId = getIntent().getStringExtra("testId"); // Получение testId

        if (testId != null) {
            questionRepository = new QuestionRepository(FirebaseFirestore.getInstance());
            questionRepository.getAllQuestionByTestId(testId)
                    .thenAccept(list -> {
                        questionList.addAll(list);
                        questionAdapter = new QuestionStudentAdapter(questionList, this);
                        rvQuestionsList.setLayoutManager(new LinearLayoutManager(this));
                        rvQuestionsList.setAdapter(questionAdapter);
                    });
        }
    }

    public void onBackk(View view) {
        Intent intent = new Intent(this,TestDetailActivity.class);
        startActivity(intent);
        finish();
    }
}

