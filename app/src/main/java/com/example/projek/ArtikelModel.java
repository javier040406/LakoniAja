package com.example.projek;

public class ArtikelModel {
    private String judul;
    private String deskripsi;
    private String isi;
    private int gambar;

    public ArtikelModel(String judul, String deskripsi, String isi, int gambar) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.isi = isi;
        this.gambar = gambar;
    }

    public String getJudul() { return judul; }
    public String getDeskripsi() { return deskripsi; }
    public String getIsi() { return isi; }
    public int getGambar() { return gambar; }
}