package com.example.projek;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chat_Konseli extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private ImageButton buttonSend, buttonBack;
    private ProgressBar progressBar;
    private ChatKonseliAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();

    private int idUser;
    private int idKonselor = 1;
    private int idSesi = 6;

    private OkHttpClient client;
    private static final String BASE_URL = "http://10.241.207.46/webLakoniAja/api/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_konseli);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_konseli), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        getIdUserFromPreferences();

        if (idUser == 0) {
            Toast.makeText(this, "Error: User ID tidak ditemukan", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupClickListeners();
        loadChatHistory();
    }

    private void getIdUserFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        idUser = prefs.getInt("user_id", 1);
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChatKonseli);
        editTextMessage = findViewById(R.id.editTextMessageKonseli);
        buttonSend = findViewById(R.id.buttonSendKonseli);
        buttonBack = findViewById(R.id.buttonBackKonseli);
        progressBar = findViewById(R.id.progressBarKonseli);
    }

    private void setupRecyclerView() {
        adapter = new ChatKonseliAdapter(messages);
        recyclerViewChat.setAdapter(adapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(v -> finish());
        buttonSend.setOnClickListener(v -> sendMessageToServer());
    }

    private void sendMessageToServer() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            ChatMessage userMessage = new ChatMessage(messageText, true);
            adapter.addMessage(userMessage);
            editTextMessage.setText("");
            recyclerViewChat.scrollToPosition(messages.size() - 1);
            sendMessageToAPI(messageText);
        }
    }

    private void sendMessageToAPI(String message) {
        try {
            JSONObject json = new JSONObject();
            json.put("id_user", idUser);
            json.put("id_konselor", idKonselor);
            json.put("id_sesi", idSesi);
            json.put("pesan", message);

            RequestBody body = RequestBody.create(json.toString(), JSON);
            Request request = new Request.Builder()
                    .url(BASE_URL + "send_message.php")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(Chat_Konseli.this, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if (!jsonResponse.getString("status").equals("success")) {
                                Toast.makeText(Chat_Konseli.this, "Gagal: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                        }
                    });
                }
            });

        } catch (JSONException e) {
        }
    }

    private void loadChatHistory() {
        progressBar.setVisibility(View.VISIBLE);

        String url = BASE_URL + "get_messages.php?id_sesi=" + idSesi +
                "&id_user=" + idUser + "&id_konselor=" + idKonselor;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Chat_Konseli.this, "Gagal memuat pesan", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (jsonResponse.getString("status").equals("success")) {
                            parseMessages(jsonResponse.getJSONArray("data"));
                        } else {
                            Toast.makeText(Chat_Konseli.this, "Gagal memuat: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                    }
                });
            }
        });
    }

    private void parseMessages(JSONArray messagesArray) {
        messages.clear();
        try {
            for (int i = 0; i < messagesArray.length(); i++) {
                JSONObject messageObj = messagesArray.getJSONObject(i);
                String pesan = messageObj.getString("pesan");
                String pengirim = messageObj.getString("pengirim");
                boolean isKonseli = pengirim.equals("user");
                ChatMessage chatMessage = new ChatMessage(pesan, isKonseli);
                messages.add(chatMessage);
            }
            adapter.notifyDataSetChanged();
            recyclerViewChat.scrollToPosition(messages.size() - 1);
        } catch (JSONException e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadChatHistory();
                startAutoRefresh();
            }
        }, 5000);
    }
}