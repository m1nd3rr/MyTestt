package com.example.mytest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.TestDetailActivity;
import com.example.mytest.model.Test;

import java.util.ArrayList;
import java.util.List;

public class StudentTestAdapter extends RecyclerView.Adapter<StudentTestAdapter.TestViewHolder> {
    private List<Test> testList;
    private Context context;

    public StudentTestAdapter(List<Test> testList, Context context) {
        this.testList = testList;
        this.context = context;
    }

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tests, parent, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        Test test = testList.get(position);
        holder.testTitle.setText(test.getTitle());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, TestDetailActivity.class);
            intent.putExtra("test_id", test.getId()); // Передаем ID выбранного теста
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {
        TextView testTitle;

        public TestViewHolder(View itemView) {
            super(itemView);
            testTitle = itemView.findViewById(R.id.test_title);
        }
    }
}
