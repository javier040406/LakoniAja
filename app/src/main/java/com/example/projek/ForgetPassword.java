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

public class ForgetPassword extends AppCompatActivity {

    Button btnSubmit;
    EditText etPasswordBaru;
    boolean isPasswordVisible = false; // status lihat/sembunyi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgetpassword);

        btnSubmit = findViewById(R.id.buttonpassword);
        etPasswordBaru = findViewById(R.id.password); // pastikan id di XML = password

        // Set ikon awal (mata tertutup)
        etPasswordBaru.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);

        // Toggle show/hide password
        etPasswordBaru.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etPasswordBaru.getCompoundDrawables()[DRAWABLE_END] != null) {
                    int iconWidth = etPasswordBaru.getCompoundDrawables()[DRAWABLE_END].getBounds().width();
                    if (event.getRawX() >= (etPasswordBaru.getRight() - etPasswordBaru.getPaddingEnd() - iconWidth)) {
                        if (isPasswordVisible) {
                            // sembunyikan password
                            etPasswordBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            etPasswordBaru.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);
                            isPasswordVisible = false;
                        } else {
                            // tampilkan password
                            etPasswordBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            etPasswordBaru.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matabuka, 0);
                            isPasswordVisible = true;
                        }
                        etPasswordBaru.setSelection(etPasswordBaru.getText().length());
                        return true;
                    }
                }
            }
            return false;
        });

        // Tombol Submit
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: tambahkan validasi (misalnya cek panjang password, dll)

                // 1. Notifikasi sukses
                Toast.makeText(ForgetPassword.this, "Password Berhasil Diubah!", Toast.LENGTH_SHORT).show();

                // 2. Pindah ke halaman Login (MainActivity)
                Intent intent = new Intent(ForgetPassword.this, Login.class);
                startActivity(intent);

                // 3. Tutup ForgetPassword supaya tidak bisa kembali
                finish();
            }
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgetpassword), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
