package com.example.mytest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.TestResultsActivity;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Answer;
import com.example.mytest.model.Question;
import com.example.mytest.repository.AnswerRepository;
import com.example.mytest.repository.ResultRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TestResultsAdapter extends RecyclerView.Adapter<TestResultsAdapter.ViewHolder> {
    private final Context context;
    private final List<Question> questions;
    private int correctAnswersCount = 0;
    private int incorrectAnswersCount = 0;

    public TestResultsAdapter(Context context, List<Question> questions) {
        this.context = context;
        this.questions = questions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public int getCorrectAnswersCount() {
        return correctAnswersCount;
    }

    public int getIncorrectAnswersCount() {
        return incorrectAnswersCount;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionTitle, studentAnswer, correctAnswer;
        private final AnswerRepository answerRepository;
        private final ResultRepository resultRepository;

        public ViewHolder(View itemView) {
            super(itemView);
            questionTitle = itemView.findViewById(R.id.tvQuestion);
            studentAnswer = itemView.findViewById(R.id.tvAnswers);
            correctAnswer = itemView.findViewById(R.id.tvAnswersStudent);
            answerRepository = new AnswerRepository(FirebaseFirestore.getInstance());
            resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
        }

        public void bind(Question question) {
            String questionId = question.getId();
            questionTitle.setText(question.getTitle());

            answerRepository.getCorrectAnswerByQuestionId(questionId).thenAccept(answerList -> {
                resultRepository.getStudentResultByTestId(questionId, Authentication.getStudentId(), Select.getResult().getId()).thenAccept(aBoolean -> {
                    correctAnswer.setText("Правильный ответ: ");
                    for (Answer answer : answerList) {
                        if (answer.isCorrect()) {
                            if (answer.getContent() != null) {
                                correctAnswer.setText(correctAnswer.getText() + answer.getContent() + ", ");
                            }
                        }
                    }

                    // Проверка правильности ответа
                    ImageView statusIcon = itemView.findViewById(R.id.ivStatusIcon);
                    if (aBoolean) {
                        studentAnswer.setText("Верно");
                        studentAnswer.setTextColor(context.getResources().getColor(R.color.green));
                        statusIcon.setImageResource(R.drawable.complete);
                        correctAnswersCount++;
                    } else {
                        studentAnswer.setText("Ответ неверен");
                        studentAnswer.setTextColor(context.getResources().getColor(R.color.red));
                        statusIcon.setImageResource(R.drawable.non);
                        incorrectAnswersCount++;
                    }
                    statusIcon.setVisibility(View.VISIBLE);

                    if (context instanceof TestResultsActivity) {
                        TestResultsActivity activity = (TestResultsActivity) context;
                        activity.updateResults(correctAnswersCount, incorrectAnswersCount);
                    }
                });
            });
        }
    }
}


