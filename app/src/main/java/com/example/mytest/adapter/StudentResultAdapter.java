package com.example.mytest.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.model.Result;
import com.example.mytest.repository.StudentRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StudentResultAdapter extends RecyclerView.Adapter<StudentResultAdapter.ResultViewHolder> {

    private final List<Result> results;
    private final Context context;

    public StudentResultAdapter(List<Result> results, Context context) {
        this.results = results;
        this.context = context;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Result result = results.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {

        private final TextView studentNameTextView;
        private final TextView resultTextView;
        private  final TextView groupTextView;
        private StudentRepository studentRepository;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            studentNameTextView = itemView.findViewById(R.id.tvStudentName);
            groupTextView = itemView.findViewById(R.id.tvStudentGroup);
            resultTextView = itemView.findViewById(R.id.tvStudentResult);
            studentRepository = new StudentRepository(FirebaseFirestore.getInstance());
        }

        public void bind(Result result) {
            studentRepository.getStudentById(result.getUserId()).thenAccept(student -> {
                studentNameTextView.setText(student.getFirstName() + " " + student.getLastName());
                groupTextView.setText("Номер группы: " + student.getGroupNumber());

                if (!result.isCompleted()) {
                    resultTextView.setText("В процессе");
                    resultTextView.setTextColor(Color.RED); // Красный для "В процессе"
                } else {
                    resultTextView.setText("Результат: " + result.getCorrectAnswer() + "/" + result.getCountAnswer());
                    resultTextView.setTextColor(Color.GREEN); // Зеленый для завершенного
                }
            });
        }

    }
}
