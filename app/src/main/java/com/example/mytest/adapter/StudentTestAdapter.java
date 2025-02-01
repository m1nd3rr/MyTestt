package com.example.mytest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.model.Test;

import java.util.ArrayList;
import java.util.List;

public class StudentTestAdapter extends RecyclerView.Adapter<StudentTestAdapter.ItemTestsViewHolder> {
    private List<Test> tests;
    private Context context;

    // Конструктор адаптера
    public StudentTestAdapter(List<Test> tests, Context context) {
        this.tests = tests != null ? tests : new ArrayList<>();
        this.context = context;
    }

    @Override
    public ItemTestsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Убедитесь, что контекст не null
        if (context == null) {
            throw new IllegalStateException("Context is null");
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_tests, parent, false);
        return new ItemTestsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemTestsViewHolder holder, int position) {
        // Получаем тест
        Test test = tests.get(position);
        holder.testTitle.setText(test.getTitle());
        // Устанавливаем название теста
        // Можно добавить дополнительные данные, если они есть, например, автор, количество вопросов и т.д.
    }

    @Override
    public int getItemCount() {
        return tests.size();  // Количество тестов в списке
    }

    // ViewHolder для item_tests
    public class ItemTestsViewHolder extends RecyclerView.ViewHolder {
        TextView testTitle;
        TextView questionCount;

        public ItemTestsViewHolder(View itemView) {
            super(itemView);
            testTitle = itemView.findViewById(R.id.test_title);
        }
    }
}
