package com.example.projek.network;

import com.example.projek.BasicResponse;
import com.example.projek.BookingResponse;
import com.example.projek.BookingUserResponse;
import com.example.projek.JadwalResponse;
import com.example.projek.KonselorResponse;

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
            @Field("username") String username,
            @Field("password") String password

    );
    @FormUrlEncoded
    @POST("login.php")
    Call<Map<String, Object>> loginUser(
            @Field("username") String username,
            @Field("password") String password
    );

    // === LOGIN ===

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

    @GET("get_konselor.php")
    Call<KonselorResponse> getKonselor();

    @GET("get_jadwal.php")
    Call<JadwalResponse> getJadwalKonselor(
            @Query("id_konselor") String idKonselor
    );

    @FormUrlEncoded
    @POST("booking.php")
    Call<BookingResponse> bookingPHP(
            @Field("id_user") String idUser,
            @Field("jenis_konseling") String jenisKonseling,
            @Field("tanggal") String tanggal,
            @Field("jam_mulai") String jamMulai
    );

    @GET("get_booking_user.php")
    Call<BookingUserResponse> getBookingUser(
            @Query("id_user") String idUser
    );
    @FormUrlEncoded
    @POST("batal_booking.php")
    Call<BasicResponse> batalBooking(
            @Field("id_booking") String idBooking,
            @Field("id_jadwal") String idJadwal
    );


}

