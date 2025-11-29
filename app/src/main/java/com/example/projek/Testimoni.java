package com.example.projek;

import com.google.gson.annotations.SerializedName;

public class Testimoni {

    @SerializedName("id_testimoni")
    private String idTestimoni;

    @SerializedName("komentar")
    private String komentar;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("nama") // sesuai dengan query SELECT u.nama
    private String nama;

    public String getIdTestimoni() { return idTestimoni; }

    public String getKomentar() { return komentar; }

    public String getTanggal() { return tanggal; }

    public String getNama() { return nama; }
}
