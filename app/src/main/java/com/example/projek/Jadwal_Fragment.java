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

        Button btnKonselor = view.findViewById(R.id.btnKonselor);
        Button btnJadwalSaya = view.findViewById(R.id.btnJadwalSaya);

        View layoutKonselor = view.findViewById(R.id.layoutKonselor);
        View layoutJadwal = view.findViewById(R.id.layoutJadwal);

        containerKonselor = view.findViewById(R.id.containerKonselor);
        containerJadwalSaya = view.findViewById(R.id.containerJadwal);
        tvKosong = view.findViewById(R.id.tvKosong);

        // Tab Handling
        btnKonselor.setOnClickListener(v -> {
            layoutKonselor.setVisibility(View.VISIBLE);
            layoutJadwal.setVisibility(View.GONE);

            btnKonselor.setBackgroundTintList(getResources().getColorStateList(R.color.biru_langit));
            btnKonselor.setTextColor(getResources().getColor(R.color.white));

            btnJadwalSaya.setBackgroundTintList(getResources().getColorStateList(R.color.abu_muda));
            btnJadwalSaya.setTextColor(getResources().getColor(R.color.biru_langit));
        });

        btnJadwalSaya.setOnClickListener(v -> {
            layoutKonselor.setVisibility(View.GONE);
            layoutJadwal.setVisibility(View.VISIBLE);

            btnJadwalSaya.setBackgroundTintList(getResources().getColorStateList(R.color.biru_langit));
            btnJadwalSaya.setTextColor(getResources().getColor(R.color.white));

            btnKonselor.setBackgroundTintList(getResources().getColorStateList(R.color.abu_muda));
            btnKonselor.setTextColor(getResources().getColor(R.color.biru_langit));
        });

        // Load data
        loadKonselor();
        loadJadwalSaya();

        return view;
    }

    private void loadKonselor() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getKonselor().enqueue(new Callback<KonselorResponse>() {
            @Override
            public void onResponse(Call<KonselorResponse> call, Response<KonselorResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Gagal mengambil data!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!response.body().isStatus()) {
                    Toast.makeText(getContext(), "Tidak ada data konselor!", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Konselor> list = response.body().getData();
                containerKonselor.removeAllViews();

                for (Konselor k : list) addKonselorCard(k);
            }

            @Override
            public void onFailure(Call<KonselorResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan koneksi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.flfragment, bookingFragment)
                    .addToBackStack(null)
                    .commit();
        });

        containerKonselor.addView(card);
    }

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

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Gagal load jadwal!", Toast.LENGTH_SHORT).show();
                    return;
                }

                containerJadwalSaya.removeAllViews();
                List<BookingUser> list = response.body().getData();

                if (list == null || list.isEmpty()) {
                    tvKosong.setVisibility(View.VISIBLE);
                    return;
                }

                tvKosong.setVisibility(View.GONE);

                for (BookingUser b : list) addJadwalCard(b);
            }

            @Override
            public void onFailure(Call<BookingUserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan koneksi!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addJadwalCard(BookingUser b) {
        View card = getLayoutInflater().inflate(R.layout.item_booking_user, null);

        TextView tvNamaKonselor = card.findViewById(R.id.tvNamaKonselor);
        TextView tvTanggalWaktu = card.findViewById(R.id.tvTanggalWaktu);
        TextView tvJenis = card.findViewById(R.id.tvJenisKonseling);

        Button btnBatalkan = card.findViewById(R.id.btnBatalkan);
        Button btnUbah = card.findViewById(R.id.btnUbahJadwal);
        Button btnTestimoni = card.findViewById(R.id.btnTestimoni); // tombol testimoni

        tvNamaKonselor.setText(b.getNama());
        tvTanggalWaktu.setText(b.getTanggal_booking());
        tvJenis.setText(b.getJenis_konseling());

        // Tombol Batalkan
        btnBatalkan.setOnClickListener(v -> showDialogBatalJadwal(b.getId_booking(), b.getId_jadwal()));

        // Tombol Ubah Jadwal
        btnUbah.setOnClickListener(v -> {
            Fragment rescheduleFragment = Reschedule.newInstance(
                    b.getId_booking(),
                    b.getTanggal_booking(),
                    b.getJenis_konseling(),
                    b.getNama(),
                    b.getId_konselor()
            );
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flfragment, rescheduleFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Tombol Testimoni
        btnTestimoni.setOnClickListener(v -> {
            // Simpan id_konselor ke SharedPreferences supaya TestimoniFragment bisa membacanya
            SharedPreferences sp = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("id_konselor", b.getId_konselor());
            editor.apply();

            // Buka TestimoniFragment dengan id_booking
            Fragment fragmentTestimoni = TestimoniFragment.newInstance(b.getId_booking());
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flfragment, fragmentTestimoni)
                    .addToBackStack(null)
                    .commit();
        });

        containerJadwalSaya.addView(card);
    }

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

                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    Toast.makeText(getContext(), "Jadwal berhasil dibatalkan!", Toast.LENGTH_SHORT).show();
                    loadJadwalSaya();
                } else {
                    Toast.makeText(getContext(), "Gagal membatalkan!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan koneksi!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
