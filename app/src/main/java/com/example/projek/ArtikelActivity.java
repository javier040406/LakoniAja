package com.example.projek;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ArtikelActivity extends AppCompatActivity {

    private RecyclerView rvArtikel;
    private ArtikelAdapter adapter;
    private List<Artikel> artikelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        rvArtikel = findViewById(R.id.rvArtikel);
        rvArtikel.setLayoutManager(new LinearLayoutManager(this));

        artikelList = new ArrayList<>();
        artikelList.add(new Artikel(
                "Pentingnya Kesehatan Mental bagi Remaja dan Cara Menghadapinya",
                "Masa remaja adalah masa penting dalam pembentukan generasi sehat, tangguh, dan produktif.",
                R.drawable.img_artikel1));

        adapter = new ArtikelAdapter(this, artikelList);
        rvArtikel.setAdapter(adapter);


    }
}
