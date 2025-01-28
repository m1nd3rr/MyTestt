package com.example.mytest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.model.Teacher;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {
    private final List<Teacher> teacherList;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Teacher teacher);
    }

    public TeacherAdapter(List<Teacher> teacherList, OnDeleteClickListener deleteClickListener) {
        this.teacherList = teacherList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teacherList.get(position);
        holder.firstName.setText("Имя: " + teacher.getFirstName());
        holder.lastName.setText("Фамилия: " + teacher.getLastName());

        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(teacher));
    }

    @Override
    public int getItemCount() {
        return teacherList.size();
    }

    public void removeTeacher(Teacher teacher) {
        int position = teacherList.indexOf(teacher);
        if (position != -1) {
            teacherList.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView firstName, lastName, subject;
        Button deleteButton;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.first_name);
            lastName = itemView.findViewById(R.id.last_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
