package com.example.projek;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtikelActivity extends AppCompatActivity {

    private RecyclerView recyclerArtikel;
    private ArtikelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Artikel");

        recyclerArtikel = findViewById(R.id.rvArtikel);
        recyclerArtikel.setLayoutManager(new LinearLayoutManager(this));

        loadArtikel();
    }

    private void loadArtikel() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAllArtikel().enqueue(new Callback<ArtikelResponse>() {
            @Override
            public void onResponse(Call<ArtikelResponse> call, Response<ArtikelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Artikel> artikelList = response.body().getData();
                    adapter = new ArtikelAdapter(ArtikelActivity.this, artikelList);
                    recyclerArtikel.setAdapter(adapter);
                } else {
                    Log.e("ARTIKEL", "Response gagal: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ArtikelResponse> call, Throwable t) {
                Log.e("ARTIKEL", "Error: " + t.getMessage());
            }
        });
    }
}