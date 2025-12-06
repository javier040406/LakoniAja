package com.example.projek;

public class Artikel {
    private int id_artikel;
    private String judul;
    private String isi;
    private String gambar;
    private String link_sumber;

    public Artikel() {} // Dibutuhkan Retrofit

    public int getId_artikel() { return id_artikel; }
    public String getJudul() { return judul; }
    public String getIsi() { return isi; }
    public String getGambar() { return gambar; }
    public String getLink_sumber() { return link_sumber; }
}
