package com.example.projek;

import android.content.Intent;
import android.os.Bundle;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        etUsername = findViewById(R.id.nama);
        etPassword = findViewById(R.id.password);
        btnLogin   = findViewById(R.id.buttonlogin);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        // ikon mata awal (tertutup)
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

        // Tombol Login
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Harap isi semua field!", Toast.LENGTH_SHORT).show();
                return;
            }

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

                        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();

                        // Ganti bagian ini di onResponse:

                        if (success) {

                            // Ambil data user dari response
                            Map<String, Object> userData = (Map<String, Object>) response.body().get("data");
                            // Simpan id_user di SharedPreferences sebagai String agar fragment bisa membaca
                            getSharedPreferences("USER_DATA", MODE_PRIVATE)
                                    .edit()
                                    .putString("id_user", userData.get("id_user").toString()) // tetap String
                                    .apply();

                            // Pindah ke MainActivity
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    } else {
                        Toast.makeText(Login.this, "Gagal login. Coba lagi!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    loadingOverlay.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    t.printStackTrace();
                }
            });
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void gotoregister(View view) {
        startActivity(new Intent(Login.this, RegisterActivity.class));
    }

    public void gotoforget(View view) {
        startActivity(new Intent(Login.this, ForgetPassword.class));
    }
}
