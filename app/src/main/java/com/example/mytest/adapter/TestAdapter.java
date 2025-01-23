package com.example.mytest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.CompletedTestActivity;
import com.example.mytest.CreateTestActivity;
import com.example.mytest.PassingTestActivity;
import com.example.mytest.R;
import com.example.mytest.TestHistory;
import com.example.mytest.auth.Authentication;
import com.example.mytest.auth.Select;
import com.example.mytest.model.Result;
import com.example.mytest.model.Test;
import com.example.mytest.repository.TestRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestViewHolder> {
    private final List<Test> testList;
    private final List<Result> resultList;
    private final Context context;
    private final boolean isCompleteMode;

    public TestAdapter(List<Test> testList,List<Result> resultList, Context context, boolean isCompleteMode) {
        this.testList = testList;
        this.context = context;
        this.isCompleteMode = isCompleteMode;
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Выбираем разметку на основе режима (завершённые тесты или обычные)
        int layoutId = isCompleteMode ? R.layout.complete_test_item_list : R.layout.test_item_list;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new TestViewHolder(view, context, this, isCompleteMode);
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        Test test = testList.get(position);
        Result result;
        if(resultList != null){
             result = resultList.get(position);
        }else{
            result = null;
        }

        holder.bind(test,result);

    }

    @Override
    public int getItemCount() {
        return testList.size();
    }

    static class TestViewHolder extends RecyclerView.ViewHolder {
        private final TextView text, number;
        private final LinearLayout layout;
        private final ImageView imageView;
        private final Context context;
        private final TestAdapter adapter;
        private final boolean isCompleteMode;

        public TestViewHolder(@NonNull View itemView, Context context, TestAdapter adapter, boolean isCompleteMode) {
            super(itemView);
            text = itemView.findViewById(R.id.test_title);
            number = itemView.findViewById(R.id.test_number);
            layout = itemView.findViewById(R.id.test_item_list);
            imageView = itemView.findViewById(R.id.notificationIcon);
            this.context = context;
            this.adapter = adapter;
            this.isCompleteMode = isCompleteMode;
        }

        public void bind(Test test,Result result) {
            number.setText(String.valueOf(getAdapterPosition() + 1));
            text.setText(test.getTitle());

            // Обработчик клика для элемента списка
            layout.setOnClickListener(view -> {
                Intent intent;
                if (isCompleteMode) {
                    // Если режим завершённых тестов
                    intent = new Intent(context, CompletedTestActivity.class);
                    Select.setResult(result);
                } else if (Authentication.getStudent() != null && !(context instanceof TestHistory)) {
                    // Если пользователь — студент, и это не TestHistory
                    intent = new Intent(context, PassingTestActivity.class);
                } else {
                    // Режим по умолчанию — создание теста
                    intent = new Intent(context, CreateTestActivity.class);
                }
                Select.setTest(test);
                context.startActivity(intent);
            });

            // Обработчик клика для иконки удаления (если доступно)
            if (!isCompleteMode) {
                imageView.setOnClickListener(view -> {
                    TestRepository testRepository = new TestRepository(FirebaseFirestore.getInstance());
                    testRepository.deleteTest(test);
                    int position = getAdapterPosition();
                    adapter.removeTest(position);
                });
            } else {
                imageView.setVisibility(View.GONE); // Скрыть иконку в режиме завершённых тестов
            }
        }
    }

    public void removeTest(int position) {
        testList.remove(position);
        notifyItemRemoved(position);
    }
}
