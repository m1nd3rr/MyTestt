package com.example.mytest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytest.auth.Authentication;
import com.example.mytest.model.Admin;

public class RegisterAdmin extends BaseActivity {

    private EditText loginAdmin, passwordAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_admin);

        loginAdmin = findViewById(R.id.loginAdmin);
        passwordAdmin = findViewById(R.id.paswordAdmin);

        Button buttonLoginAdmin = findViewById(R.id.buttonSubmitAdmin);

        buttonLoginAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = loginAdmin.getText().toString().trim();
                String password = passwordAdmin.getText().toString().trim();
                if (login.equals("admin") && password.equals("admin")) {
                    Admin admin = new Admin();
                    Authentication.setAdmin(admin);
                    Intent intent = new Intent(RegisterAdmin.this, AdminProfile.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterAdmin.this, "Неверные данные для входа", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
