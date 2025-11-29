package com.example.projek;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TestimoniAdapter extends RecyclerView.Adapter<TestimoniAdapter.ViewHolder> {

    private List<Testimoni> testimoniList;

    public TestimoniAdapter(List<Testimoni> testimoniList) {
        this.testimoniList = testimoniList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_testimoni, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Testimoni t = testimoniList.get(position);
        holder.nama.setText(t.getNama());
        holder.deskripsi.setText(t.getKomentar());
        holder.foto.setImageResource(R.drawable.defaultprofile); // bisa diganti API foto jika ada
    }

    @Override
    public int getItemCount() {
        return testimoniList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nama, deskripsi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.fotoTestimoni);
            nama = itemView.findViewById(R.id.namaTestimoni);
            deskripsi = itemView.findViewById(R.id.deskripsiTestimoni);
        }
    }
}