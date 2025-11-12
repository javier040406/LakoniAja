package com.example.projek.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://192.168.18.9/android_api/"; // ganti id nya disini dan xml-network
    // Kalau pakai HP langsung, ganti IP laptop kamu, misal:
    // private static final String BASE_URL = "http://10.10.184.56/android_api/";

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
