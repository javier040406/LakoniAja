package com.example.projek.network;

import com.example.projek.ArtikelResponse;
import com.example.projek.BasicResponse;
import com.example.projek.BookingResponse;
import com.example.projek.BookingUserResponse;
import com.example.projek.JadwalResponse;
import com.example.projek.KonselorResponse;
import com.example.projek.Testimoni;
import com.example.projek.TestimoniResponse;

import java.util.List;
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
    @POST("send_message.php")
    Call<Map<String, Object>> sendMessage(
            @Field("id_booking") int idBooking,
            @Field("id_user") int idUser,
            @Field("id_konselor") int idKonselor,
            @Field("pesan") String pesan
    );

    // === CHAT - GET MESSAGES ===
    @FormUrlEncoded
    @POST("get_messages.php")
    Call<Map<String, Object>> getMessages(@Field("id_booking") int idBooking)
    ;

    // === CHAT - GET SESSIONS ===
    @GET("api/get_sessions.php")
    Call<Map<String, Object>> getSessions(
            @Query("id_user") int idUser
    );

    // === GET PROFILE ===
    @FormUrlEncoded
    @POST("get_profile.php")
    Call<Map<String, Object>> getProfileByIdUser(
            @Field("id_user") String idUser
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

    // === GET BOOKING USER ===
    @GET("get_booking_user.php")
    Call<Map<String, Object>> getBookingUser(
            @Query("id_user") String idUser
    );

    // === AMBIL JADWAL KONSELOR
    @GET("get_jadwal.php")
    Call<JadwalResponse> getJadwalKonselor(
            @Query("id_konselor") String idKonselor
    );

    // === BOOKING ===
    @FormUrlEncoded
    @POST("booking.php")
    Call<BookingResponse> bookingPHP(
            @Field("id_user") String idUser,
            @Field("jenis_konseling") String jenisKonseling,
            @Field("tanggal") String tanggal,
            @Field("jam_mulai") String jamMulai,
            @Field("id_jadwal") String idJadwal
    );

    @FormUrlEncoded
    @POST("cekBookingAktif.php")
    Call<BasicResponse> cekBookingAktif(@Field("id_user") String idUser);

    // === AMBIL KONSELOR ===
    @GET("get_konselor.php")
    Call<KonselorResponse> getKonselor();

    // === BATAL BOOKING ===
    @FormUrlEncoded
    @POST("batal_booking.php")
    Call<BasicResponse> batalBooking(
            @Field("id_booking") String idBooking,
            @Field("id_jadwal") String idJadwal
    );

    // === RESCHEDULE BOOKING ===
    @FormUrlEncoded
    @POST("reschedule.php")
    Call<BasicResponse> rescheduleBooking(
            @Field("id_booking") String idBooking,
            @Field("tanggal_lama") String tanggalLama,
            @Field("jenis_lama") String jenisLama,
            @Field("tanggal_baru") String tanggalBaru,
            @Field("jenis_baru") String jenisBaru,
            @Field("id_konselor") String idKonselor,
            @Field("jam_baru") String jamBaru
    );

    @FormUrlEncoded
    @POST("checkRescheduleStatus.php")
    Call<BasicResponse> checkRescheduleStatus(
            @Field("id_booking") String idBooking
    );

    // === TESTIMONI ===
    @POST("kirim_testimoni.php")
    @FormUrlEncoded
    Call<BasicResponse> kirimTestimoni(
            @Field("id_user") String idUser,
            @Field("id_konselor") String idKonselor,
            @Field("komentar") String komentar,
            @Field("tanggal") String tanggal
    );

    @GET("get_testimoni.php")
    Call<TestimoniResponse> getTestimoni();

    @GET("get_artikel.php") // ganti dengan endpoint PHP kamu
    Call<ArtikelResponse> getAllArtikel();
}

