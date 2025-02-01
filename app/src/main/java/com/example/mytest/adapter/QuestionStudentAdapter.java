package com.example.mytest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.model.Question;
import com.example.mytest.R;

import java.util.List;

public class QuestionStudentAdapter extends RecyclerView.Adapter<QuestionStudentAdapter.QuestionViewHolder> {
    private final List<Question> questionList;
    private final Context context;
    //private final boolean isCompleteMode;

    public QuestionStudentAdapter(List<Question> questionList, Context context) {
        this.questionList = questionList;
        this.context = context;
        //this.isCompleteMode = isCompleteMode;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //int layoutId = isCompleteMode ? R.layout.complete_question_item_list : R.layout.question_item_list;
        //View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        //return new QuestionViewHolder(view, context, isCompleteMode);

        View view = LayoutInflater.from(context).inflate(R.layout.complete_question_item_list, parent, false);
        return new QuestionStudentAdapter.QuestionViewHolder(view,questionList,context);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final TextView text, number;
        private ImageView icon;
        //private final boolean isCompleteMode;
        private final Context context;

        public QuestionViewHolder(@NonNull View itemView, List<Question> questionList, Context context) {
            super(itemView);
            //this.isCompleteMode = isCompleteMode;
            this.context = context;
            text = itemView.findViewById(R.id.question_title);
            number = itemView.findViewById(R.id.question_number);


        }

        public void bind(Question question) {
            number.setText(String.valueOf(getAdapterPosition() + 1));
            text.setText(question.getTitle());
        }
    }
}
