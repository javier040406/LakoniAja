package com.example.projek;

import java.util.List;

public class KonselorResponse {
    private String status; // karena API mengirim "success"
    private List<Konselor> data;

    public String getStatus() { return status; }

    public boolean isStatus() {
        return "success".equalsIgnoreCase(status);
    }

    public List<Konselor> getData() { return data; }
}

