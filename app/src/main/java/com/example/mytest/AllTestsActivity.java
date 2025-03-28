package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.TestAdapter;
import com.example.mytest.model.Test;
import com.example.mytest.repository.TestRepository;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.List;


public class AllTestsActivity extends BaseActivity {
    private TestRepository testRepository;
    private RecyclerView recyclerView;
    private TestAdapter adapter;
    private EditText searchInput;
    private List<Test> originalTestList; // Список всех тестов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_tests_activity);

        searchInput = findViewById(R.id.search_input);
        testRepository = new TestRepository(FirebaseFirestore.getInstance());
        recyclerView = findViewById(R.id.recycler_view_tests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTests();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterTests(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void loadTests() {
        testRepository.getAllTest().thenAccept(testList -> {
            originalTestList = testList;
            adapter = new TestAdapter(testList, null, this, false);
            recyclerView.setAdapter(adapter);
        }).exceptionally(throwable -> {
            Toast.makeText(this, "Ошибка загрузки тестов", Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    private void filterTests(String query) {
        List<Test> filteredList = new ArrayList<>();
        for (Test test : originalTestList) {
            if (test.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(test);
            }
        }
        adapter.updateList(filteredList);
    }

    public void OnBack(View view) {
        Intent intent = new Intent(AllTestsActivity.this, AdminProfile.class);
        startActivity(intent);
        finish();
    }
}
