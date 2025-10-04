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

public class Login extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    boolean isPasswordVisible = false; // status password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        etUsername = findViewById(R.id.nama);
        etPassword = findViewById(R.id.password);
        btnLogin   = findViewById(R.id.buttonlogin);

        // Set icon awal untuk drawableEnd (mata tertutup)
        // Pastikan kamu punya ic_eye_closed.png & ic_eye_open.png di res/drawable
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);

        // Touch listener untuk mendeteksi klik pada drawableEnd (ikon mata)
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; // index drawable kanan
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etPassword.getCompoundDrawables()[DRAWABLE_END] != null) {
                    int iconWidth = etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width();
                    // rawX >= right - paddingEnd - widthDrawable
                    if (event.getRawX() >= (etPassword.getRight() - etPassword.getPaddingEnd() - iconWidth)) {
                        // toggle visibility
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
                        // biar cursor tetap di akhir
                        etPassword.setSelection(etPassword.getText().length());
                        return true; // event consumed
                    }
                }
            }
            return false;
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = etUsername.getText().toString();
                String pass = etPassword.getText().toString();

                if (user.equals("admin") && pass.equals("12345")) {
                    // ✅ Login sukses
                    Toast.makeText(Login.this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // ❌ Login gagal
                    Toast.makeText(Login.this, "Username atau password salah!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Pindah ke halaman Register
    public void gotoregister(View view) {
        Intent intent = new Intent(Login.this, RegisterActivity.class);
        startActivity(intent);
    }

    // Pindah ke halaman Lupa Password
    public void gotoforget(View view) {
        Intent intent = new Intent(Login.this, ForgetPassword.class);
        startActivity(intent);
    }
}
