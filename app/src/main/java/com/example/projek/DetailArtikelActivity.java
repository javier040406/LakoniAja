package com.example.projek;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class DetailArtikelActivity extends AppCompatActivity {

    private ImageView imgDetail;
    private TextView tvJudul, tvIsi, tvSumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_artikel);

        imgDetail = findViewById(R.id.imgDetail);
        tvJudul = findViewById(R.id.tvDetailJudul);
        tvIsi = findViewById(R.id.tvDetailIsi);
        tvSumber = findViewById(R.id.tvSumber);

        // Ambil data artikel
        Intent intent = getIntent();
        String judul = intent.getStringExtra("judul");
        String isi = intent.getStringExtra("isi");
        String linkSumber = intent.getStringExtra("link_sumber");
        String gambarUrl = intent.getStringExtra("gambar");   // <--- ambil URL gambar

        // Tampilkan data
        tvJudul.setText(judul);
        tvIsi.setText(isi);

        // Load gambar via Glide
        Glide.with(this)
                .load(gambarUrl)
                .placeholder(R.drawable.img_artikel1)
                .error(R.drawable.img_artikel1)
                .into(imgDetail);

        // Klik link sumber
        if (linkSumber != null && !linkSumber.isEmpty()) {
            tvSumber.setOnClickListener(v -> {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkSumber));
                startActivity(browserIntent);
            });
        } else {
            tvSumber.setText("Sumber tidak tersedia");
        }
    }
}
