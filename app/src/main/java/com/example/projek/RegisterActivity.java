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

public class RegisterActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText etPassword;
    boolean isPasswordVisible = false; // status lihat/sembunyi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        btnSubmit = findViewById(R.id.buttonregister);
        etPassword = findViewById(R.id.password); // pastikan id EditText di XML = password

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
                // TODO: validasi / simpan database

                // notifikasi sukses
                Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();

                // pindah ke login
                Intent intent = new Intent(RegisterActivity.this, Login.class);
                startActivity(intent);

                // tutup register biar gak balik dengan back
                finish();
            }
        });

        // edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
