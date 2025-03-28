package com.example.mytest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.model.Answer;
import com.example.mytest.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PassingAdapter extends RecyclerView.Adapter<PassingAdapter.PassingViewHolder> {
    private final List<Answer> answerList;
    private final Context context;
    private final Question question;
    private List<String> pool = new ArrayList<>();

    public PassingAdapter(List<Answer> answerList, Context context, Question question) {
        this.answerList = answerList;
        this.context = context;
        this.question = question;

        for (int i = 0; i < answerList.size(); i ++) {
            pool.add(answerList.get(i).getText());
        }
        Collections.shuffle(pool);
    }

    @NonNull
    @Override
    public PassingAdapter.PassingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (question.getType()) {
            case "single-choice":
            case "multi-choice":
            case "true-false":
                view = LayoutInflater.from(context).inflate(R.layout.dialog_answer, parent, false);
                break;
            case "sort":
                view = LayoutInflater.from(context).inflate(R.layout.dialog_sort, parent, false);
                break;
            case "connect" :
                view = LayoutInflater.from(context).inflate(R.layout.connect_passing, parent, false);
                break;
            default:
                view = null;
        }

        return new PassingAdapter.PassingViewHolder(view, question, pool, answerList.size());
    }

    @Override
    public void onBindViewHolder(@NonNull PassingAdapter.PassingViewHolder holder, int position) {
        Answer answer = answerList.get(position);
        holder.bind(answer, position);
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public boolean getPassingResult() {
        switch (question.getType()) {
            case "single-choice":
            case "multi-choice":
            case "true-false":
                for (int i = 0; i < answerList.size(); i++) {
                    if (answerList.get(i).isCorrect() != PassingViewHolder.booleanList.get(i))
                        return false;
                }
                break;
            case "sort":
                for (int i = 0; i < answerList.size(); i++) {
                    if (answerList.get(i).getSortNumber() != PassingViewHolder.integerList.get(i))
                        return false;
                }
                break;
            case "connect":
                for (int i = 0; i < answerList.size(); i++) {
                    if (!PassingViewHolder.stringChooseList.get(i).equals(answerList.get(i).getText()))
                        return false;
                }
                break;
        }

        return true;
    }

    public Answer getSelectedAnswer() {
        if (question.getType().equals("single-choice") || question.getType().equals("true-false")) {
            for (int i = 0; i < answerList.size(); i++) {
                if (PassingViewHolder.booleanList.get(i)) {
                    return answerList.get(i); // Возвращаем выбранный ответ
                }
            }
        }
        return null;
    }

    static class PassingViewHolder extends RecyclerView.ViewHolder {
        private final Question question;
        private TextView textTitle;
        private Spinner sortNumber;

        private TextView condition, answerText;
        private Spinner mama;

        private CheckBox checkBox;
        private List<String> pool;

        int size;

        static final List<Boolean> booleanList = new ArrayList<>();
        static final List<Integer> integerList = new ArrayList<>();
        static final List<String> stringChooseList = new ArrayList<>();

        public PassingViewHolder(@NonNull View itemView, Question question, List<String> pool, int size) {
            super(itemView);
            this.question = question;
            this.pool = pool;
            this.size = size;

            booleanList.clear();
            for (int i = 0; i < size; i++) {
                booleanList.add(false);
            }

            stringChooseList.clear();
            for (int i = 0; i < size; i++) {
                stringChooseList.add(null);
            }

            integerList.clear();
            for (int i = 0; i < size; i++) {
                integerList.add(0);
            }

            switch (question.getType()) {
                case "single-choice":
                case "multi-choice":
                case "true-false":
                    textTitle = itemView.findViewById(R.id.dialog_answer_editText);
                    checkBox = itemView.findViewById(R.id.dialog_answer_checkBox);
                    break;
                case "sort":
                    textTitle = itemView.findViewById(R.id.dialog_answer_editText);
                    sortNumber = itemView.findViewById(R.id.dialog_answer_sort_number);
                    break;
                case "connect" :
                    condition = itemView.findViewById(R.id.condition);
                    answerText = itemView.findViewById(R.id.answer);
                    mama = itemView.findViewById(R.id.choose);
            }
        }

        public void bind(Answer answer, int position) {
            switch (question.getType()) {
                case "single-choice":
                case "multi-choice":
                case "true-false":
                    textTitle.setText(answer.getContent());
                    textTitle.setEnabled(false);
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (question.getType().equals("single-choice") || question.getType().equals("true-false")) {
                            if (booleanList.contains(true)) {
                                checkBox.setChecked(false);
                            }
                        }

                        booleanList.set(position, checkBox.isChecked());
                    });
                    break;

                case "sort":
                    textTitle.setText(answer.getContent());
                    textTitle.setEnabled(false);

                    ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                            itemView.getContext(),
                            android.R.layout.simple_spinner_item,
                            getSortNumbers()
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sortNumber.setAdapter(adapter);

                    sortNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                            integerList.set(position, (Integer) parent.getItemAtPosition(p));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    break;

                case "connect" :
                    condition.setText(answer.getContent());
                    answerText.setText(pool.get(position));

                    ArrayAdapter<Integer> adapter2 = new ArrayAdapter<>(
                            itemView.getContext(),
                            android.R.layout.simple_spinner_item,
                            getSortNumbers()
                    );
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mama.setAdapter(adapter2);


                    mama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                            int i = (Integer)parent.getItemAtPosition(p);
                            stringChooseList.set(position , pool.get(i - 1));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
            }
        }

        private List<Integer> getSortNumbers() {
            List<Integer> sortNumbers = new ArrayList<>();
            for (int i = 1; i <= size; i++) {
                sortNumbers.add(i);
            }
            return sortNumbers;
        }
    }
}
