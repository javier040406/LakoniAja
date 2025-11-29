package com.example.projek;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Beranda_Fragment extends Fragment {

    private RecyclerView recyclerTestimoni;
    private TestimoniAdapter testimoniAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beranda_, container, false);

        TextView textHello = view.findViewById(R.id.textView2);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String namaUser = sharedPreferences.getString("nama", "User"); // default jika kosong
        textHello.setText("Hello, " + namaUser);

        recyclerTestimoni = view.findViewById(R.id.recyclerTestimoni);
        recyclerTestimoni.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTestimoni();

        ImageButton btnMulaiKonseling = view.findViewById(R.id.btn_mulai_konseling);
        btnMulaiKonseling.setOnClickListener(v -> {
            Jadwal_Fragment jadwalFragment = new Jadwal_Fragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.flfragment, jadwalFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        ImageButton btnArtikel = view.findViewById(R.id.btn_artikel);
        btnArtikel.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ArtikelFragment.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadTestimoni() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getTestimoni().enqueue(new Callback<TestimoniResponse>() {
            @Override
            public void onResponse(Call<TestimoniResponse> call, Response<TestimoniResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Testimoni> listTestimoni = response.body().getData();
                    testimoniAdapter = new TestimoniAdapter(listTestimoni);
                    recyclerTestimoni.setAdapter(testimoniAdapter);
                } else {
                    Log.e("TESTIMONI", "Response gagal: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<TestimoniResponse> call, Throwable t) {
                Log.e("TESTIMONI", "Error: " + t.getMessage());
            }
        });
    }
}
