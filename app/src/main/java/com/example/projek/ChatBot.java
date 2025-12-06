package com.example.projek;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

public class ChatBot extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private ImageButton buttonBack;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_API_KEY = BuildConfig.GROQ_API_KEY;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_bot_activity);

        // AGAR KEYBOARD MENYESUAIKAN LAYOUT
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        initViews();
        setupRecyclerView();
        setupClickListeners();
        addWelcomeMessage();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonBack = findViewById(R.id.buttonBack);
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setAdapter(chatAdapter);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));

        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                recyclerViewChat.smoothScrollToPosition(chatAdapter.getItemCount());
            }
        });
    }

    private void setupClickListeners() {
        buttonSend.setOnClickListener(v -> sendMessage());

        buttonBack.setOnClickListener(v -> finish());

        editTextMessage.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                    keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void addWelcomeMessage() {
        String welcomeMessage =  "Halo! Saya Si Lakon, asisten AI dari Lakoni Aja. " +
                "Saya di sini untuk membantu Anda, terutama dalam hal konseling dan kesehatan mental. " +
                "Silakan ceritakan apa yang sedang Anda rasakan atau ingin Anda tanyakan.";
        addMessageToChat(welcomeMessage, false);
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Masukkan pesan terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        addMessageToChat(message, true);
        editTextMessage.setText("");

        // NONAKTIFKAN TOMBOL SEMENTARA
        buttonSend.setEnabled(false);

        sendToGroqAPI(message);
    }

    private void addMessageToChat(String message, boolean isUser) {
        runOnUiThread(() -> {
            chatMessages.add(new ChatMessage(message, isUser));
            chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        });
    }

    private void sendToGroqAPI(String userMessage) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.1-8b-instant");

            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content",
                    "Anda adalah asisten AI khusus konseling dan kesehatan mental dari Politeknik Negeri Jember (Polije). " +
                            "Tugas Anda adalah memberikan dukungan emosional, informasi tentang kesehatan mental, " +
                            "saran pengelolaan stres, kecemasan, motivasi diri, dan topik psikologi dasar. " +
                            "Selalu gunakan bahasa yang lembut, empati, tidak menghakimi, dan menenangkan. " +
                            "Jika pengguna bertanya tentang hal yang tidak berkaitan dengan konseling atau kesehatan mental, " +
                            "seperti teknologi, pemrograman, game, politik, matematika, bisnis, atau topik umum lainnya, " +
                            "anda harus menolak dengan sopan dan berkata: 'Maaf, Saya hanya dapat membantu dalam topik konseling dan kesehatan mental.' " +
                            "Tetaplah profesional dan batasi diri hanya pada topik kesehatan mental.");

            messages.put(systemMessage);

            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", userMessage);
            messages.put(messageObj);

            jsonBody.put("messages", messages);
            jsonBody.put("temperature", 0.7);
            jsonBody.put("max_tokens", 1024);
            jsonBody.put("top_p", 1);
            jsonBody.put("stream", false);

            String requestBodyString = jsonBody.toString();

            RequestBody body = RequestBody.create(requestBodyString, JSON);
            Request request = new Request.Builder()
                    .url(GROQ_API_URL)
                    .header("Authorization", "Bearer " + GROQ_API_KEY)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        // AKTIFKAN KEMBALI TOMBOL
                        buttonSend.setEnabled(true);

                        Toast.makeText(ChatBot.this, "Error koneksi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        addMessageToChat("Maaf, terjadi kesalahan koneksi. Pastikan internet Anda tersambung dan coba lagi.", false);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();

                    runOnUiThread(() -> {
                        // AKTIFKAN KEMBALI TOMBOL
                        buttonSend.setEnabled(true);
                    });

                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONArray choices = jsonResponse.getJSONArray("choices");

                            if (choices.length() > 0) {
                                JSONObject choice = choices.getJSONObject(0);
                                JSONObject message = choice.getJSONObject("message");
                                String botResponse = message.getString("content").trim();

                                addMessageToChat(botResponse, false);
                            } else {
                                addMessageToChat("Maaf, tidak ada respons dari AI. Coba lagi.", false);
                            }
                        } catch (JSONException e) {
                            runOnUiThread(() ->
                                    Toast.makeText(ChatBot.this, "Error parsing: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                            addMessageToChat("Maaf, terjadi kesalahan parsing respons.", false);
                        }
                    } else {
                        String errorMsg = "Error API: " + response.code();
                        if (response.code() == 401) {
                            errorMsg = "API Key tidak valid. Periksa kembali GROQ_API_KEY.";
                        } else if (response.code() == 429) {
                            errorMsg = "Rate limit terlampaui. Coba lagi nanti.";
                        } else if (response.code() == 400) {
                            errorMsg = "Bad request. Periksa parameter request.";
                        }

                        final String finalErrorMsg = errorMsg;
                        runOnUiThread(() -> {
                            Toast.makeText(ChatBot.this, finalErrorMsg, Toast.LENGTH_LONG).show();
                        });
                        addMessageToChat("Maaf, terjadi kesalahan server (" + response.code() + ").", false);
                    }
                }
            });

        } catch (JSONException e) {
            runOnUiThread(() -> {
                buttonSend.setEnabled(true);
                Toast.makeText(this, "Error creating request: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }
}