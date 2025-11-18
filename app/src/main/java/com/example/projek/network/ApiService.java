package com.example.projek.network;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // === REGISTER ===
    @FormUrlEncoded
    @POST("register.php")
    Call<Map<String, Object>> registerUser(
            @Field("nama") String nama,
            @Field("nim") String nim,
            @Field("email") String email,
            @Field("tanggal_lahir") String tanggalLahir,
            @Field("no_hp") String noHp,
            @Field("username") String username,
            @Field("password") String password
    );

    // === LOGIN ===
    @FormUrlEncoded
    @POST("login.php")
    Call<Map<String, Object>> loginUser(
            @Field("login") String username,
            @Field("password") String password
    );

    // === CHAT - SEND MESSAGE ===
    @FormUrlEncoded
    @POST("api/send_message.php")
    Call<Map<String, Object>> sendMessage(
            @Field("id_user") int idUser,
            @Field("id_konselor") int idKonselor,
            @Field("id_sesi") int idSesi,
            @Field("pesan") String pesan
    );

    // === CHAT - GET MESSAGES ===
    @GET("api/get_messages.php")
    Call<Map<String, Object>> getMessages(
            @Query("id_user") int idUser,
            @Query("id_konselor") int idKonselor,
            @Query("id_sesi") int idSesi
    );

    // === CHAT - GET SESSIONS ===
    @GET("api/get_sessions.php")
    Call<Map<String, Object>> getSessions(
            @Query("id_user") int idUser
    );

    // === GET PROFILE ===
    @FormUrlEncoded
    @POST("get_profile.php")
    Call<Map<String, Object>> getProfile(
            @Field("username") String username
    );

    // === UBAH PASSWORD
    @FormUrlEncoded
    @POST("change_password.php")
    Call<Map<String, Object>> changePassword(
            @Field("username") String username,
            @Field("new_password") String newPassword
    );

    // === VALIDASI PENGGUNA
    @FormUrlEncoded
    @POST("validate_user.php")
    Call<Map<String, Object>> validateUserForPasswordReset(
            @Field("email") String email,
            @Field("tanggal_lahir") String tanggalLahir
    );
}
