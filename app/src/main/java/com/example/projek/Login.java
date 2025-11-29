package com.example.projek;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    EditText etLogin, etPassword;
    Button btnLogin;
    boolean isPasswordVisible = false;
    FrameLayout loadingOverlay;
    private String currentUsername;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        etLogin = findViewById(R.id.nama);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.buttonlogin);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        calendar = Calendar.getInstance();

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

        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(Login.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            loginToServer(login, pass);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loginToServer(String login, String password) {
        loadingOverlay.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.loginUser(login, password);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                loadingOverlay.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");
                    String message = (String) response.body().get("message");

                    if (success != null && success) {
                        Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();

                        Map<String, Object> user = (Map<String, Object>) response.body().get("user");
                        String idUser = String.valueOf(user.get("id_user"));   // ambil ID user
                        String namaUser = String.valueOf(user.get("nama"));    // ambil nama user

                        SharedPreferences.Editor editor = getSharedPreferences("USER_DATA", MODE_PRIVATE).edit();
                        editor.putString("id_user", idUser);     // simpan ID user
                        editor.putString("nama", namaUser);      // simpan nama user
                        editor.putBoolean("isLoggedIn", true);   // tandai login
                        editor.apply();

                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }, 500);
                    }
                    if (success != null && success) {

                        Map<String, Object> user = (Map<String, Object>) response.body().get("user");
                        String idUser = String.valueOf(user.get("id_user"));   // ambil ID user
                        String namaUser = String.valueOf(user.get("nama"));    // ambil nama user

                        SharedPreferences.Editor editor = getSharedPreferences("USER_DATA", MODE_PRIVATE).edit();
                        editor.putString("id_user", idUser);     // simpan ID user
                        editor.putString("nama", namaUser);      // simpan nama user
                        editor.putBoolean("isLoggedIn", true);   // tandai login
                        editor.apply();

                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }, 500);
                    }
                    else {
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
        showUserValidationDialog();
    }

    private void showUserValidationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_validate_user);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etEmail = dialog.findViewById(R.id.etEmail);
        EditText etBirthdate = dialog.findViewById(R.id.etBirthdate);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnVerify = dialog.findViewById(R.id.btnVerify);

        // Setup DatePicker untuk etBirthdate
        etBirthdate.setOnClickListener(v -> {
            showDatePicker(etBirthdate);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnVerify.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String birthdate = etBirthdate.getText().toString().trim();

            if (email.isEmpty() || birthdate.isEmpty()) {
                Toast.makeText(Login.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            validateUserForPasswordReset(email, birthdate, dialog);
        });

        dialog.show();
    }

    private void showDatePicker(EditText etBirthdate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Format tanggal menjadi DD/MM/YYYY
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        etBirthdate.setText(dateFormat.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set batas tanggal maksimal hari ini
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void validateUserForPasswordReset(String email, String birthdate, Dialog dialog) {
        loadingOverlay.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.validateUserForPasswordReset(email, birthdate);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                loadingOverlay.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");
                    String message = (String) response.body().get("message");

                    if (success != null && success) {
                        Map<String, Object> user = (Map<String, Object>) response.body().get("user");
                        if (user != null) {
                            currentUsername = (String) user.get("username");
                            dialog.dismiss();
                            showEditPasswordDialog();
                        }
                    } else {
                        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Response tidak valid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditPasswordDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etNewPassword = dialog.findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = dialog.findViewById(R.id.etConfirmPassword);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSavePassword = dialog.findViewById(R.id.btnSavePassword);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSavePassword.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(Login.this, "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 8) {
                Toast.makeText(Login.this, "Password baru minimal 8 karakter", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(Login.this, "Password tidak cocok", Toast.LENGTH_SHORT).show();
                return;
            }

            resetPassword(newPassword, dialog);
        });

        dialog.show();
    }

    private void resetPassword(String newPassword, Dialog dialog) {
        loadingOverlay.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.changePassword(currentUsername, newPassword);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                loadingOverlay.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");
                    String message = (String) response.body().get("message");

                    if (success != null && success) {
                        Toast.makeText(Login.this, "Password berhasil direset", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, "Response tidak valid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(Login.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}