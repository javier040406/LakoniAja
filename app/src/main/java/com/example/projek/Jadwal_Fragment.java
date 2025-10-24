package com.example.projek;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        // Tombol lihat jadwal
        View btnLihatJadwal = view.findViewById(R.id.buttonlihatjadwal1);

        // Tombol ubah jadwal
        View btnUbahJadwal = view.findViewById(R.id.btnubahjadwal);

        // Ketika tombol "Lihat Jadwal" diklik → pindah ke BookingFragment
        btnLihatJadwal.setOnClickListener(v -> {
            Fragment bookingFragment = new BookingFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flfragment, bookingFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Ketika tombol "Ubah Jadwal" diklik → pindah ke Reschedule fragment
        btnUbahJadwal.setOnClickListener(v -> {
            Fragment rescheduleFragment = new Reschedule();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flfragment, rescheduleFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
