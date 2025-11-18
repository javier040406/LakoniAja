package com.example.projek;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AkunFragment extends Fragment {

    private TextView tampilNama, tampilNim, tampilUsername, tampilEmail, tampilNoHp, tampilTanggalLahir;
    private Button btnEditPassword, btnLogout;
    private String currentUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_akun, container, false);

        initViews(view);
        loadUserData();

        return view;
    }

    private void initViews(View view) {
        tampilNama = view.findViewById(R.id.tampil_nama);
        tampilNim = view.findViewById(R.id.tampil_nim);
        tampilUsername = view.findViewById(R.id.tampil_username);
        tampilEmail = view.findViewById(R.id.tampil_email);
        tampilNoHp = view.findViewById(R.id.tampil_no_hp);
        tampilTanggalLahir = view.findViewById(R.id.tampil_tanggal_lahir);
        btnEditPassword = view.findViewById(R.id.btn_edit_password);
        btnLogout = view.findViewById(R.id.btn_logout);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", "");

        btnEditPassword.setOnClickListener(v -> {
            showEditPasswordDialog();
        });

        btnLogout.setOnClickListener(v -> {
            konfirmasiLogout();
        });
    }

    private void loadUserData() {
        if (currentUsername.isEmpty()) {
            Toast.makeText(getActivity(), "Username tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getProfile(currentUsername);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");

                    if (success != null && success) {
                        Map<String, Object> user = (Map<String, Object>) response.body().get("user");

                        if (user != null) {
                            tampilNama.setText(getSafeString(user.get("nama")));
                            tampilNim.setText(getSafeString(user.get("nim")));
                            tampilUsername.setText(getSafeString(user.get("username")));
                            tampilEmail.setText(getSafeString(user.get("email")));
                            tampilNoHp.setText(getSafeString(user.get("no_hp")));

                            String tanggalLahir = getSafeString(user.get("tanggal_lahir"));
                            String formattedDate = convertDateFormat(tanggalLahir);
                            tampilTanggalLahir.setText(formattedDate);
                        }
                    } else {
                        String message = (String) response.body().get("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Response tidak valid dari server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getSafeString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    private String convertDateFormat(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            String[] parts = dateString.split("-");
            if (parts.length == 3) {
                String year = parts[0];
                String month = parts[1];
                String day = parts[2];
                return day + "/" + month + "/" + year;
            }
            return dateString;
        } catch (Exception e) {
            return dateString;
        }
    }

    private void showEditPasswordDialog() {
        final Dialog dialog = new Dialog(getActivity());
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
                Toast.makeText(getActivity(), "Harap isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 8) {
                Toast.makeText(getActivity(), "Password baru minimal 8 karakter", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(getActivity(), "Password tidak cocok", Toast.LENGTH_SHORT).show();
                return;
            }

            changePassword(newPassword, dialog);
        });

        dialog.show();
    }

    private void changePassword(String newPassword, Dialog dialog) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.changePassword(currentUsername, newPassword);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");
                    String message = (String) response.body().get("message");

                    if (success != null && success) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Response tidak valid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getActivity(), "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void konfirmasiLogout() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_konfirmasi_logout);

        Button btnYa = dialog.findViewById(R.id.btn_ya);
        Button btnTidak = dialog.findViewById(R.id.btn_tidak);

        btnYa.setOnClickListener(v -> {
            prosesLogout();
            dialog.dismiss();
        });

        btnTidak.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        dialog.show();
    }

    private void prosesLogout() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(getActivity(), Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}