package com.example.projek;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArtikelAdapter extends RecyclerView.Adapter<ArtikelAdapter.ViewHolder> {

    ArrayList<ArtikelModel> list;
    Context context;

    public ArtikelAdapter(Context context, ArrayList<ArtikelModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Pastikan layout R.layout.item_artikel sudah dibuat di folder res/layout
        View v = LayoutInflater.from(context).inflate(R.layout.item_artikel, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArtikelModel artikel = list.get(position);

        holder.tvJudul.setText(artikel.getJudul());
        holder.tvDeskripsi.setText(artikel.getDeskripsi());
        holder.imgArtikel.setImageResource(artikel.getGambar());

        holder.btnBaca.setOnClickListener(v -> {
            // Pastikan DetailArtikelActivity sudah dibuat dan didaftarkan di AndroidManifest.xml
            Intent intent = new Intent(context, DetailArtikel.class);
            intent.putExtra("judul", artikel.getJudul());
            intent.putExtra("isi", artikel.getIsi());
            intent.putExtra("gambar", artikel.getGambar());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgArtikel;
        TextView tvJudul, tvDeskripsi;
        Button btnBaca;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArtikel = itemView.findViewById(R.id.imgArtikel);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            btnBaca = itemView.findViewById(R.id.btnBaca);
        }
    }
}