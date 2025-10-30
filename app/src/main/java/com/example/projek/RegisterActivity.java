package com.example.projek;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText etNama, etNim, etEmail, etUsername, etPassword;
    boolean isPasswordVisible = false; // status lihat/sembunyi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        btnSubmit = findViewById(R.id.buttonregister);
        etNama = findViewById(R.id.nama);
        etNim = findViewById(R.id.nim);
        etEmail = findViewById(R.id.email);
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);

        // set icon mata tertutup default
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);

        // toggle show/hide password saat icon diklik
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; // posisi drawable kanan
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etPassword.getCompoundDrawables()[DRAWABLE_END] != null) {
                    int iconWidth = etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width();
                    if (event.getRawX() >= (etPassword.getRight() - etPassword.getPaddingEnd() - iconWidth)) {
                        if (isPasswordVisible) {
                            // sembunyikan password
                            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);
                            isPasswordVisible = false;
                        } else {
                            // tampilkan password
                            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matabuka, 0);
                            isPasswordVisible = true;
                        }
                        etPassword.setSelection(etPassword.getText().length()); // cursor tetap di akhir
                        return true;
                    }
                }
            }
            return false;
        });

        // tombol register
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = etNama.getText().toString().trim();
                String nim = etNim.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (nama.isEmpty() || nim.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Harap isi semua field!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<Map<String, Object>> call = apiService.registerUser(nama, nim, email, username, password);

                call.enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, Login.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Gagal registrasi, coba lagi!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
    });


    // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });
}
}