package com.example.projekt_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etLogin = findViewById(R.id.etRegisterLogin);
        EditText etPassword = findViewById(R.id.etRegisterPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        btnRegister.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String haslo = etPassword.getText().toString();
            String confirmHaslo = etConfirmPassword.getText().toString();

            if (login.isEmpty() || haslo.isEmpty() || confirmHaslo.isEmpty()) {
                Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                return;
            }

            if (haslo.length() < 4) {
                Toast.makeText(this, "Hasło musi mieć min. 4 znaki", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!haslo.equals(confirmHaslo)) {
                Toast.makeText(this, "Hasła nie są takie same", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService.register(login, haslo, new ApiService.ApiCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        boolean success = json.getBoolean("success");

                        if (success) {
                            int userId = json.getInt("userId");
                            String userLogin = json.getString("login");

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.putExtra("username", userLogin);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = json.optString("message", "Błąd rejestracji");
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Błąd odpowiedzi", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(RegisterActivity.this, "Błąd połączenia: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });

        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}