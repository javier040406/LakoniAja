package com.example.projek;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints.DateValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reschedule extends Fragment {

    private String idBooking, idKonselor, namaKonselor;
    private String tanggalLama, jenisLama;

    private Spinner spinnerWaktu, spinnerSesi;
    private TextView txtTanggal;
    private Button btnReschedule;

    private List<Jadwal> jadwalList = new ArrayList<>();
    private Set<String> tanggalTersedia = new HashSet<>();

    public Reschedule() {}

    public static Reschedule newInstance(String idBooking, String tanggalLama,
                                         String jenisLama, String namaKonselor,
                                         String idKonselor) {
        Reschedule fragment = new Reschedule();
        Bundle args = new Bundle();
        args.putString("id_booking", idBooking);
        args.putString("tanggal_lama", tanggalLama);
        args.putString("jenis_lama", jenisLama);
        args.putString("nama_konselor", namaKonselor);
        args.putString("id_konselor", idKonselor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idBooking = getArguments().getString("id_booking");
            tanggalLama = getArguments().getString("tanggal_lama");
            jenisLama = getArguments().getString("jenis_lama");
            namaKonselor = getArguments().getString("nama_konselor");
            idKonselor = getArguments().getString("id_konselor");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reschedule, container, false);

        txtTanggal = view.findViewById(R.id.txt_tanggalreschedule);
        spinnerWaktu = view.findViewById(R.id.spinnerjamreschedule);
        spinnerSesi = view.findViewById(R.id.spinnersesireschedule);
        btnReschedule = view.findViewById(R.id.buttonreschedule);

        TextView labelKonselor = view.findViewById(R.id.label_konselorr);
        labelKonselor.setText(namaKonselor);

        ArrayAdapter<String> adapterSesi = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Offline", "Online"});
        adapterSesi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSesi.setAdapter(adapterSesi);

        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        txtTanggal.setOnClickListener(v -> showDatePicker());

        loadJadwalFromAPI();
        checkRescheduleStatus();

        btnReschedule.setOnClickListener(v -> prosesReschedule());

        return view;
    }

    private void showDatePicker() {

        DateValidator validator = new DateValidator() {
            @Override
            public boolean isValid(long date) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String d = format.format(date);
                return tanggalTersedia.contains(d);
            }

            @Override
            public int describeContents() { return 0; }

            @Override
            public void writeToParcel(android.os.Parcel dest, int flags) {}
        };

        CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();
        constraints.setValidator(validator);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal Tersedia")
                .setCalendarConstraints(constraints.build())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);

            String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(calendar.getTime());

            txtTanggal.setText(selectedDate);
            loadWaktuByTanggal(selectedDate);
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void checkRescheduleStatus() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> call = api.checkRescheduleStatus(idBooking);
        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getRescheduleDone()) {
                        btnReschedule.setEnabled(false);
                        btnReschedule.setAlpha(0.5f);
                        Toast.makeText(getContext(), "Anda sudah pernah mereschedule", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal cek status reschedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadJadwalFromAPI() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<JadwalResponse> call = apiService.getJadwalKonselor(idKonselor);
        call.enqueue(new Callback<JadwalResponse>() {
            @Override
            public void onResponse(Call<JadwalResponse> call, Response<JadwalResponse> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                jadwalList.clear();
                tanggalTersedia.clear();

                for (Jadwal j : response.body().getData()) {
                    if ("tersedia".equalsIgnoreCase(j.getStatus())) {
                        jadwalList.add(j);
                        tanggalTersedia.add(j.getTanggal());
                    }
                }
            }

            @Override
            public void onFailure(Call<JadwalResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadWaktuByTanggal(String tanggal) {
        List<String> waktuList = new ArrayList<>();
        for (Jadwal j : jadwalList) {
            if (j.getTanggal().equals(tanggal) && j.getStatus().equalsIgnoreCase("tersedia")) {
                waktuList.add(j.getJam_mulai());
            }
        }
        if (waktuList.isEmpty()) waktuList.add("Tidak tersedia");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, waktuList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWaktu.setAdapter(adapter);
    }

    private void prosesReschedule() {
        String tanggal = txtTanggal.getText().toString();
        if (tanggal.isEmpty()) {
            Toast.makeText(getContext(), "Pilih tanggal terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerWaktu.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Pilih jam terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String jam = spinnerWaktu.getSelectedItem().toString();
        if ("Tidak tersedia".equals(jam)) {
            Toast.makeText(getContext(), "Tidak ada jadwal di tanggal ini", Toast.LENGTH_SHORT).show();
            return;
        }

        String sesi = spinnerSesi.getSelectedItem().toString();
        showKonfirmasiDialogReschedule(tanggal, jam, sesi);
    }

    private void showKonfirmasiDialogReschedule(String tanggal, String jam, String sesi) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_konfirmasi_reschedule);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText("Anda akan mengubah jadwal Konseling dengan " + namaKonselor +
                "\nTanggal: " + tanggal +
                "\nJam: " + jam +
                "\nSesi: " + sesi +
                "\n\nApakah sudah benar?");

        Button btnYa = dialog.findViewById(R.id.btn_ya);
        btnYa.setOnClickListener(v -> {
            dialog.dismiss();
            kirimReschedule(tanggal, jam, sesi);
        });

        Button btnBatal = dialog.findViewById(R.id.btn_batal);
        btnBatal.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void kirimReschedule(String tanggal, String jam, String sesi) {
        String tanggalBookingBaru = tanggal + " " + jam + ":00";

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<BasicResponse> call = apiService.rescheduleBooking(
                idBooking,
                tanggalLama,
                jenisLama,
                tanggalBookingBaru,
                sesi,
                idKonselor,
                jam
        );

        call.enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BasicResponse br = response.body();
                    Toast.makeText(getContext(), br.getMessage(), Toast.LENGTH_LONG).show();
                    if (br.isStatus()) {
                        btnReschedule.setEnabled(false);
                        btnReschedule.setAlpha(0.5f);
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                } else {
                    Toast.makeText(getContext(), "Response tidak valid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() { super.onResume(); hideNavbar(); }
    @Override
    public void onPause() { super.onPause(); showNavbar(); }

    private void hideNavbar() {
        BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
        if (nav != null) nav.setVisibility(View.GONE);
    }
    private void showNavbar() {
        BottomNavigationView nav = requireActivity().findViewById(R.id.bottom_navigation);
        if (nav != null) nav.setVisibility(View.VISIBLE);
    }
}
