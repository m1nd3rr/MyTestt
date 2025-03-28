package com.example.mytest.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.model.Report;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ComplaintViewHolder> {
    private List<Report> reportList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Report report);
    }

    public ReportAdapter(List<Report> reportList, OnItemClickListener listener) {
        this.reportList = reportList;
        this.listener = listener;
    }
    public void updateList(List<Report> newList) {
        reportList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.bind(report, listener);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public Report getReportAt(int position) {
        return reportList.get(position);
    }

    static class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView textComplaint, textDate;

        ComplaintViewHolder(@NonNull View itemView) {
            super(itemView);
            textComplaint = itemView.findViewById(R.id.textComplaint);
            textDate = itemView.findViewById(R.id.textDate);
        }

        public void bind(Report report, OnItemClickListener listener) {
            textComplaint.setText(report.getNameReport());
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            textDate.setText(sdf.format(report.getTimestamp()));

            itemView.setOnClickListener(v -> listener.onItemClick(report));
        }
    }
}
