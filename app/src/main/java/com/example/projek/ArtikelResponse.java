package com.example.projek;

import java.util.List;

public class ArtikelResponse {
    private String status;
    private List<Artikel> data;

    public boolean isStatus() { return status.equals("success"); }
    public List<Artikel> getData() { return data; }
}
