package com.example.projek;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Jadwal_Fragment extends Fragment {

    LinearLayout containerKonselor, containerJadwalSaya;
    TextView tvKosong;

    public Jadwal_Fragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_jadwal_, container, false);

        // ===================== INIT =====================
        Button btnKonselor = view.findViewById(R.id.btnKonselor);
        Button btnJadwalSaya = view.findViewById(R.id.btnJadwalSaya);

        View layoutKonselor = view.findViewById(R.id.layoutKonselor);
        View layoutJadwal = view.findViewById(R.id.layoutJadwal);

        containerKonselor = view.findViewById(R.id.containerKonselor);
        containerJadwalSaya = view.findViewById(R.id.containerJadwal);
        tvKosong = view.findViewById(R.id.tvKosong);


        // ===================== TAB HANDLER =====================
        btnKonselor.setOnClickListener(v -> {
            layoutKonselor.setVisibility(View.VISIBLE);
            layoutJadwal.setVisibility(View.GONE);

            btnKonselor.setBackgroundTintList(getResources().getColorStateList(R.color.biru_tua));
            btnKonselor.setTextColor(getResources().getColor(R.color.white));

            btnJadwalSaya.setBackgroundTintList(getResources().getColorStateList(R.color.abu_muda));
            btnJadwalSaya.setTextColor(getResources().getColor(R.color.biru_tua));
        });

        btnJadwalSaya.setOnClickListener(v -> {
            layoutKonselor.setVisibility(View.GONE);
            layoutJadwal.setVisibility(View.VISIBLE);

            btnJadwalSaya.setBackgroundTintList(getResources().getColorStateList(R.color.biru_tua));
            btnJadwalSaya.setTextColor(getResources().getColor(R.color.white));

            btnKonselor.setBackgroundTintList(getResources().getColorStateList(R.color.abu_muda));
            btnKonselor.setTextColor(getResources().getColor(R.color.biru_tua));
        });

        // ===================== LOAD DATA =====================
        loadKonselor();
        loadJadwalSaya();

        return view;
    }

    // ===========================================================
    // LOAD DATA KONSELOR
    // ===========================================================
    private void loadKonselor() {
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getKonselor().enqueue(new Callback<KonselorResponse>() {
            @Override
            public void onResponse(Call<KonselorResponse> call, Response<KonselorResponse> response) {
                if (response.isSuccessful() && response.body().isStatus()) {
                    List<Konselor> list = response.body().getData();
                    containerKonselor.removeAllViews();

                    for (Konselor k : list) {
                        addKonselorCard(k);
                    }
                }
            }

            @Override
            public void onFailure(Call<KonselorResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal mengambil data konselor!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===========================================================
    // CREATE CARD KONSELOR DINAMIS
    // ===========================================================
    private void addKonselorCard(Konselor k) {
        View card = getLayoutInflater().inflate(R.layout.item_konselor, null);

        TextView tvNama = card.findViewById(R.id.tv_nama_konselor);
        TextView tvBidang = card.findViewById(R.id.tv_bidang_konselor);
        TextView tvNip = card.findViewById(R.id.tv_nip_konselor);
        Button btnLihat = card.findViewById(R.id.buttonlihatjadwal);

        tvNama.setText(k.getNama());
        tvBidang.setText(k.getBidang_keahlian());
        tvNip.setText("NIP : " + k.getNip());

        btnLihat.setOnClickListener(v -> {
            Fragment bookingFragment = BookingFragment.newInstance(k.getId(), k.getNama());
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flfragment, bookingFragment)
                    .addToBackStack(null)
                    .commit();
        });

        containerKonselor.addView(card);
    }

    // ===========================================================
    // LOAD JADWAL USER
    // ===========================================================
    private void loadJadwalSaya() {

        SharedPreferences sp = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        String idUser = sp.getString("id_user", null);

        if (idUser == null) {
            Toast.makeText(getContext(), "User belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getBookingUser(idUser).enqueue(new Callback<BookingUserResponse>() {
            @Override
            public void onResponse(Call<BookingUserResponse> call, Response<BookingUserResponse> response) {
                if (response.isSuccessful() && response.body().isStatus()) {

                    containerJadwalSaya.removeAllViews();

                    List<BookingUser> list = response.body().getData();

                    // CEK KOSONG
                    if (list == null || list.isEmpty()) {
                        tvKosong.setVisibility(View.VISIBLE);
                        return;
                    }

                    tvKosong.setVisibility(View.GONE);

                    for (BookingUser b : list) {
                        addJadwalCard(b);
                    }
                }
            }

            @Override
            public void onFailure(Call<BookingUserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal mengambil jadwal!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===========================================================
    // CREATE CARD JADWAL DINAMIS
    // ===========================================================
    private void addJadwalCard(BookingUser b) {
        View card = getLayoutInflater().inflate(R.layout.item_booking_user, null);

        TextView tvNamaKonselor = card.findViewById(R.id.tvNamaKonselor);
        TextView tvTanggalWaktu = card.findViewById(R.id.tvTanggalWaktu);
        TextView tvJenis = card.findViewById(R.id.tvJenisKonseling);

        Button btnBatalkan = card.findViewById(R.id.btnBatalkan);
        Button btnUbah = card.findViewById(R.id.btnUbahJadwal);

        tvNamaKonselor.setText(b.getNama());
        tvTanggalWaktu.setText(b.getTanggal_booking());
        tvJenis.setText(b.getJenis_konseling());

        // KIRIM ID BOOKING KE DIALOG
        btnBatalkan.setOnClickListener(v -> showDialogBatalJadwal(b.getId_booking(), b.getId_jadwal()));

        btnUbah.setOnClickListener(v -> {
            Fragment rescheduleFragment = new Reschedule();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flfragment, rescheduleFragment)
                    .addToBackStack(null)
                    .commit();
        });

        containerJadwalSaya.addView(card);
    }


    // ===========================================================
    // DIALOG KONFIRMASI BATAL
    // ===========================================================
    private void showDialogBatalJadwal(String idBooking, String idJadwal) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_batal_jadwal);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnYa = dialog.findViewById(R.id.btn_ya);
        Button btnBatal = dialog.findViewById(R.id.btn_batal);

        tvMessage.setText("Apakah Anda yakin ingin membatalkan jadwal ini?");

        btnYa.setOnClickListener(v -> {
            batalBooking(idBooking, idJadwal);
            dialog.dismiss();
        });

        btnBatal.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void batalBooking(String idBooking, String idJadwal) {
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.batalBooking(idBooking, idJadwal).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body().isStatus()) {
                    Toast.makeText(getContext(), "Jadwal berhasil dibatalkan!", Toast.LENGTH_SHORT).show();
                    loadJadwalSaya(); // refresh list
                } else {
                    Toast.makeText(getContext(),
                            "Error: " + response.errorBody(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan koneksi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
