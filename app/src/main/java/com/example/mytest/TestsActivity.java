package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.TestAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.model.Test;
import com.example.mytest.repository.TestRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TestsActivity extends AppCompatActivity {
    List<Test> testList = new ArrayList<>();
    TestRepository testRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean isCompleteMode;
        if(getIntent().getStringExtra("button" )!= null){
            isCompleteMode= getIntent().getStringExtra("button").equals("complete");
        }else {
            isCompleteMode= false;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
        testRepository = new TestRepository(FirebaseFirestore.getInstance());
        testRepository.getAllStudentTest()
                .thenAccept(list -> {
                    testList.addAll(list);
                    RecyclerView recyclerView = findViewById(R.id.recyclerViewTests);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    TestAdapter adapter = new TestAdapter(testList,null, this, isCompleteMode);
                    recyclerView.setAdapter(adapter);
                });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_search){
                return true;
            }
            else if (item.getItemId() == R.id.navigation_home){
                Intent intent = new Intent(this, StudentProfileActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}