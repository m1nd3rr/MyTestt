package com.example.mytest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mytest.R;
import com.example.mytest.model.Answer;
import com.example.mytest.model.Question;

import java.util.List;

public class TestResultsAdapter extends RecyclerView.Adapter<TestResultsAdapter.ViewHolder> {

    public static class ResultItem {
        private Question question;
        private List<Answer> correctAnswers;
        private List<Answer> studentAnswers;

        public Question getQuestion() {
            return question;
        }

        public void setQuestion(Question question) {
            this.question = question;
        }

        public List<Answer> getCorrectAnswers() {
            return correctAnswers;
        }

        public void setCorrectAnswers(List<Answer> correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        public List<Answer> getStudentAnswers() {
            return studentAnswers;
        }

        public void setStudentAnswers(List<Answer> studentAnswers) {
            this.studentAnswers = studentAnswers;
        }
    }

    private final List<ResultItem> results;

    public TestResultsAdapter(List<ResultItem> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResultItem item = results.get(position);

        holder.tvQuestion.setText(item.getQuestion().getTitle());
        StringBuilder answersText = new StringBuilder();
        StringBuilder answersTextStudent = new StringBuilder();

        for (Answer correctAnswer : item.getCorrectAnswers()) {
            answersText.append("Правильный ответ: ").append(correctAnswer.getContent()).append("\n");
        }

        for (Answer studentAnswer : item.getStudentAnswers()) {
            answersTextStudent.append("Ответ студента: ").append(studentAnswer.getContent()).append("\n");
        }

        holder.tvAnswers.setText(answersText.toString());
        holder.tvAnswersStudent.setText(answersTextStudent.toString());

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswers,tvAnswersStudent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);
            tvAnswers = itemView.findViewById(R.id.tvAnswers);
            tvAnswersStudent = itemView.findViewById(R.id.tvAnswersStudent);
        }
    }
}
