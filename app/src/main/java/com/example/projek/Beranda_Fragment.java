package com.example.projek;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class Beranda_Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Beranda_Fragment() {
        // Required empty public constructor
    }

    public static Beranda_Fragment newInstance(String param1, String param2) {
        Beranda_Fragment fragment = new Beranda_Fragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout fragment
        View view = inflater.inflate(R.layout.fragment_beranda_, container, false);

        // Inisialisasi View lain
        View header = view.findViewById(R.id.headerLayout);
        View tombolUtama = view.findViewById(R.id.menuLayout);
        View testimoni = view.findViewById(R.id.testimoniLayout);

        ImageButton btnMulaiKonseling = view.findViewById(R.id.btn_mulai_konseling);
        ImageButton btnArtikel = view.findViewById(R.id.btn_artikel); // Mengganti nama variabel agar sesuai

        // Muat file animasi dari res/anim/
        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        Animation scaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);

        // Jalankan animasi
        if (header != null) header.startAnimation(fadeIn);
        if (tombolUtama != null) tombolUtama.startAnimation(slideUp);
        if (testimoni != null) testimoni.startAnimation(scaleUp);

        if (btnMulaiKonseling != null) {
            btnMulaiKonseling.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Jadwal_Fragment jadwalFragment = new Jadwal_Fragment();

                    // FragmentTransaction untuk mengganti fragment
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.flfragment, jadwalFragment);  // Ganti fragment di container
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        // Listener untuk tombol artikel
        if (btnArtikel != null) {
            btnArtikel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Arahkan ke ArtikelActivity
                    Intent intent = new Intent(getActivity(), ArtikelActivity.class);
                    startActivity(intent);
                }
            });
        }

        return view;
    }
}
