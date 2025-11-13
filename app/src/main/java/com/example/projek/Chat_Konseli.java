package com.example.projek;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chat_Konseli extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editText;
    private ImageButton btnSend, btnBack;
    private ProgressBar progressBar;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatList;

    private static final String BASE_URL = "http://192.168.18.9/android_api/"; // ganti IP laptop kamu

    private OkHttpClient client;
    private int konseliId = 1;    // contoh: ID user login (bisa diambil dari SharedPreferences)
    private int konselorId = 100; // contoh: ID konselor

    private Handler handler = new Handler();
    private Runnable messageUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_konseli);

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        recyclerView = findViewById(R.id.recyclerViewChat);
        editText = findViewById(R.id.editTextMessage);
        btnSend = findViewById(R.id.buttonSend);
        btnBack = findViewById(R.id.buttonBack);
        progressBar = findViewById(R.id.progressBar);

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Tombol kirim
        btnSend.setOnClickListener(v -> sendMessage());
        // Tombol kembali
        btnBack.setOnClickListener(v -> finish());

        // Load pesan awal
        getMessages();

        // Auto-refresh pesan setiap 2 detik
        messageUpdater = new Runnable() {
            @Override
            public void run() {
                getMessages();
                handler.postDelayed(this, 2000); // refresh tiap 2 detik
            }
        };
        handler.post(messageUpdater);
    }

    private void sendMessage() {
        String message = editText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) return;

        // Tampilkan di layar langsung
        addMessage(message, true);
        editText.setText("");

        RequestBody body = new FormBody.Builder()
                .add("sender_id", String.valueOf(konseliId))
                .add("receiver_id", String.valueOf(konselorId))
                .add("message", message)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "send_message.php")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(Chat_Konseli.this, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(Chat_Konseli.this, "Gagal mengirim ke server", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void getMessages() {
        Request request = new Request.Builder()
                .url(BASE_URL + "get_messages.php?sender_id=" + konseliId + "&receiver_id=" + konselorId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                // bisa log error di sini
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;
                try {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    if (json.getBoolean("success")) {
                        JSONArray messages = json.getJSONArray("messages");

                        // Jangan clear, tapi hanya update kalau ada pesan baru
                        List<ChatMessage> tempList = new ArrayList<>();
                        for (int i = 0; i < messages.length(); i++) {
                            JSONObject msg = messages.getJSONObject(i);
                            boolean isUser = msg.getString("sender_id").equals(String.valueOf(konseliId));
                            tempList.add(new ChatMessage(msg.getString("message"), isUser));
                        }

                        runOnUiThread(() -> {
                            chatList.clear();
                            chatList.addAll(tempList);
                            chatAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(chatList.size() - 1);
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void addMessage(String text, boolean isUser) {
        runOnUiThread(() -> {
            chatList.add(new ChatMessage(text, isUser));
            chatAdapter.notifyItemInserted(chatList.size() - 1);
            recyclerView.scrollToPosition(chatList.size() - 1);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(messageUpdater); // stop auto-refresh saat keluar
    }
}