package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytest.adapter.TestAdapter;
import com.example.mytest.adapter.TestsAdminAdapter;
import com.example.mytest.auth.Authentication;
import com.example.mytest.repository.TestRepository;
import com.google.firebase.firestore.FirebaseFirestore;


public class AllTestsActivity extends AppCompatActivity {
    private TestRepository testRepository;
    private RecyclerView recyclerView;
    private TestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_tests_activity);


        testRepository = new TestRepository(FirebaseFirestore.getInstance());
        recyclerView = findViewById(R.id.recycler_view_tests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTests();
    }

    private void loadTests() {
        testRepository.getAllTest().thenAccept(testList -> {
            adapter = new TestAdapter(testList, null, this, false);
            recyclerView.setAdapter(adapter);
        }).exceptionally(throwable -> {
            Toast.makeText(this, "Ошибка загрузки тестов", Toast.LENGTH_SHORT).show();
            return null;
        });
    }

    public void OnBack(View view) {
        Intent intent = new Intent(AllTestsActivity.this, AdminProfile.class);
        startActivity(intent);
        finish();
    }
}
