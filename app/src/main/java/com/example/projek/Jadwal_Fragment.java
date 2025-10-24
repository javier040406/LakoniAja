package com.example.projek;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Jadwal_Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Jadwal_Fragment() {
        // Required empty public constructor
    }

    public static Jadwal_Fragment newInstance(String param1, String param2) {
        Jadwal_Fragment fragment = new Jadwal_Fragment();
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
        View view = inflater.inflate(R.layout.fragment_jadwal_, container, false);

        // Tombol-tombol tab
        Button btnKonselor = view.findViewById(R.id.btnKonselor);
        Button btnJadwalSaya = view.findViewById(R.id.btnJadwalSaya);

        // Layout tampilan
        View layoutKonselor = view.findViewById(R.id.layoutKonselor);
        View layoutJadwal = view.findViewById(R.id.layoutJadwal);

        // Tombol lihat jadwal (di konselor)
        View btnLihatJadwal1 = view.findViewById(R.id.buttonlihatjadwal1);
        View btnLihatJadwal2 = view.findViewById(R.id.buttonlihatjadwal2);
        View btnLihatJadwal3 = view.findViewById(R.id.buttonlihatjadwal3);

        // Tombol ubah jadwal (di jadwal saya)
        View btnUbahJadwal = view.findViewById(R.id.btnubahjadwal);

        // Tombol batalkan jadwal (di jadwal saya)
        Button btnBatalJadwal = view.findViewById(R.id.btnbatalkan);

        // ===================== LOGIKA TAB =====================
        btnKonselor.setOnClickListener(v -> {
            layoutKonselor.setVisibility(View.VISIBLE);
            layoutJadwal.setVisibility(View.GONE);

            btnKonselor.setBackgroundTintList(getResources().getColorStateList(R.color.biru_tua));
            btnKonselor.setTextColor(getResources().getColor(R.color.white));

            btnJadwalSaya.setBackgroundTintList(getResources().getColorStateList(R.color.abu_muda));
            btnJadwalSaya.setTextColor(getResources().getColor(R.color.biru_tua));
        });

        btnJadwalSaya.setOnClickListener(v -> {
            layoutKonselor.setVisibility(View.GONE);
            layoutJadwal.setVisibility(View.VISIBLE);

            btnJadwalSaya.setBackgroundTintList(getResources().getColorStateList(R.color.biru_tua));
            btnJadwalSaya.setTextColor(getResources().getColor(R.color.white));

            btnKonselor.setBackgroundTintList(getResources().getColorStateList(R.color.abu_muda));
            btnKonselor.setTextColor(getResources().getColor(R.color.biru_tua));
        });

        // ===================== TOMBOL LAIN =====================
        // "Lihat Jadwal" → buka BookingFragment
        View.OnClickListener lihatJadwalListener = v -> {
            Fragment bookingFragment = new BookingFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flfragment, bookingFragment)
                    .addToBackStack(null)
                    .commit();
        };

        btnLihatJadwal1.setOnClickListener(lihatJadwalListener);
        btnLihatJadwal2.setOnClickListener(lihatJadwalListener);
        btnLihatJadwal3.setOnClickListener(lihatJadwalListener);

        // "Ubah Jadwal" → buka Reschedule fragment
        btnUbahJadwal.setOnClickListener(v -> {
            Fragment rescheduleFragment = new Reschedule();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flfragment, rescheduleFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // "Batalkan Jadwal" → tampilkan popup konfirmasi
        btnBatalJadwal.setOnClickListener(v -> showDialogBatalJadwal());

        return view;
    }

    // ===================== DIALOG KONFIRMASI BATAL =====================
    private void showDialogBatalJadwal() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_batal_jadwal);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnYa = dialog.findViewById(R.id.btn_ya);
        Button btnBatal = dialog.findViewById(R.id.btn_batal);

        tvMessage.setText("Apakah Anda yakin ingin membatalkan jadwal ini?");

        btnYa.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Jadwal berhasil dibatalkan!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnBatal.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
}
