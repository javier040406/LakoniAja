package com.example.projek;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Reschedule extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Reschedule() {
        // Required empty public constructor
    }

    public static Reschedule newInstance(String param1, String param2) {
        Reschedule fragment = new Reschedule();
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
        View view = inflater.inflate(R.layout.fragment_reschedule, container, false);

        TextView txtTanggal = view.findViewById(R.id.txt_tanggal);
        Spinner spinnerWaktu = view.findViewById(R.id.spinnerjam);
        Button btnReschedule = view.findViewById(R.id.buttonreschedule);
        ImageView btnBack = view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        // Pilih tanggal
        txtTanggal.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        txtTanggal.setText(selectedDate);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        // Tombol booking
        btnReschedule.setOnClickListener(v -> {
            String tanggal = txtTanggal.getText().toString().trim();
            String waktu = spinnerWaktu.getSelectedItem().toString();

            if (tanggal.isEmpty() || tanggal.equals("Pilih tanggal")) {
                Toast.makeText(getContext(), "Pilih tanggal terlebih dahulu!", Toast.LENGTH_SHORT).show();
            } else if (waktu.equals("Pilih Waktu")) {
                Toast.makeText(getContext(), "Pilih waktu terlebih dahulu!", Toast.LENGTH_SHORT).show();
            } else {
                showKonfirmasiDialog(tanggal, waktu);
            }
        });

        return view;
    }

    private void showKonfirmasiDialog(String tanggal, String waktu) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_konfirmasi_reschedule);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnYa = dialog.findViewById(R.id.btn_ya);
        Button btnBatal = dialog.findViewById(R.id.btn_batal);

        String pesan = "Anda akan mengubah jadwal ke:\n\n" +
                "Tanggal: " + tanggal + "\n" +
                "Jam: " + waktu + "\n\n" +
                "Apakah jadwal ini sudah benar?";
        tvMessage.setText(pesan);

        btnYa.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Jadwal berhasil diubah!", Toast.LENGTH_SHORT).show();
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
