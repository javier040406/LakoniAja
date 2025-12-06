package com.example.projek;

public class Konselor {
    private String id_konselor;  // harus sama dengan JSON
    private String nama;
    private String bidang_keahlian;
    private String nip;
    private String foto;

    public String getFoto() { return foto; }

    public String getId() {
        return id_konselor;  // BookingFragment newInstance akan bekerja
    }

    public String getNama() {
        return nama;
    }

    public String getBidang_keahlian() {
        return bidang_keahlian;
    }

    public String getNip() {
        return nip;
    }
}



