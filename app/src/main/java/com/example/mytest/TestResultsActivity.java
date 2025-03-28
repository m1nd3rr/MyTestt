package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.TestResultsAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.repository.QuestionRepository;
import com.google.firebase.firestore.FirebaseFirestore;

public class TestResultsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TestResultsAdapter adapter;
    private QuestionRepository questionRepository;
    private TextView tvCorrectAnswers, tvIncorrectAnswers;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);

        recyclerView = findViewById(R.id.rvResults);
        TextView tvTestTitle = findViewById(R.id.tvTestTitle);

        tvTestTitle.setText(Select.getTest().getTitle());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        tvIncorrectAnswers = findViewById(R.id.tvIncorrectAnswers);

        questionRepository = new QuestionRepository(FirebaseFirestore.getInstance());

        questionRepository.getAllQuestionByTestId(Select.getTest().getId()).thenAccept(list -> {
            adapter = new TestResultsAdapter(this, list);
            recyclerView.setAdapter(adapter);
        });
    }

    public void updateResults(int correctCount, int incorrectCount) {
        tvCorrectAnswers.setText("Верные ответы: " + correctCount);
        tvIncorrectAnswers.setText("Ошибки: " + incorrectCount);
    }

    public void backbutton(View view) {
        Intent intent = new Intent(this, StudentProfileActivity.class);
        startActivity(intent);
        finish();
    }
}

