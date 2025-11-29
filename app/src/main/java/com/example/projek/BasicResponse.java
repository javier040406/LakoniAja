package com.example.projek;

import com.google.gson.annotations.SerializedName;

public class BasicResponse {
    private boolean status;
    private String message;

    @SerializedName("reschedule_done")
    private boolean rescheduleDone;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean getRescheduleDone() {
        return rescheduleDone;
    }
}
