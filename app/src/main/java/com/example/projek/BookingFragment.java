package com.example.projek;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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

import androidx.fragment.app.Fragment;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingFragment extends Fragment {

    private Spinner spinnerWaktu, spinnerSesi;
    private TextView spinnerTanggal;
    private String idKonselor, namaKonselor, idUser;
    private List<Jadwal> jadwalList = new ArrayList<>();
    private Button btnBooking;

    // list millis (UTC-midnight) yang tersedia dan set untuk lookup cepat
    private List<Long> tanggalTersediaMillis = new ArrayList<>();
    private Set<Long> tanggalTersediaSet = new HashSet<>();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        spinnerTanggal = view.findViewById(R.id.txt_tanggal);
        spinnerWaktu = view.findViewById(R.id.spinnerjam);
        spinnerSesi = view.findViewById(R.id.spinnersesi);
        btnBooking = view.findViewById(R.id.buttonbooking);

        ArrayAdapter<String> adapterSesi = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Offline", "Online"});
        adapterSesi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSesi.setAdapter(adapterSesi);

        TextView labelNamaKonselor = view.findViewById(R.id.label_konselor);
        labelNamaKonselor.setText(namaKonselor);

        ImageView btnBack = view.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setVisibility(View.VISIBLE);
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        spinnerTanggal.setOnClickListener(v -> showDatePicker());

        loadJadwalFromAPI();
        checkUserBookingStatus();
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
        api.cekBookingAktif(idUser).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BasicResponse br = response.body();
                    if (!br.isStatus()) {
                        btnBooking.setEnabled(false);
                        btnBooking.setAlpha(0.5f);
                        Toast.makeText(getContext(), br.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        btnBooking.setEnabled(true);
                        btnBooking.setAlpha(1f);
                    }
                } else {
                    // konservatif: disable jika response bermasalah
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
        apiService.getJadwalKonselor(idKonselor).enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(Call<JadwalResponse> call, Response<JadwalResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Gagal memuat jadwal", Toast.LENGTH_SHORT).show();
                    return;
                }

                jadwalList.clear();
                tanggalTersediaMillis.clear();
                tanggalTersediaSet.clear();

                // Parsing tanggal string (yyyy-MM-dd) -> normalisasi ke UTC-midnight
                for (Jadwal j : response.body().getData()) {
                    String status = j.getStatus() == null ? "" : j.getStatus().trim();
                    if ("tersedia".equalsIgnoreCase(status)) {
                        jadwalList.add(j);
                        try {
                            String tanggalStr = j.getTanggal(); // expected "yyyy-MM-dd"
                            if (tanggalStr != null && !tanggalStr.isEmpty()) {
                                long utcMidnight = dateStringToUtcMidnightMillis(tanggalStr);
                                if (!tanggalTersediaSet.contains(utcMidnight)) {
                                    tanggalTersediaMillis.add(utcMidnight);
                                    tanggalTersediaSet.add(utcMidnight);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("BookingDebug", "Parse tanggal error: " + e.getMessage());
                        }
                    }
                }

                // Optional: set default tanggal (first available) ke spinnerTanggal
                if (!tanggalTersediaMillis.isEmpty() && (spinnerTanggal.getText() == null || spinnerTanggal.getText().toString().isEmpty())) {
                    // format dengan UTC timezone agar konsisten
                    SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    out.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String first = out.format(new Date(tanggalTersediaMillis.get(0)));
                    spinnerTanggal.setText(first);
                    loadWaktuByTanggal(first);
                }
            }

            @Override
            public void onFailure(Call<JadwalResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        if (tanggalTersediaMillis.isEmpty()) {
            Toast.makeText(getContext(), "Tidak ada tanggal tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        CalendarConstraints.DateValidator validator = new AllowedDatesValidator(tanggalTersediaMillis);

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(validator)
                .build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Konseling")
                // set selection ke tanggal pertama yang tersedia supaya tidak default ke today
                .setSelection(tanggalTersediaMillis.get(0))
                .setCalendarConstraints(constraints)
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            // selection adalah millis yang dikembalikan oleh date picker (UTC-midnight)
            long selectionUtc = normalizeToUtcMidnight(selection);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String selectedDate = sdf.format(new Date(selectionUtc));
            spinnerTanggal.setText(selectedDate);
            loadWaktuByTanggal(selectedDate);
        });

        datePicker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
    }

    private void loadWaktuByTanggal(String tanggal) {
        List<String> waktuList = new ArrayList<>();
        for (Jadwal j : jadwalList) {
            if (j.getTanggal() != null && j.getTanggal().equals(tanggal) && "tersedia".equalsIgnoreCase(j.getStatus())) {
                waktuList.add(j.getJam_mulai());
            }
        }

        if (waktuList.isEmpty()) waktuList.add("Tidak tersedia");

        spinnerWaktu.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, waktuList));
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
        if ("Tidak tersedia".equals(waktu)) {
            Toast.makeText(getContext(), "Tidak ada jadwal di tanggal ini", Toast.LENGTH_SHORT).show();
            return;
        }

        String sesi = spinnerSesi.getSelectedItem().toString();
        if (idUser == null || idUser.isEmpty()) {
            Toast.makeText(getContext(), "ID User tidak ditemukan, login ulang!", Toast.LENGTH_SHORT).show();
            return;
        }

        String idJadwal = null;
        for (Jadwal j : jadwalList) {
            if (j.getTanggal() != null && j.getTanggal().equals(tanggal) && j.getJam_mulai().equals(waktu)) {
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

        dialog.findViewById(R.id.btn_ya).setOnClickListener(v -> {
            dialog.dismiss();
            kirimBooking(idUser, sesi, tanggal, jamMulai, idJadwal);
        });

        dialog.findViewById(R.id.btn_batal).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void kirimBooking(String idUser, String sesi, String tanggal, String jamMulai, String idJadwal) {
        ApiClient.getClient().create(ApiService.class)
                .bookingPHP(idUser, sesi, tanggal, jamMulai, idJadwal)
                .enqueue(new Callback<BookingResponse>() {
                    @Override
                    public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BookingResponse br = response.body();
                            Toast.makeText(getContext(), br.getMessage(), Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.flfragment, new Jadwal_Fragment())
                                    .commit();
                        } else {
                            Toast.makeText(getContext(), "Gagal booking", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BookingResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Mengonversi string "yyyy-MM-dd" menjadi millis pada UTC midnight (00:00 UTC).
     * Ini memastikan nilai yang digunakan cocok dengan yang dikembalikan MaterialDatePicker.
     */
    private long dateStringToUtcMidnightMillis(String yyyyMMdd) throws Exception {
        // parsing manual agar tidak tergantung timezone device
        String[] parts = yyyyMMdd.split("-");
        if (parts.length < 3) throw new IllegalArgumentException("Format tanggal invalid: " + yyyyMMdd);
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1; // Calendar bulan 0-based
        int day = Integer.parseInt(parts[2]);

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        // jam/min/sec already zero karena cal.clear()
        return cal.getTimeInMillis();
    }

    /**
     * Normalize value (mis. selection long) ke UTC-midnight.
     * MaterialDatePicker biasanya sudah mengirim nilai midnight UTC, namun tetap kita normalize.
     */
    private long normalizeToUtcMidnight(long millis) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    // =======================
    // Validator kustom: hanya izinkan tanggal yang ada di list millis (UTC-midnight)
    // harus Parcelable karena CalendarConstraints.DateValidator extends Parcelable
    // =======================
    public static class AllowedDatesValidator implements CalendarConstraints.DateValidator {

        private final long[] allowedMillis;
        private transient Set<Long> allowedSet;

        public AllowedDatesValidator(List<Long> allowedList) {
            // gunakan set unik
            allowedSet = new HashSet<>(allowedList);
            allowedMillis = new long[allowedSet.size()];
            int i = 0;
            for (Long l : allowedSet) allowedMillis[i++] = l;
        }

        protected AllowedDatesValidator(Parcel in) {
            allowedMillis = in.createLongArray();
            allowedSet = new HashSet<>();
            if (allowedMillis != null) {
                for (long l : allowedMillis) allowedSet.add(l);
            }
        }

        @Override
        public boolean isValid(long date) {
            // date parameter adalah UTC-midnight dari MaterialDatePicker
            return allowedSet != null && allowedSet.contains(date);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLongArray(allowedMillis);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<AllowedDatesValidator> CREATOR = new Creator<AllowedDatesValidator>() {
            @Override
            public AllowedDatesValidator createFromParcel(Parcel in) {
                return new AllowedDatesValidator(in);
            }

            @Override
            public AllowedDatesValidator[] newArray(int size) {
                return new AllowedDatesValidator[size];
            }
        };
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
