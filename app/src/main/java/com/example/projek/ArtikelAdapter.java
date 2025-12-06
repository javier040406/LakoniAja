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

import com.bumptech.glide.Glide;

import java.util.List;

public class ArtikelAdapter extends RecyclerView.Adapter<ArtikelAdapter.ViewHolder> {

    private Context context;
    private List<Artikel> artikelList;

    // Base URL folder gambar
    private final String BASE_URL_IMAGE = "http://192.168.18.9/weblakoniaja/uploads/artikel/";

    public ArtikelAdapter(Context context, List<Artikel> artikelList) {
        this.context = context;
        this.artikelList = artikelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_artikel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Artikel artikel = artikelList.get(position);

        // Judul & Deskripsi
        holder.tvJudul.setText(artikel.getJudul());
        String deskripsi = artikel.getIsi().length() > 100
                ? artikel.getIsi().substring(0, 100) + "..."
                : artikel.getIsi();
        holder.tvDeskripsi.setText(deskripsi);

        // Gunakan URL lengkap untuk gambar
        String imageUrl = BASE_URL_IMAGE + artikel.getGambar();

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.img_artikel1)
                .error(R.drawable.img_artikel1)
                .into(holder.imgArtikel);

        // Baca selengkapnya
        holder.btnBaca.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailArtikelActivity.class);
            intent.putExtra("judul", artikel.getJudul());
            intent.putExtra("isi", artikel.getIsi());
            intent.putExtra("link_sumber", artikel.getLink_sumber());
            intent.putExtra("gambar", imageUrl);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return artikelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
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