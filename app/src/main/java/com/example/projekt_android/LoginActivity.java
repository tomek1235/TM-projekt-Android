package com.example.projekt_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etLogin = findViewById(R.id.etLogin);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.GoToRegister);

        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String haslo = etPassword.getText().toString();

            if (login.isEmpty() || haslo.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService.login(login, haslo, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(String result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            boolean success = json.getBoolean("success");

                            if (success) {
                                int userId = json.getInt("userId");
                                String userLogin = json.getString("login");

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("username", userLogin);
                                intent.putExtra("userId", userId);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = json.optString("message", "Błędny login lub hasło");
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Błąd odpowiedzi", Toast.LENGTH_SHORT).show();
                        }
                }

                @Override
                public void onError(String error) {
                            Toast.makeText(LoginActivity.this, "Błąd połączenia: " + error, Toast.LENGTH_LONG).show();

                }
            });
        });

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}