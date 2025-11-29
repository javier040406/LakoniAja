package com.example.projek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestimoniFragment extends Fragment {

    private static final String ARG_ID_BOOKING = "id_booking";
    private String idBooking;

    private EditText etKomentar;
    private Button btnKirim;

    public TestimoniFragment() {}

    public static TestimoniFragment newInstance(String idBooking) {
        TestimoniFragment fragment = new TestimoniFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID_BOOKING, idBooking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idBooking = getArguments().getString(ARG_ID_BOOKING);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_testimoni, container, false);

        // Tombol back
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        etKomentar = view.findViewById(R.id.etTestimoni);
        btnKirim = view.findViewById(R.id.btnKirimTestimoni);

        // Sembunyikan BottomNavigationView
        BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
        if (nav != null) nav.setVisibility(View.GONE);

        btnKirim.setOnClickListener(v -> kirimTestimoni());

        return view;
    }

    private void kirimTestimoni() {
        String komentar = etKomentar.getText().toString().trim();
        if (komentar.isEmpty()) {
            Toast.makeText(getContext(), "Isi komentar terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sp = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String idUser = sp.getString("id_user", null);
        String idKonselor = sp.getString("id_konselor", null);

        if (idUser == null || idKonselor == null) {
            Toast.makeText(getContext(), "User atau konselor tidak tersedia!", Toast.LENGTH_SHORT).show();
            return;
        }

        String tanggal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.kirimTestimoni(idUser, idKonselor, komentar, tanggal).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(getContext(), "Testimoni berhasil dikirim!", Toast.LENGTH_SHORT).show();

                    // Kembalikan ke JadwalFragment
                    Fragment jadwalFragment = new Jadwal_Fragment();
                    getParentFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flfragment, jadwalFragment)
                            .commit();

                    // Tampilkan kembali BottomNavigationView
                    BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
                    if (nav != null) nav.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(getContext(), "Gagal mengirim testimoni!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan koneksi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Pastikan navbar muncul kembali jika fragment dihancurkan
        BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
        if (nav != null) nav.setVisibility(View.VISIBLE);
    }
}
