package com.example.mytest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.AllTestsActivity;
import com.example.mytest.R;
import com.example.mytest.model.Test;

import java.util.List;

public class TestsAdminAdapter extends RecyclerView.Adapter<TestsAdminAdapter.TestViewHolder> {

    private List<Test> testList;

    public TestsAdminAdapter(List<Test> testList, Object o, AllTestsActivity allTestsActivity, boolean b) {
        this.testList = testList;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test, parent, false);
        return new TestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        Test test = testList.get(position);
        holder.testNameTextView.setText(test.getTitle());
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    public void updateTestList(List<Test> newTestList) {
        this.testList = newTestList;
        notifyDataSetChanged();
    }

    public static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView testNameTextView;

        public TestViewHolder(View itemView) {
            super(itemView);
            testNameTextView = itemView.findViewById(R.id.first_name);
        }
    }
}
