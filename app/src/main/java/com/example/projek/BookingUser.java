package com.example.projek;

public class BookingUser {
    private String id_booking;
    private String id_user;
    private String id_jadwal;
    private String jenis_konseling;
    private String tanggal_booking;
    private String id_konselor;  // tambahkan ini
    private String nama;

    public String getId_booking() { return id_booking; }
    public String getId_user() { return id_user; }
    public String getId_jadwal() { return id_jadwal; }
    public String getJenis_konseling() { return jenis_konseling; }
    public String getTanggal_booking() { return tanggal_booking; }
    public String getId_konselor() { return id_konselor; } // getter baru
    public String getNama() { return nama; }
}