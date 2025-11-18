package com.example.projek;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    Button btnSubmit;
    EditText etNama, etNim, etEmail, etTanggalLahir, etNoHp, etUsername, etPassword;
    boolean isPasswordVisible = false;
    private Calendar calendar;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupPasswordToggle();
        setupDatePicker();
        setupRegisterButton();
        setupNimListener();
    }

    private void initViews() {
        btnSubmit = findViewById(R.id.buttonregister);
        etNama = findViewById(R.id.nama);
        etNim = findViewById(R.id.nim);
        etEmail = findViewById(R.id.email);
        etTanggalLahir = findViewById(R.id.tanggal_lahir);
        etNoHp = findViewById(R.id.no_hp);
        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);

        calendar = Calendar.getInstance();

        // Set icon mata tertutup default
        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);
    }

    private void setupNimListener() {
        etNim.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Ketika NIM kehilangan fokus, set email otomatis
                    String nim = etNim.getText().toString().trim();
                    if (!nim.isEmpty()) {
                        etEmail.setText(nim + "@student.polije.ac.id");
                    }
                }
            }
        });
    }

    private void setupPasswordToggle() {
        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_END = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (etPassword.getCompoundDrawables()[DRAWABLE_END] != null) {
                        int iconWidth = etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width();
                        if (event.getRawX() >= (etPassword.getRight() - etPassword.getPaddingEnd() - iconWidth)) {
                            togglePasswordVisibility();
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Sembunyikan password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matatutup, 0);
            isPasswordVisible = false;
        } else {
            // Tampilkan password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.matabuka, 0);
            isPasswordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void setupDatePicker() {
        etTanggalLahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void setupRegisterButton() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String nama = etNama.getText().toString().trim();
        String nim = etNim.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String tanggalLahir = etTanggalLahir.getText().toString().trim();
        String noHp = etNoHp.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi field kosong
        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(nim) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(tanggalLahir) || TextUtils.isEmpty(noHp) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterActivity.this, "Harap isi semua field!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi format NIM (1 huruf + 8 angka)
        if (!nim.matches("^[A-Za-z]\\d{8}$")) {
            Toast.makeText(RegisterActivity.this, "Format NIM harus 1 huruf diikuti 8 angka!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi email harus sesuai dengan NIM
        String expectedEmail = nim + "@student.polije.ac.id";
        if (!email.equals(expectedEmail)) {
            Toast.makeText(RegisterActivity.this, "Email harus " + expectedEmail, Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi format tanggal lahir
        if (!tanggalLahir.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            Toast.makeText(RegisterActivity.this, "Format tanggal lahir harus DD/MM/YYYY!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Debug: Tampilkan data yang akan dikirim
        Log.d(TAG, "Data yang dikirim:");
        Log.d(TAG, "Nama: " + nama);
        Log.d(TAG, "NIM: " + nim);
        Log.d(TAG, "Email: " + email);
        Log.d(TAG, "Tanggal Lahir: " + tanggalLahir);
        Log.d(TAG, "No HP: " + noHp);
        Log.d(TAG, "Username: " + username);

        try {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<Map<String, Object>> call = apiService.registerUser(
                    nama, nim, email, tanggalLahir, noHp, username, password
            );

            Log.d(TAG, "Memulai request API...");
            Log.d(TAG, "URL: " + ApiClient.BASE_URL + "register.php");

            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Log.d(TAG, "Response Code: " + response.code());

                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.d(TAG, "Response Body: " + response.body());

                            try {
                                Object successObj = response.body().get("success");
                                Object messageObj = response.body().get("message");

                                boolean success = false;
                                String message = "Terjadi kesalahan";

                                if (successObj instanceof Boolean) {
                                    success = (Boolean) successObj;
                                } else if (successObj != null) {
                                    success = Boolean.parseBoolean(successObj.toString());
                                }

                                if (messageObj instanceof String) {
                                    message = (String) messageObj;
                                } else if (messageObj != null) {
                                    message = messageObj.toString();
                                }

                                if (success) {
                                    Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, Login.class));
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing response: " + e.getMessage());
                                Toast.makeText(RegisterActivity.this, "Error parsing response dari server", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "Response body is null");
                            Toast.makeText(RegisterActivity.this, "Response kosong dari server", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle error response
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e(TAG, "Error Response Code: " + response.code());
                            Log.e(TAG, "Error Response Body: " + errorBody);
                            Log.e(TAG, "Error Message: " + response.message());

                            if (response.code() == 404) {
                                Toast.makeText(RegisterActivity.this, "Endpoint tidak ditemukan (404)", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 500) {
                                Toast.makeText(RegisterActivity.this, "Server error (500)", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error: " + response.code() + " - " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                            Toast.makeText(RegisterActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.e(TAG, "Error koneksi: " + t.getMessage());
                    t.printStackTrace();

                    if (t.getMessage() != null && t.getMessage().contains("Failed to connect")) {
                        Toast.makeText(RegisterActivity.this, "Gagal terhubung ke server. Periksa koneksi internet dan IP server.", Toast.LENGTH_LONG).show();
                    } else if (t.getMessage() != null && t.getMessage().contains("timeout")) {
                        Toast.makeText(RegisterActivity.this, "Timeout koneksi ke server", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Format tanggal menjadi YYYY/MM/DD
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etTanggalLahir.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set batas tanggal (opsional: maksimal tanggal hari ini)
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }
}