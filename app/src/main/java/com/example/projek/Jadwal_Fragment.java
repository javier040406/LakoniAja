package com.example.projek;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Jadwal_Fragment extends Fragment {

    LinearLayout containerKonselor, containerJadwalSaya;
    TextView tvKosong;
    private boolean hasActiveBooking = false;

    private List<Konselor> listKonselorFull = new ArrayList<>();
    private List<Map<String, Object>> listJadwalFull = new ArrayList<>();

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

        // ===================== SEARCH =====================
        EditText etSearch = view.findViewById(R.id.cari);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterKonselor(s.toString());
                filterJadwal(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Tab Handling
        btnKonselor.setOnClickListener(v -> switchTab(true, btnKonselor, btnJadwalSaya, layoutKonselor, layoutJadwal));
        btnJadwalSaya.setOnClickListener(v -> switchTab(false, btnKonselor, btnJadwalSaya, layoutKonselor, layoutJadwal));

        // Load data
        loadKonselor();
        loadJadwalSaya();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadJadwalSaya();
    }

    private void switchTab(boolean isKonselorTab, Button btnKonselor, Button btnJadwalSaya,
                           View layoutKonselor, View layoutJadwal) {
        if (isKonselorTab) {
            layoutKonselor.setVisibility(View.VISIBLE);
            layoutJadwal.setVisibility(View.GONE);

            btnKonselor.setBackgroundTintList(getResources().getColorStateList(R.color.biru_langit));
            btnKonselor.setTextColor(getResources().getColor(R.color.white));

            btnJadwalSaya.setBackgroundTintList(getResources().getColorStateList(R.color.abu_muda));
            btnJadwalSaya.setTextColor(getResources().getColor(R.color.biru_langit));
        } else {
            layoutKonselor.setVisibility(View.GONE);
            layoutJadwal.setVisibility(View.VISIBLE);

            btnJadwalSaya.setBackgroundTintList(getResources().getColorStateList(R.color.biru_langit));
            btnJadwalSaya.setTextColor(getResources().getColor(R.color.white));

            btnKonselor.setBackgroundTintList(getResources().getColorStateList(R.color.abu_muda));
            btnKonselor.setTextColor(getResources().getColor(R.color.biru_langit));
        }
    }

    // ===================== LOAD KONSELOR =====================
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
                listKonselorFull = new ArrayList<>(list);
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

        if (hasActiveBooking) {
            btnLihat.setEnabled(false);
            btnLihat.setBackgroundTintList(getResources().getColorStateList(R.color.abu_muda));
            btnLihat.setText("SUDAH BOOKING");
            btnLihat.setTextColor(getResources().getColor(R.color.grey));
        } else {
            btnLihat.setEnabled(true);
            btnLihat.setBackgroundTintList(getResources().getColorStateList(R.color.biru_langit));
            btnLihat.setText("LIHAT JADWAL");
            btnLihat.setTextColor(getResources().getColor(R.color.white));

            btnLihat.setOnClickListener(v -> {
                Fragment bookingFragment = BookingFragment.newInstance(k.getId(), k.getNama());
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.flfragment, bookingFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        containerKonselor.addView(card);
    }

    private void filterKonselor(String query) {
        containerKonselor.removeAllViews();
        for (Konselor k : listKonselorFull) {
            if (k.getNama().toLowerCase().contains(query.toLowerCase())) {
                addKonselorCard(k);
            }
        }
    }

    // ===================== LOAD JADWAL =====================
    private void loadJadwalSaya() {
        SharedPreferences sp = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);
        String idUser = sp.getString("id_user", null);

        if (!isLoggedIn || idUser == null) {
            Toast.makeText(getContext(), "User belum login!", Toast.LENGTH_SHORT).show();
            tvKosong.setVisibility(View.VISIBLE);
            tvKosong.setText("Silakan login terlebih dahulu");
            hasActiveBooking = false;
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getBookingUser(idUser).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    String status = (String) responseBody.get("status");

                    containerJadwalSaya.removeAllViews();

                    if ("success".equals(status)) {
                        List<Map<String, Object>> bookingList = (List<Map<String, Object>>) responseBody.get("data");

                        if (bookingList != null && !bookingList.isEmpty()) {
                            listJadwalFull = new ArrayList<>(bookingList);
                            tvKosong.setVisibility(View.GONE);
                            hasActiveBooking = true;

                            for (Map<String, Object> booking : bookingList) {
                                addJadwalCard(booking);
                            }
                        } else {
                            tvKosong.setVisibility(View.VISIBLE);
                            tvKosong.setText("Belum ada jadwal konseling");
                            hasActiveBooking = false;
                        }
                    } else {
                        String message = (String) responseBody.get("message");
                        tvKosong.setVisibility(View.VISIBLE);
                        tvKosong.setText(message != null ? message : "Gagal mengambil jadwal");
                        hasActiveBooking = false;
                    }
                } else {
                    Toast.makeText(getContext(), "Response tidak berhasil", Toast.LENGTH_SHORT).show();
                    hasActiveBooking = false;
                }

                if (getView() != null) {
                    loadKonselor();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                hasActiveBooking = false;
                tvKosong.setVisibility(View.VISIBLE);
                tvKosong.setText("Gagal memuat jadwal");
            }
        });
    }

    private void addJadwalCard(Map<String, Object> booking) {
        View card = getLayoutInflater().inflate(R.layout.item_booking_user, null);

        TextView tvNamaKonselor = card.findViewById(R.id.tvNamaKonselor);
        TextView tvTanggalWaktu = card.findViewById(R.id.tvTanggalWaktu);
        TextView tvJenis = card.findViewById(R.id.tvJenisKonseling);

        Button btnBatalkan = card.findViewById(R.id.btnBatalkan);
        Button btnUbah = card.findViewById(R.id.btnUbahJadwal);
        Button btnTestimoni = card.findViewById(R.id.btnTestimoni);

        String namaKonselor = (String) booking.get("nama");
        String tanggalBooking = (String) booking.get("tanggal_booking");
        String jenisKonseling = (String) booking.get("jenis_konseling");
        String idBooking = String.valueOf(booking.get("id_booking"));
        String idJadwal = String.valueOf(booking.get("id_jadwal"));
        String idKonselor = String.valueOf(booking.get("id_konselor"));
        String jamMulai = (String) booking.get("jam_mulai");

        tvNamaKonselor.setText(namaKonselor != null ? namaKonselor : "-");

        String tanggalFormatted = formatTanggal(tanggalBooking);
        String waktuText = jamMulai != null ? jamMulai : "-";

        tvTanggalWaktu.setText("📅 " + tanggalFormatted + " | 🕒 " + waktuText);
        tvJenis.setText(jenisKonseling != null ? jenisKonseling : "-");

        btnBatalkan.setOnClickListener(v -> showDialogBatalJadwal(idBooking, idJadwal));

        btnUbah.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("id_booking", idBooking);
            bundle.putString("tanggal_lama", tanggalBooking);
            bundle.putString("jenis_lama", jenisKonseling);
            bundle.putString("nama_konselor", namaKonselor);
            bundle.putString("id_konselor", idKonselor);
            bundle.putString("jam_mulai", jamMulai);

            Fragment rescheduleFragment = new Reschedule();
            rescheduleFragment.setArguments(bundle);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flfragment, rescheduleFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Tombol Testimoni dengan pengecekan jam 2 jam
        btnTestimoni.setOnClickListener(v -> {
            if (jamMulai == null || tanggalBooking == null) {
                Toast.makeText(getContext(),
                        "Data jadwal tidak lengkap", Toast.LENGTH_SHORT).show();
                return;
            }

            String waktuMulaiStr = tanggalBooking + " " + jamMulai;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            try {
                Date waktuMulai = sdf.parse(waktuMulaiStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(waktuMulai);
                cal.add(Calendar.HOUR_OF_DAY, 2); // Tambah 2 jam durasi
                Date waktuSelesai = cal.getTime();

                if (new Date().before(waktuSelesai)) {
                    Toast.makeText(getContext(),
                            "Dapat diakses setelah konseling selesai",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Format waktu salah", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sp = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("id_konselor", idKonselor);
            editor.apply();

            if (idBooking != null && !idBooking.equals("null")) {
                Fragment fragmentTestimoni = TestimoniFragment.newInstance(idBooking);
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flfragment, fragmentTestimoni)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "ID Booking tidak valid", Toast.LENGTH_SHORT).show();
            }
        });

        containerJadwalSaya.addView(card);
    }

    private void filterJadwal(String query) {
        containerJadwalSaya.removeAllViews();
        boolean adaHasil = false;
        for (Map<String, Object> booking : listJadwalFull) {
            String nama = (String) booking.get("nama");
            if (nama != null && nama.toLowerCase().contains(query.toLowerCase())) {
                addJadwalCard(booking);
                adaHasil = true;
            }
        }

        tvKosong.setVisibility(adaHasil ? View.GONE : View.VISIBLE);
        if (!adaHasil) tvKosong.setText("Tidak ada hasil pencarian");
    }

    private String formatTanggal(String tanggalBooking) {
        if (tanggalBooking != null && !tanggalBooking.isEmpty()) {
            try {
                String[] parts = tanggalBooking.split(" ")[0].split("-");
                if (parts.length == 3) {
                    return parts[2] + "/" + parts[1] + "/" + parts[0];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tanggalBooking != null ? tanggalBooking : "-";
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
                    String errorMsg = "Gagal membatalkan jadwal";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Kesalahan koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
