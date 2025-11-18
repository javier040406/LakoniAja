package com.example.projek;

import java.util.List;

public class BookingUserResponse {
    private String status;     // <-- ubah jadi String
    private List<BookingUser> data;

    public boolean isStatus() {
        return status.equals("success");
    }

    public List<BookingUser> getData() { return data; }
}

