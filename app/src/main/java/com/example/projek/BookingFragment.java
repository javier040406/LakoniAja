package com.example.projek;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingFragment extends Fragment {

    private Spinner spinnerWaktu, spinnerSesi;
    private TextView spinnerTanggal;
    private String idKonselor, namaKonselor, idUser;
    private List<Jadwal> jadwalList = new ArrayList<>();
    private Button btnBooking;

    public BookingFragment() {}

    public static BookingFragment newInstance(String idKonselor, String namaKonselor) {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putString("id_konselor", idKonselor);
        args.putString("nama_konselor", namaKonselor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idKonselor = getArguments().getString("id_konselor");
            namaKonselor = getArguments().getString("nama_konselor");
        }

        SharedPreferences sp = requireActivity().getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
        idUser = sp.getString("id_user", null);
        if (idUser == null) {
            Log.e("BookingDebug", "ID User kosong saat masuk BookingFragment");
        } else {
            Log.d("BookingDebug", "ID User saat masuk BookingFragment: " + idUser);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        spinnerTanggal = view.findViewById(R.id.txt_tanggal);
        spinnerWaktu = view.findViewById(R.id.spinnerjam);
        spinnerSesi = view.findViewById(R.id.spinnersesi);
        btnBooking = view.findViewById(R.id.buttonbooking);

        // Spinner Sesi
        ArrayAdapter<String> adapterSesi = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Offline", "Online"});
        adapterSesi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSesi.setAdapter(adapterSesi);

        // Label nama konselor
        TextView labelNamaKonselor = view.findViewById(R.id.label_konselor);
        labelNamaKonselor.setText(namaKonselor);

        // Tombol back
        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Pilih tanggal
        spinnerTanggal.setOnClickListener(v -> showDatePicker());

        // Load jadwal tersedia
        loadJadwalFromAPI();

        // Cek apakah user sudah punya booking aktif
        checkUserBookingStatus();

        // Tombol booking
        btnBooking.setOnClickListener(v -> prosesBooking());

        return view;
    }

    // ================= LOGIC CEK BOOKING AKTIF =================
    private void checkUserBookingStatus() {
        if (idUser == null) {
            btnBooking.setEnabled(false);
            btnBooking.setAlpha(0.5f);
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> call = api.cekBookingAktif(idUser); // API PHP cekBookingAktif.php
        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BasicResponse br = response.body();
                    if (!br.isStatus()) {
                        // user sudah punya booking aktif
                        btnBooking.setEnabled(false);
                        btnBooking.setAlpha(0.5f);
                        Toast.makeText(getContext(), br.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        btnBooking.setEnabled(true);
                        btnBooking.setAlpha(1f);
                    }
                } else {
                    btnBooking.setEnabled(false);
                    btnBooking.setAlpha(0.5f);
                    Toast.makeText(getContext(), "Gagal cek booking aktif", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                btnBooking.setEnabled(false);
                btnBooking.setAlpha(0.5f);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= LOGIC BOOKING =================
    private void loadJadwalFromAPI() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<JadwalResponse> call = apiService.getJadwalKonselor(idKonselor);
        call.enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(Call<JadwalResponse> call, Response<JadwalResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Gagal memuat jadwal", Toast.LENGTH_SHORT).show();
                    return;
                }
                jadwalList.clear();
                for (Jadwal j : response.body().getData()) {
                    String statusFix = (j.getStatus() == null) ? "" : j.getStatus().trim();
                    if (statusFix.equalsIgnoreCase("tersedia")) {
                        jadwalList.add(j);
                    }
                }
            }

            @Override
            public void onFailure(Call<JadwalResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(
                getContext(),
                (view, year, month, day) -> {
                    String selectedDate = year + "-" + (month + 1) + "-" + day;
                    spinnerTanggal.setText(selectedDate);
                    loadWaktuByTanggal(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show();
    }

    private void loadWaktuByTanggal(String tanggal) {
        List<String> waktuList = new ArrayList<>();
        for (Jadwal j : jadwalList) {
            if (j.getTanggal().equals(tanggal) && j.getStatus().equalsIgnoreCase("tersedia")) {
                waktuList.add(j.getJam_mulai());
            }
        }
        if (waktuList.isEmpty()) waktuList.add("Tidak tersedia");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, waktuList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWaktu.setAdapter(adapter);
    }

    private void prosesBooking() {
        String tanggal = spinnerTanggal.getText().toString();
        if (tanggal.isEmpty()) {
            Toast.makeText(getContext(), "Pilih tanggal terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spinnerWaktu.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Pilih waktu terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }
        String waktu = spinnerWaktu.getSelectedItem().toString();
        if (waktu.equals("Tidak tersedia")) {
            Toast.makeText(getContext(), "Tidak ada jadwal di tanggal ini", Toast.LENGTH_SHORT).show();
            return;
        }
        String sesi = spinnerSesi.getSelectedItem().toString();
        if (idUser == null || idUser.isEmpty()) {
            Toast.makeText(getContext(), "ID User tidak ditemukan, login ulang!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Dapatkan id_jadwal berdasarkan tanggal & jam
        String idJadwal = null;
        for (Jadwal j : jadwalList) {
            if (j.getTanggal().equals(tanggal) && j.getJam_mulai().equals(waktu)) {
                idJadwal = String.valueOf(j.getId_jadwal());
                break;
            }
        }
        if (idJadwal == null) {
            Toast.makeText(getContext(), "Jadwal tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }
        showKonfirmasiDialog(idUser, sesi, tanggal, waktu, idJadwal);
    }

    private void showKonfirmasiDialog(String idUser, String sesi, String tanggal, String jamMulai, String idJadwal) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_konfirmasi_booking);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText("Anda akan melakukan Konseling dengan " + namaKonselor +
                "\nTanggal: " + tanggal +
                "\nJam: " + jamMulai +
                "\nSesi: " + sesi +
                "\n\nApakah jadwal ini sudah benar?");

        Button btnYa = dialog.findViewById(R.id.btn_ya);
        btnYa.setOnClickListener(v -> {
            dialog.dismiss();
            kirimBooking(idUser, sesi, tanggal, jamMulai, idJadwal);
        });

        Button btnBatal = dialog.findViewById(R.id.btn_batal);
        btnBatal.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void kirimBooking(String idUser, String sesi, String tanggal, String jamMulai, String idJadwal) {
        ApiService api = ApiClient.getClient().create(ApiService.class);

        Call<BookingResponse> call = api.bookingPHP(idUser, sesi, tanggal, jamMulai, idJadwal);
        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BookingResponse br = response.body();
                    if (br.isStatus()) {
                        Toast.makeText(getContext(), br.getMessage(), Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flfragment, new Jadwal_Fragment())
                                .commit();
                    } else {
                        Toast.makeText(getContext(), "Gagal: " + br.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Response tidak valid", Toast.LENGTH_SHORT).show();
                    try {
                        if (response.errorBody() != null)
                            Log.e("BookingDebug", response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("BookingDebug", "onFailure", t);
            }
        });
    }

    // ===== LOGIC HIDE/SHOW NAVBAR =====
    @Override
    public void onResume() {
        super.onResume();
        hideAppNavbar();
    }

    @Override
    public void onPause() {
        super.onPause();
        showAppNavbar();
    }

    private void hideAppNavbar() {
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) bottomNav.setVisibility(View.GONE);
    }

    private void showAppNavbar() {
        BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
        if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
    }
}
