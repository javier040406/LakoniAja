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

        if (konselorName != null && !konselorName.isEmpty()) {
            textKonselorName.setText(konselorName);
        }

        setupRecyclerView();

        if (id_booking == 0) {
            loadLatestBooking();
        } else {
            loadBookingData();
        }
    }

    private void loadBookingData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getBookingUser(String.valueOf(id_user));

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

                    if ("success".equals(responseBody.get("status")) && data != null && !data.isEmpty()) {
                        for (Map<String, Object> booking : data) {
                            Object idBookingObj = booking.get("id_booking");
                            if (idBookingObj == null) idBookingObj = booking.get("id");

                            if (idBookingObj != null) {
                                int currentBookingId = Integer.parseInt(idBookingObj.toString());
                                if (currentBookingId == id_booking) {
                                    tanggalSesi = (String) booking.get("tanggal");
                                    jamSelesai = (String) booking.get("jam_selesai");

                                    if (konselorName == null || konselorName.isEmpty()) {
                                        konselorName = (String) booking.get("nama");
                                        textKonselorName.setText(konselorName);
                                    }
                                    break;
                                }
                            }
                        }

                        checkSessionExpiry();
                        loadMessages();
                        startAutoRefresh();
                    } else {
                        loadMessages();
                        startAutoRefresh();
                    }
                } else {
                    loadMessages();
                    startAutoRefresh();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                loadMessages();
                startAutoRefresh();
            }
        });
    }

    private void checkSessionExpiry() {
        if (tanggalSesi == null || jamSelesai == null) return;

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
            } else {
                enableChat();
            }

        } catch (Exception ignored) {}
    }

    private void disableChat() {
        runOnUiThread(() -> {
            buttonSend.setEnabled(false);
            editTextMessage.setEnabled(false);
            editTextMessage.setHint("Chat telah ditutup");
            isRunning = false;
            executor.shutdown();
        });
    }

    private void enableChat() {
        runOnUiThread(() -> {
            buttonSend.setEnabled(true);
            editTextMessage.setEnabled(true);
            editTextMessage.setHint("Ketik pesan...");
        });
    }

    private void loadLatestBooking() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getBookingUser(String.valueOf(id_user));

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

                    if ("success".equals(responseBody.get("status")) && data != null && !data.isEmpty()) {
                        Map<String, Object> latestBooking = data.get(0);

                        Object idBookingObj = latestBooking.get("id_booking");
                        if (idBookingObj == null) idBookingObj = latestBooking.get("id");
                        id_booking = Integer.parseInt(idBookingObj.toString());

                        Object idKonselorObj = latestBooking.get("id_konselor");
                        if (idKonselorObj != null) id_konselor = Integer.parseInt(idKonselorObj.toString());

                        konselorName = (String) latestBooking.get("nama");
                        tanggalSesi = (String) latestBooking.get("tanggal");
                        jamSelesai = (String) latestBooking.get("jam_selesai");

                        textKonselorName.setText(konselorName);

                        if (id_booking > 0) {
                            checkSessionExpiry();
                            loadMessages();
                            startAutoRefresh();
                        } else {
                            finish();
                        }

                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
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

        if (message.isEmpty()) return;
        if (id_booking == 0) return;
        if (!buttonSend.isEnabled()) return;

        buttonSend.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.sendMessage(id_booking, id_user, id_konselor, message);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                buttonSend.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();

                    if ("success".equals(body.get("status"))) {
                        editTextMessage.setText("");
                        adapter.addMessage(new ChatMessage(message, true));
                        recyclerViewChat.smoothScrollToPosition(adapter.getItemCount() - 1);
                        loadMessages();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                buttonSend.setEnabled(true);
            }
        });
    }

    private void loadMessages() {
        if (id_booking == 0) return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.getMessages(id_booking);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseBody = response.body();
                    List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

                    List<ChatMessage> messages = new ArrayList<>();

                    if (data != null) {
                        for (Map<String, Object> messageObj : data) {
                            String pengirim = (String) messageObj.get("pengirim");
                            String pesan = (String) messageObj.get("pesan");

                            boolean isUserMessage = "user".equals(pengirim);
                            messages.add(new ChatMessage(pesan, isUserMessage));
                        }
                    }

                    adapter.updateData(messages);
                    if (!messages.isEmpty()) {
                        recyclerViewChat.scrollToPosition(messages.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {}
        });
    }

    private void startAutoRefresh() {
        if (!isRunning) return;

        executor.execute(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(3000);
                    handler.post(() -> {
                        loadMessages();
                        if (tanggalSesi != null && jamSelesai != null) checkSessionExpiry();
                    });
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