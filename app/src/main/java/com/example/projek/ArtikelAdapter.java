package com.example.projek;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArtikelAdapter extends RecyclerView.Adapter<ArtikelAdapter.ViewHolder> {

    private Context context;
    private List<Artikel> artikelList;

    public ArtikelAdapter(Context context, List<Artikel> artikelList) {
        this.context = context;
        this.artikelList = artikelList;
    }

    @NonNull
    @Override
    public ArtikelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artikel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtikelAdapter.ViewHolder holder, int position) {
        Artikel artikel = artikelList.get(position);
        holder.tvJudul.setText(artikel.getJudul());
        holder.tvDeskripsi.setText(artikel.getDeskripsi());
        holder.imgArtikel.setImageResource(artikel.getGambar());

        holder.btnBaca.setOnClickListener(v -> {
            // aksi klik tombol "Baca Sekarang"
        });
    }

    @Override
    public int getItemCount() {
        return artikelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
