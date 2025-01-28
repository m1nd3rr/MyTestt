package com.example.mytest.repository;

import com.example.mytest.model.Admin;
import com.example.mytest.model.Teacher;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.CompletableFuture;

public class AdminRepository {
    private final CollectionReference adminCollection;


    public AdminRepository(FirebaseFirestore db) {
        adminCollection = db.collection("admin");
    }

    public void addAdmin(Admin admin) {
        String adminId = adminCollection.document().getId();
        admin.setId(adminId);
        adminCollection.document(adminId).set(admin);
    }
    public CompletableFuture<Admin> getAdminByLogin(String login) {
        CompletableFuture<Admin> future = new CompletableFuture<>();

        adminCollection.whereEqualTo("login",login).get().addOnCompleteListener(task -> {
            if(task.getResult().isEmpty()) future.complete(null);

            for (QueryDocumentSnapshot document : task.getResult()) {
                Admin admin = document.toObject(Admin.class);
                if (admin.getLogin().equals(login)) future.complete(admin);
            }
        });

        return future;
    }
}
