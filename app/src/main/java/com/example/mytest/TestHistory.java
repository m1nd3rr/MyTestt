package com.example.mytest;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.TestAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.model.Test;
import com.example.mytest.repository.QuestionRepository;
import com.example.mytest.repository.ResultRepository;
import com.example.mytest.repository.RoomRepository;
import com.example.mytest.repository.TestRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TestHistory extends BaseActivity {
    TestRepository testRepository;
    ResultRepository resultRepository;
    Test test;
    QuestionRepository questionRepository;
    RoomRepository roomRepository;
    private EditText searchInput;
    private List<Test> originalTests;
    private  TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_history);

        boolean isCompleteMode = getIntent().getBooleanExtra("isCompleteMode", false);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTests);
        resultRepository = new ResultRepository(FirebaseFirestore.getInstance());
        testRepository = new TestRepository(FirebaseFirestore.getInstance());
        List<Test> testList = new ArrayList<>();
        searchInput = findViewById(R.id.search_input);
        questionRepository = new QuestionRepository(FirebaseFirestore.getInstance());
        roomRepository = new RoomRepository(FirebaseFirestore.getInstance());

        if (Authentication.getStudent() != null) {
            if(getIntent().getBooleanExtra("isCompleteMode", false)){
                resultRepository.getAllResultByStudentId(Authentication.student.getId()).thenAccept(list ->{
                    testRepository.getAllTestByResult(list).thenAccept(listTest ->{
                        originalTests = listTest;
                        testList.addAll(listTest);
                        adapter = new TestAdapter(testList,list, this,isCompleteMode);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    });
                });
            }
            else {
                testRepository.getAllTestByStudentId(Authentication.student.getId()).thenAccept(list ->{
                    originalTests = list;
                    testList.addAll(list);
                    adapter = new TestAdapter(testList,null, this,isCompleteMode);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                });
            }
        }else {
            testRepository.getAllTestByTeacherId(Authentication.getTeacher().getId())
                    .thenAccept(list -> {
                        originalTests = list;
                        testList.addAll(list);
                        adapter = new TestAdapter(testList,null, this,isCompleteMode);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    });
        }
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
    private void filterTests(String query) {
        List<Test> filteredList = new ArrayList<>();
        for (Test test : originalTests) {
            if (test.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(test);
            }
        }
        adapter.updateList(filteredList);
    }
    public void onBackButtonClickk(View view) {
        Intent intent;
        if(Authentication.getTeacher() != null){
            intent = new Intent(this, TeacherProfileActivity.class);
        } else {
            intent = new Intent(this, StudentProfileActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
