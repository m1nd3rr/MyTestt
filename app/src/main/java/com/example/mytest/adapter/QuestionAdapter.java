package com.example.mytest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.QuestionActivity;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Question;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private final List<Question> questionList;
    private final Context context;
    private final boolean isCompleteMode;

    public QuestionAdapter(List<Question> questionList, Context context, boolean isCompleteMode) {
        this.questionList = questionList;
        this.context = context;
        this.isCompleteMode = isCompleteMode;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isCompleteMode ? R.layout.complete_question_item_list : R.layout.question_item_list;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new QuestionViewHolder(view, context, isCompleteMode);
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
        private ImageView icon; // Будет null в режиме isCompleteMode
        private final boolean isCompleteMode;
        private final Context context;

        public QuestionViewHolder(@NonNull View itemView, Context context, boolean isCompleteMode) {
            super(itemView);
            this.isCompleteMode = isCompleteMode;
            this.context = context;
            text = itemView.findViewById(R.id.question_title);
            number = itemView.findViewById(R.id.question_number);

            // Устанавливаем icon только в режиме редактирования
            if (!isCompleteMode) {
                icon = itemView.findViewById(R.id.notificationIcons);
            }
        }

        public void bind(Question question) {
            number.setText(String.valueOf(getAdapterPosition() + 1));
            text.setText(question.getTitle());

            // Если режим не завершённый, устанавливаем слушатель для icon
            if (!isCompleteMode && icon != null) {
                icon.setVisibility(View.VISIBLE); // Показываем иконку
                icon.setOnClickListener(view -> {
                    Intent intent = new Intent(view.getContext(), QuestionActivity.class);
                    Select.setQuestion(question);
                    view.getContext().startActivity(intent);
                    ((Activity) view.getContext()).finish();
                });
            } else if (icon != null) {
                icon.setVisibility(View.GONE); // Скрываем иконку
            }
        }
    }
}
