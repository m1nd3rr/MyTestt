package com.example.mytest.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.AllStudentsActivity;
import com.example.mytest.EditStudentActivity;
import com.example.mytest.R;
import com.example.mytest.model.Report;
import com.example.mytest.model.Student;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<Student> studentList;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(Student student);
    }

    public StudentAdapter(List<Student> studentList, OnDeleteClickListener deleteClickListener) {
        this.studentList = studentList;
        this.deleteClickListener = deleteClickListener;
    }
    public void updateList(List<Student> newList) {
        studentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.firstName.setText("Имя: " + student.getFirstName());
        holder.lastName.setText("Фамилия: " + student.getLastName());
        holder.groupNumber.setText("Группа: " + student.getGroupNumber());

        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(student));

        holder.editButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EditStudentActivity.class);
            intent.putExtra("student_id", student.getId());
            intent.putExtra("first_name", student.getFirstName());
            intent.putExtra("last_name", student.getLastName());
            intent.putExtra("email", student.getEmail());
            intent.putExtra("password", student.getPassword());
            intent.putExtra("group_number", student.getGroupNumber());
            intent.putExtra("photo", student.getPhoto());

                context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public void removeStudent(Student student) {
        int position = studentList.indexOf(student);
        if (position != -1) {
            studentList.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView firstName, lastName, groupNumber;
        Button deleteButton, editButton;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            firstName = itemView.findViewById(R.id.first_name);
            lastName = itemView.findViewById(R.id.last_name);
            groupNumber = itemView.findViewById(R.id.group_number);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
        }
    }
}
