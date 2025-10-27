package com.example.projek;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AkunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AkunFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AkunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AkunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AkunFragment newInstance(String param1, String param2) {
        AkunFragment fragment = new AkunFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_akun, container, false);

        Button btnLogout = view.findViewById(R.id.btn_logout);

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                // Panggil metode untuk melakukan logout
                KonfirmasiLogout();
            });
        }
        return view;
}
    private void KonfirmasiLogout() {
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
        }
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        dialog.show();
}

    private void prosesLogout() {
        // 3. Hapus data sesi dari SharedPreferences
        // Pastikan nama "PREFS_NAME" dan key "IS_LOGGED_IN" SAMA dengan yang Anda gunakan saat login
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Menghapus semua data (atau gunakan editor.remove("IS_LOGGED_IN"))
        editor.apply();

        // 4. Arahkan pengguna kembali ke LoginActivity
        // Buat Intent untuk memulai LoginActivity
        Intent intent = new Intent(getActivity(), Login.class);

        // Tambahkan flags untuk membersihkan semua activity sebelumnya dari back stack
        // Ini mencegah pengguna menekan tombol "kembali" dan masuk lagi ke MainActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Mulai LoginActivity
        startActivity(intent);

        // (Opsional) Tutup Activity saat ini jika diperlukan
        if (getActivity() != null) {
            getActivity().finish();
        }

    }

}
