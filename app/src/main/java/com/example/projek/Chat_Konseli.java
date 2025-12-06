package com.example.projek;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projek.network.ApiClient;
import com.example.projek.network.ApiService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat_Konseli extends AppCompatActivity {

    private static final String TAG = "Chat_Konseli";

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private ImageButton buttonSend, buttonBack;
    private TextView textKonselorName;

    private ChatKonseliAdapter adapter;
    private List<ChatMessage> chatMessages;

    private int id_user;
    private int id_booking;
    private int id_konselor;
    private String konselorName;
    private String tanggalSesi;
    private String jamSelesai;

    private Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean isRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_konseli);

        initViews();

        id_booking = getIntent().getIntExtra("id_booking", 0);
        id_konselor = getIntent().getIntExtra("id_konselor", 0);
        konselorName = getIntent().getStringExtra("konselor_name");

        SharedPreferences sharedPreferences = getSharedPreferences("USER_DATA", MODE_PRIVATE);
        id_user = Integer.parseInt(sharedPreferences.getString("id_user", "0"));

        Log.d(TAG, "User ID: " + id_user + ", Booking ID: " + id_booking + ", Konselor: " + konselorName);

        if (konselorName != null && !konselorName.isEmpty()) {
            textKonselorName.setText(konselorName);
        }

        setupRecyclerView();

        if (id_booking == 0) {
            loadLatestBooking();
        } else {
            // Ambil data tanggal dan jam dari getBookingUser
            loadBookingData();
        }
    }

    private void loadBookingData() {
        Log.d(TAG, "Loading booking data for user: " + id_user);

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

                        if (data != null && data.size() > 0) {
                            // Cari booking yang sesuai dengan id_booking
                            for (Map<String, Object> booking : data) {
                                try {
                                    Object idBookingObj = booking.get("id_booking");
                                    if (idBookingObj == null) idBookingObj = booking.get("id");

                                    if (idBookingObj != null) {
                                        int currentBookingId = Integer.parseInt(idBookingObj.toString());

                                        if (currentBookingId == id_booking) {
                                            // Ambil data tanggal dan jam selesai
                                            tanggalSesi = (String) booking.get("tanggal");
                                            jamSelesai = (String) booking.get("jam_selesai");

                                            Log.d(TAG, "Found booking - Tanggal: " + tanggalSesi + ", Jam Selesai: " + jamSelesai);

                                            // Update konselor name jika belum ada
                                            if (konselorName == null || konselorName.isEmpty()) {
                                                konselorName = (String) booking.get("nama");
                                                if (konselorName != null && !konselorName.isEmpty()) {
                                                    textKonselorName.setText(konselorName);
                                                }
                                            }

                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error processing booking data", e);
                                }
                            }
                        }

                        checkSessionExpiry();
                        loadMessages();
                        startAutoRefresh();

                    } else {
                        Log.e(TAG, "Status tidak success: " + responseBody.get("message"));
                        loadMessages();
                        startAutoRefresh();
                    }
                } else {
                    Log.e(TAG, "Response tidak successful");
                    loadMessages();
                    startAutoRefresh();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Gagal load booking data: " + t.getMessage());
                loadMessages();
                startAutoRefresh();
            }
        });
    }

    private void checkSessionExpiry() {
        if (tanggalSesi == null || jamSelesai == null) {
            Log.w(TAG, "Data tanggal/jam tidak lengkap, tidak bisa cek expiry");
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String dateTimeStr = tanggalSesi + " " + jamSelesai;
            Date waktuSelesai = dateFormat.parse(dateTimeStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(waktuSelesai);
            calendar.add(Calendar.MINUTE, 10);
            Date waktuSelesaiPlus10Menit = calendar.getTime();

            Date sekarang = new Date();

            if (sekarang.after(waktuSelesaiPlus10Menit)) {
                disableChat();
                Toast.makeText(this, "Sesi konseling Anda telah berakhir, Anda tidak dapat mengirim pesan sekarang", Toast.LENGTH_LONG).show();
            } else {
                enableChat();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + e.getMessage());
        }
    }

    private void disableChat() {
        runOnUiThread(() -> {
            buttonSend.setEnabled(false);
            editTextMessage.setEnabled(false);
            editTextMessage.setHint("Chat telah ditutup");
            editTextMessage.setFocusable(false);
            editTextMessage.setClickable(false);

            isRunning = false;
            executor.shutdown();
        });
    }

    private void enableChat() {
        runOnUiThread(() -> {
            buttonSend.setEnabled(true);
            editTextMessage.setEnabled(true);
            editTextMessage.setHint("Ketik pesan...");
            editTextMessage.setFocusable(true);
            editTextMessage.setClickable(true);
        });
    }

    private void loadLatestBooking() {
        Log.d(TAG, "Loading latest booking for user: " + id_user);

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

                        if (data != null && data.size() > 0) {
                            Map<String, Object> latestBooking = data.get(0);

                            try {
                                Object idBookingObj = latestBooking.get("id_booking");
                                if (idBookingObj == null) idBookingObj = latestBooking.get("id");

                                if (idBookingObj != null) {
                                    id_booking = Integer.parseInt(idBookingObj.toString());
                                }

                                Object idKonselorObj = latestBooking.get("id_konselor");
                                if (idKonselorObj != null) {
                                    id_konselor = Integer.parseInt(idKonselorObj.toString());
                                }

                                konselorName = (String) latestBooking.get("nama");
                                if (konselorName == null) {
                                    konselorName = (String) latestBooking.get("konselor_name");
                                }

                                tanggalSesi = (String) latestBooking.get("tanggal");
                                jamSelesai = (String) latestBooking.get("jam_selesai");

                                Log.d(TAG, "Booking loaded - ID: " + id_booking + ", Konselor: " + konselorName +
                                        ", Tanggal: " + tanggalSesi + ", Jam Selesai: " + jamSelesai);

                                if (konselorName != null && !konselorName.isEmpty()) {
                                    textKonselorName.setText(konselorName);
                                }

                                if (id_booking > 0) {
                                    checkSessionExpiry();
                                    loadMessages();
                                    startAutoRefresh();
                                } else {
                                    Toast.makeText(Chat_Konseli.this, "ID Booking tidak valid", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing booking data", e);
                                Toast.makeText(Chat_Konseli.this, "Error parsing data booking", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(Chat_Konseli.this, "Tidak ada booking ditemukan", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(Chat_Konseli.this, "Error: " + responseBody.get("message"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(Chat_Konseli.this, "Gagal mengambil data booking", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(Chat_Konseli.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChatKonseli);
        editTextMessage = findViewById(R.id.editTextMessageKonseli);
        buttonSend = findViewById(R.id.buttonSendKonseli);
        buttonBack = findViewById(R.id.buttonBackKonseli);
        textKonselorName = findViewById(R.id.chatkoselor);

        buttonBack.setOnClickListener(v -> finish());
        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        adapter = new ChatKonseliAdapter(chatMessages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(adapter);
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (id_booking == 0) {
            Toast.makeText(this, "Booking ID tidak valid, mencoba mengambil ulang...", Toast.LENGTH_SHORT).show();
            loadLatestBooking();
            return;
        }

        if (!buttonSend.isEnabled()) {
            Toast.makeText(this, "Chat telah ditutup", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Sending message - Booking: " + id_booking + ", User: " + id_user + ", Message: " + message);

        buttonSend.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.sendMessage(id_booking, id_user, message);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                buttonSend.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    Log.d(TAG, "Send response: " + responseBody.toString());

                    if ("success".equals(responseBody.get("status"))) {
                        editTextMessage.setText("");

                        ChatMessage newMessage = new ChatMessage(message, true);
                        adapter.addMessage(newMessage);
                        recyclerViewChat.smoothScrollToPosition(adapter.getItemCount() - 1);

                        loadMessages();

                    } else {
                        String errorMsg = (String) responseBody.get("message");
                        Toast.makeText(Chat_Konseli.this, "Gagal mengirim: " + errorMsg, Toast.LENGTH_SHORT).show();

                        if (errorMsg != null && errorMsg.contains("id_booking")) {
                            loadLatestBooking();
                        }
                    }
                } else {
                    Toast.makeText(Chat_Konseli.this, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                buttonSend.setEnabled(true);
                Toast.makeText(Chat_Konseli.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        if (id_booking == 0) {
            Log.w(TAG, "Cannot load messages - booking ID is 0");
            return;
        }

        Log.d(TAG, "Loading messages for booking: " + id_booking);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getMessages(id_booking);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    String status = (String) responseBody.get("status");

                    if ("success".equals(status)) {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

                        List<ChatMessage> messages = new ArrayList<>();

                        if (data != null) {
                            for (Map<String, Object> messageObj : data) {
                                String pengirim = (String) messageObj.get("pengirim");
                                String pesan = (String) messageObj.get("pesan");

                                if (pesan != null) {
                                    boolean isUserMessage = "user".equals(pengirim);
                                    ChatMessage chatMessage = new ChatMessage(pesan, isUserMessage);
                                    messages.add(chatMessage);
                                }
                            }
                        }

                        adapter.updateData(messages);
                        if (adapter.getItemCount() > 0) {
                            recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);
                        }

                        Log.d(TAG, "Loaded " + messages.size() + " messages");
                    } else {
                        String errorMsg = (String) responseBody.get("message");
                        Toast.makeText(Chat_Konseli.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Chat_Konseli.this, "Gagal memuat pesan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(Chat_Konseli.this, "Gagal memuat pesan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startAutoRefresh() {
        if (!isRunning) return;

        executor.execute(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(3000);
                    if (isRunning) {
                        handler.post(() -> {
                            loadMessages();
                            if (tanggalSesi != null && jamSelesai != null) {
                                checkSessionExpiry();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
        executor.shutdown();
    }
}