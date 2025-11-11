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

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    boolean isPasswordVisible = false;
    FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔹 Jika sudah login, langsung ke MainActivity
        boolean isLoggedIn = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        etUsername = findViewById(R.id.nama);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.buttonlogin);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        // 🔹 ikon mata toggle password
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);
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

        // 🔹 Tombol login ditekan
        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(Login.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            loginToServer(user, pass);
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 🔹 Proses login ke server
    private void loginToServer(String username, String password) {
        loadingOverlay.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.loginUser(username, password);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                loadingOverlay.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");
                    String message = (String) response.body().get("message");

                    if (success != null && success) {
                        Toast.makeText(Login.this, "Login berhasil", Toast.LENGTH_SHORT).show();

                        // Simpan status login
                        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                .edit()
                                .putBoolean("isLoggedIn", true)
                                .apply();

                        // Pindah ke MainActivity
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }, 500);
                    } else {
                        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Response tidak valid dari server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
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
