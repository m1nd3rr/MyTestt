package com.example.mytest.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.R;
import com.example.mytest.model.Answer;
import com.example.mytest.model.Question;
import com.example.mytest.repository.AnswerRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {
    private final List<Answer> answerList;
    private final Context context;
    private final Question question;

    public AnswerAdapter(List<Answer> answerList, Context context, Question question) {
        this.answerList = answerList;
        this.context = context;
        this.question = question;
    }

    @NonNull
    @Override
    public AnswerAdapter.AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.answer_item_list, parent, false);
        return new AnswerAdapter.AnswerViewHolder(view, this, answerList, question);
    }
    @Override
    public void onBindViewHolder(@NonNull AnswerAdapter.AnswerViewHolder holder, int position) {
        Answer answer = answerList.get(position);
        holder.bind(answer);
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }
    static class AnswerViewHolder extends RecyclerView.ViewHolder {
        private final TextView text,number;
        private final AnswerRepository answerRepository;
        private final AnswerAdapter answerAdapter;
        private final List<Answer> answerList;
        private final Question question;
        private final CheckBox checkBox;
        public AnswerViewHolder(@NonNull View itemView, AnswerAdapter answerAdapter, List<Answer> answerList, Question question) {
            super(itemView);
            text = itemView.findViewById(R.id.answer_title);
            this.answerAdapter = answerAdapter;
            this.answerList = answerList;
            this.question = question;
            this.checkBox = itemView.findViewById(R.id.dialog_answer_checkBox);
            answerRepository = new AnswerRepository(FirebaseFirestore.getInstance());
            number = itemView.findViewById(R.id.answer_item);
        }

        public void bind(Answer answer) {
            text.setText(answer.getContent());
            number.setText(String.valueOf(getAdapterPosition() + 1));
            text.setText(answer.getContent());
            switch (question.getType()) {
                case "multi-choice":
                    multiChoose(answer);
                    break;
                case "single-choice":
                    singleChoose(answer);
                    break;
                case "true-false":
                    trueFalse(answer);
                    break;
                case "sort":
                    sort(answer);
                    break;
                case "text":
                    text(answer);
                    break;
                case "connect":
                    connect(answer);
                    break;
            }
        }

        private void multiChoose(Answer answer) {
            text.setOnClickListener(v -> {
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_answer, null);

                EditText editText = dialogView.findViewById(R.id.dialog_answer_editText);
                CheckBox checkBox = dialogView.findViewById(R.id.dialog_answer_checkBox);

                editText.setText(answer.getContent());
                checkBox.setChecked(answer.isCorrect());

                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Отредактируйте ответ")
                        .setView(dialogView)
                        .setPositiveButton("Обновить", (dialog1, which) -> {
                            answer.setContent(editText.getText().toString());
                            answer.setCorrect(checkBox.isChecked());

                            answerRepository.updateAnswer(answer);
                            answerAdapter.notifyItemChanged(getAdapterPosition());
                        })
                        .setNegativeButton("Удалить", (dialog12, which) -> {
                            answerList.remove(getAdapterPosition());
                            answerRepository.deleteAnswer(answer);
                            answerAdapter.notifyItemRemoved(getAdapterPosition());
                        })
                        .create();

                dialog.show();
            });
        }

        private void singleChoose(Answer answer) {
            text.setOnClickListener(v -> {
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_answer, null);

                EditText editText = dialogView.findViewById(R.id.dialog_answer_editText);
                CheckBox checkBox = dialogView.findViewById(R.id.dialog_answer_checkBox);

                editText.setText(answer.getContent());
                checkBox.setChecked(answer.isCorrect());

                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Отредактируйте ответ")
                        .setView(dialogView)
                        .setPositiveButton("Обновить", (dialog1, which) -> {
                            answerRepository.checkCorrectAnswer(question.getId())
                                    .thenAccept(correct -> {
                                        if (correct || !checkBox.isChecked()) {
                                            answer.setContent(editText.getText().toString());
                                            answer.setCorrect(checkBox.isChecked());

                                            answerRepository.updateAnswer(answer);
                                            answerAdapter.notifyItemChanged(getAdapterPosition());
                                        }
                                    });
                        })
                        .setNegativeButton("Удалить", (dialog12, which) -> {
                            answerList.remove(getAdapterPosition());
                            answerRepository.deleteAnswer(answer);
                            answerAdapter.notifyItemRemoved(getAdapterPosition());
                        })
                        .create();

                dialog.show();
            });
        }

        private void sort(Answer answer) {
            text.setOnClickListener(v -> {
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_sort, null);

                EditText editText = dialogView.findViewById(R.id.dialog_answer_editText);
                Spinner spinner = dialogView.findViewById(R.id.dialog_answer_sort_number);

                editText.setText(answer.getContent());

                ArrayAdapter<Integer> adapter = new ArrayAdapter<>(
                        v.getContext(),
                        android.R.layout.simple_spinner_item,
                        getSortNumbers()
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(adapter.getPosition(answer.getSortNumber()));


                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Отредактируйте ответ")
                        .setView(dialogView)
                        .setPositiveButton("Обновить", (dialog1, which) -> {
                            answer.setContent(editText.getText().toString());
                            answer.setSortNumber((Integer) spinner.getSelectedItem());

                            answerRepository.updateAnswer(answer);
                            answerAdapter.notifyItemChanged(getAdapterPosition());
                        })
                        .setNegativeButton("Удалить", (dialog12, which) -> {
                            answerList.remove(getAdapterPosition());
                            answerRepository.deleteAnswer(answer);
                            answerAdapter.notifyItemRemoved(getAdapterPosition());
                        })
                        .create();
                dialog.show();
            });
        }

        private List<Integer> getSortNumbers() {
            List<Integer> sortNumbers = new ArrayList<>();
            for (int i = 1; i <= answerList.size(); i++) {
                sortNumbers.add(i);
            }
            return sortNumbers;
        }

        private void text(Answer answer) {
            text.setOnClickListener(v -> {
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View dialogView = inflater.inflate(R.layout.diaolog_text, null);

                EditText editText = dialogView.findViewById(R.id.dialog_answer_editText);

                editText.setText(answer.getContent());

                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Отредактируйте ответ")
                        .setView(dialogView)
                        .setPositiveButton("Обновить", (dialog1, which) -> {
                            answer.setContent(editText.getText().toString());

                            answerRepository.updateAnswer(answer);
                            answerAdapter.notifyItemChanged(getAdapterPosition());
                        })
                        .setNegativeButton("Удалить", (dialog12, which) -> {
                            answerList.remove(getAdapterPosition());
                            answerRepository.deleteAnswer(answer);
                            answerAdapter.notifyItemRemoved(getAdapterPosition());
                        })
                        .create();

                dialog.show();
            });
        }

        private void connect(Answer answer) {
            text.setOnClickListener(v -> {
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                View dialogView = inflater.inflate(R.layout.dialog_connect, null);

                EditText editText = dialogView.findViewById(R.id.dialog_answer_editTex);
                EditText editText1 = dialogView.findViewById(R.id.dialog_answer_editText2);

                editText.setText(answer.getContent());
                editText1.setText(answer.getText());

                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Отредактируйте ответ")
                        .setView(dialogView)
                        .setPositiveButton("Обновить", (dialog1, which) -> {
                            answer.setContent(editText.getText().toString());
                            answer.setText(editText1.getText().toString());

                            answerRepository.updateAnswer(answer);
                            answerAdapter.notifyItemChanged(getAdapterPosition());
                        })
                        .setNegativeButton("Удалить", (dialog12, which) -> {
                            answerList.remove(getAdapterPosition());
                            answerRepository.deleteAnswer(answer);
                            answerAdapter.notifyItemRemoved(getAdapterPosition());
                        })
                        .create();

                dialog.show();
            });
        }
        private void trueFalse(Answer answer){
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(answer.isCorrect());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    answerRepository.checkCorrectAnswer(question.getId()).thenAccept(mama ->{
                        if (mama ) {
                                answer.setCorrect(isChecked);
                                answerRepository.updateAnswer(answer);

                        }else {
                            answer.setCorrect(false);
                            answerRepository.updateAnswer(answer);
                            checkBox.setChecked(false);
                        }
                    });
                }
            });
        }
    }
}
