package com.example.mytest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.EditStudentActivity;
import com.example.mytest.EditTeacherActivity;
import com.example.mytest.R;
import com.example.mytest.model.Student;
import com.example.mytest.model.Teacher;

import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {
    private List<Teacher> teacherList;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Teacher teacher);
    }

    public TeacherAdapter(List<Teacher> teacherList, OnDeleteClickListener deleteClickListener) {
        this.teacherList = teacherList;
        this.deleteClickListener = deleteClickListener;
    }
    public void updateList(List<Teacher> newList) {
        teacherList = newList;
        notifyDataSetChanged();
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

        holder.editButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EditTeacherActivity.class);
            intent.putExtra("teacher_id", teacher.getId());
            intent.putExtra("first_name", teacher.getFirstName());
            intent.putExtra("last_name", teacher.getLastName());
            intent.putExtra("email", teacher.getEmail());
            intent.putExtra("password", teacher.getPassword());
            intent.putExtra("photo", teacher.getPhoto());

            context.startActivity(intent);
        });
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
        Button deleteButton, editButton;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.first_name);
            lastName = itemView.findViewById(R.id.last_name);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
