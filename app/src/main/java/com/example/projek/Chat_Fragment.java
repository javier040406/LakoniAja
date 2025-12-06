package com.example.projek;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.util.Log;

import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.Map;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class Chat_Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private LinearLayout konselorLayout;
    private TextView konselorText;
    private int id_user;
    private Map<String, Object> currentActiveBooking; // Simpan booking aktif

    public Chat_Fragment() {}

    public static Chat_Fragment newInstance(String param1, String param2) {
        Chat_Fragment fragment = new Chat_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_, container, false);

        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences("USER_DATA", getActivity().MODE_PRIVATE);
        id_user = Integer.parseInt(sharedPreferences.getString("id_user", "0"));

        initViews(view);
        checkUserBooking();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserBooking();
    }

    private void initViews(View view) {
        LinearLayout chatBotLayout = view.findViewById(R.id.silakonchat);
        chatBotLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatBot.class);
            startActivity(intent);
        });

        konselorLayout = view.findViewById(R.id.konselor_chat);
        konselorText = view.findViewById(R.id.konselor_text);

        // Set click listener untuk konselorLayout
        konselorLayout.setOnClickListener(v -> {
            if (currentActiveBooking != null) {
                openChatWithKonselor(currentActiveBooking);
            } else {
                Toast.makeText(getActivity(), "Tidak ada sesi konseling aktif", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserBooking() {
        if (id_user == 0) {
            konselorLayout.setVisibility(View.GONE);
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getBookingUser(String.valueOf(id_user));

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    String status = (String) responseBody.get("status");

                    if ("success".equals(status)) {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

                        Map<String, Object> activeBooking = findActiveBooking(data);
                        currentActiveBooking = activeBooking; // Simpan booking aktif

                        if (activeBooking != null) {
                            konselorLayout.setVisibility(View.VISIBLE);
                            setupKonselorData(activeBooking);
                        } else {
                            konselorLayout.setVisibility(View.GONE);
                        }
                    } else {
                        konselorLayout.setVisibility(View.GONE);
                    }
                } else {
                    konselorLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                konselorLayout.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Gagal memeriksa booking", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map<String, Object> findActiveBooking(List<Map<String, Object>> bookings) {
        if (bookings == null || bookings.isEmpty()) return null;

        for (Map<String, Object> booking : bookings) {
            String jenisKonseling = (String) booking.get("jenis_konseling");
            boolean isOnline = jenisKonseling != null && "Online".equalsIgnoreCase(jenisKonseling);

            if (!isOnline) {
                continue;
            }

            Object idBookingObj = booking.get("id_booking");
            if (idBookingObj == null || idBookingObj.toString().equals("null")) {
                continue;
            }

            String statusJadwal = (String) booking.get("status_jadwal");
            boolean isSelesai = "selesai".equalsIgnoreCase(statusJadwal);

            boolean within24Hours = isWithin24HoursAfterSession(booking);

            if (!isSelesai) {
                return booking;
            } else if (isSelesai && within24Hours) {
                return booking;
            }
        }

        return null;
    }

    private boolean isWithin24HoursAfterSession(Map<String, Object> booking) {
        try {
            Object tanggalObj = booking.get("tanggal");
            Object jamSelesaiObj = booking.get("jam_selesai");

            if (tanggalObj == null || jamSelesaiObj == null) {
                return false;
            }

            String tanggalStr = tanggalObj.toString();
            String jamSelesaiStr = jamSelesaiObj.toString();

            // FIX: Handle format jam "24:10:00" menjadi "00:10:00" (hari berikutnya)
            Date waktuSelesai = parseDateTimeWithMidnightFix(tanggalStr, jamSelesaiStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(waktuSelesai);
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            Date waktuSelesaiPlus24Jam = calendar.getTime();

            Date sekarang = new Date();

            return !sekarang.after(waktuSelesaiPlus24Jam);

        } catch (Exception e) {
            Log.e("Chat_Fragment", "Error in isWithin24HoursAfterSession: " + e.getMessage());
            return false;
        }
    }

    private Date parseDateTimeWithMidnightFix(String tanggal, String jam) throws Exception {
        // Handle jam seperti "24:10:00" menjadi "00:10:00" (hari berikutnya)
        String[] jamParts = jam.split(":");
        if (jamParts.length >= 1) {
            try {
                int jamInt = Integer.parseInt(jamParts[0]);

                if (jamInt >= 24) {
                    // Konversi ke hari berikutnya
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date originalDate = dateFormat.parse(tanggal);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(originalDate);
                    calendar.add(Calendar.DAY_OF_MONTH, 1); // Tambah 1 hari

                    tanggal = dateFormat.format(calendar.getTime());
                    jam = String.format("%02d:%s:%s",
                            jamInt % 24,
                            jamParts.length > 1 ? jamParts[1] : "00",
                            jamParts.length > 2 ? jamParts[2] : "00");
                }
            } catch (NumberFormatException e) {
                // Jika parsing jam gagal, gunakan jam asli
                Log.w("Chat_Fragment", "Invalid time format: " + jam);
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.parse(tanggal + " " + jam);
    }

    private void setupKonselorData(Map<String, Object> booking) {
        if (booking == null) {
            konselorLayout.setVisibility(View.GONE);
            return;
        }

        try {
            String konselorName = (String) booking.get("nama");
            if (konselorName == null) konselorName = "Konselor";

            // HANYA TAMPILKAN NAMA KONSELOR SAJA, TANPA STATUS
            konselorText.setText(konselorName);

        } catch (Exception e) {
            konselorText.setText("Konselor");
            konselorLayout.setVisibility(View.GONE);
            Log.e("Chat_Fragment", "Error in setupKonselorData: " + e.getMessage());
        }
    }

    private void openChatWithKonselor(Map<String, Object> booking) {
        if (booking == null) {
            Toast.makeText(getActivity(), "Data booking tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi apakah sesi aktif - sekarang selalu aktif jika booking ditemukan
        if (!isSessionActive(booking)) {
            // HANYA KIRIM TOAST JIKA TIDAK AKTIF
            Toast.makeText(getActivity(), "Sesi konseling belum dimulai", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Ambil data dengan validasi
            int bookingId = 0;
            Object idBookingObj = booking.get("id_booking");
            if (idBookingObj != null) {
                bookingId = Integer.parseInt(idBookingObj.toString());
            }

            // AMBIL NAMA LANGSUNG DARI DATA BOOKING
            String konselorName = "Konselor";
            Object namaObj = booking.get("nama");
            if (namaObj != null) {
                konselorName = namaObj.toString();
            }

            int konselorId = 0;
            Object idKonselorObj = booking.get("id_konselor");
            if (idKonselorObj != null) {
                konselorId = Integer.parseInt(idKonselorObj.toString());
            }

            // Validasi data minimal
            if (bookingId == 0 || konselorId == 0) {
                Toast.makeText(getActivity(), "Data booking tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("Chat_Fragment", "Opening chat - Booking ID: " + bookingId +
                    ", Konselor ID: " + konselorId + ", Name: " + konselorName);

            // Buka Chat_Konseli dengan data
            Intent intent = new Intent(getActivity(), Chat_Konseli.class);
            intent.putExtra("id_booking", bookingId);
            intent.putExtra("id_konselor", konselorId);
            intent.putExtra("konselor_name", konselorName);

            startActivity(intent);

        } catch (Exception e) {
            Log.e("Chat_Fragment", "Error opening chat: " + e.getMessage(), e);
            Toast.makeText(getActivity(), "Error membuka chat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isSessionActive(Map<String, Object> booking) {
        try {
            String statusJadwal = (String) booking.get("status_jadwal");
            boolean isSelesai = "selesai".equalsIgnoreCase(statusJadwal);

            if (isSelesai) {
                return isWithin24HoursAfterSession(booking);
            }

            Object tanggalObj = booking.get("tanggal");
            Object jamMulaiObj = booking.get("jam_mulai");

            if (tanggalObj == null || jamMulaiObj == null) {
                return false;
            }

            String tanggalStr = tanggalObj.toString();
            String jamMulaiStr = jamMulaiObj.toString();

            // FIX: Gunakan fungsi yang sama untuk parsing waktu
            Date tanggalMulai = parseDateTimeWithMidnightFix(tanggalStr, jamMulaiStr);
            Date sekarang = new Date();

            return !sekarang.before(tanggalMulai);

        } catch (Exception e) {
            Log.e("Chat_Fragment", "Error in isSessionActive: " + e.getMessage());
            return false;
        }
    }
}