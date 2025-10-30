package com.example.projek.network;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("register.php")
    Call<Map<String, Object>> registerUser(
            @Field("nama") String nama,
            @Field("nim") String nim,
            @Field("email") String email,
            @Field("username") String username,
            @Field("password") String password
    );
}
