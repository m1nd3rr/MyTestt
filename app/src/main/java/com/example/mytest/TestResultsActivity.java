package com.example.mytest;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.TestResultsAdapter;
import com.example.mytest.model.Answer;
import com.example.mytest.model.Question;
import com.example.mytest.model.Result;
import com.example.mytest.repository.AnswerRepository;
import com.example.mytest.repository.QuestionRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TestResultsActivity extends AppCompatActivity {
    private QuestionRepository questionRepository;
    private AnswerRepository answerRepository;
    private List<Question> questionList = new ArrayList<>();
    private TestResultsAdapter resultsAdapter;
    private RecyclerView recyclerView;
    private List<TestResultsAdapter.ResultItem> resultItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);


//        TextView tvTestTitle = findViewById(R.id.tvTestTitle);
//        recyclerView = findViewById(R.id.rvResults);
//
//        String testId = getIntent().getStringExtra("testId");
//        String testTitle = getIntent().getStringExtra("testTitle");
//
//        tvTestTitle.setText(testTitle);
//
//        questionRepository = new QuestionRepository(FirebaseFirestore.getInstance());
//        answerRepository = new AnswerRepository(FirebaseFirestore.getInstance());
//
//        // Получаем результаты теста из Intent
//        Result testResult = (Result) getIntent().getSerializableExtra("testResult");
//        List<List<String>> studentAnswers = testResult.getStudentAnswers();
//        // Загружаем вопросы и ответы
//        questionRepository.getAllQuestionByTestId(testId)
//                .thenCompose(questions -> {
//                    List<CompletableFuture<Void>> futures = new ArrayList<>();
//                    for (Question question : questions) {
//                        CompletableFuture<Void> future = answerRepository.getCorrectAnswerByQuestionId(question.getId())
//                                .thenAccept(correctAnswers -> {
//                                    List<Answer> selectedAnswers = new ArrayList<>();
//                                    for (List<String> answerId : studentAnswers) {
//                                        for (Answer correctAnswer : correctAnswers) {
//                                            if (correctAnswer.getId().equals(answerId)) {
//                                                selectedAnswers.add(correctAnswer);
//                                            }
//                                        }
//                                    }
//                                    TestResultsAdapter.ResultItem item = new TestResultsAdapter.ResultItem();
//                                    item.setQuestion(question);
//                                    item.setCorrectAnswers(correctAnswers);
//                                    item.setStudentAnswers(selectedAnswers);
//                                    resultItems.add(item);
//                                });
//                        futures.add(future);
//                    }
//                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//                })
//                .thenRun(() -> {
//                    resultsAdapter = new TestResultsAdapter(resultItems);
//                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
//                    recyclerView.setAdapter(resultsAdapter);
//                });
  }
}
