package com.example.projek;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    boolean isPasswordVisible = false;
    FrameLayout loadingOverlay; // overlay loading spinner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔹 Cek apakah sudah login sebelumnya
        boolean isLoggedIn = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Jika sudah login, langsung ke MainActivity
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish(); // tutup halaman login agar tidak bisa kembali
            return;
        }

        // 🔹 Jika belum login, tampilkan halaman login
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        etUsername = findViewById(R.id.nama);
        etPassword = findViewById(R.id.password);
        btnLogin   = findViewById(R.id.buttonlogin);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        // set ikon mata awal (tertutup)
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);

        // toggle password visibility
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etPassword.getCompoundDrawables()[DRAWABLE_END] != null) {
                    int iconWidth = etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width();
                    if (event.getRawX() >= (etPassword.getRight() - etPassword.getPaddingEnd() - iconWidth)) {
                        if (isPasswordVisible) {
                            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);
                            isPasswordVisible = false;
                        } else {
                            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matabuka, 0);
                            isPasswordVisible = true;
                        }
                        etPassword.setSelection(etPassword.getText().length());
                        return true;
                    }
                }
            }
            return false;
        });

        // tombol login ditekan
        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.equals("konseli") && pass.equals("1")) {
                // tampilkan overlay loading
                loadingOverlay.setVisibility(View.VISIBLE);

                // delay 1 detik, lalu pindah ke MainActivity
                new Handler().postDelayed(() -> {
                    loadingOverlay.setVisibility(View.GONE);

                    // 🔹 Simpan status login agar tidak perlu login lagi
                    getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("isLoggedIn", true)
                            .apply();

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // tutup activity login
                }, 1000);

            } else {
                Toast.makeText(Login.this, "Username atau password salah!", Toast.LENGTH_SHORT).show();
            }
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void gotoregister(View view) {
        Intent intent = new Intent(Login.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void gotoforget(View view) {
        Intent intent = new Intent(Login.this, ForgetPassword.class);
        startActivity(intent);
    }
}
