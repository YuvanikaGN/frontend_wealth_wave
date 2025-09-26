package com.simats.wealth_wave;

import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.View;
import android.widget.LinearLayout;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.wealth_wave.models.ChatMessage;
import com.simats.wealth_wave.models.ChatbotRequest;
import com.simats.wealth_wave.models.GeminiRequest;
import com.simats.wealth_wave.responses.ChatbotResponse;
import com.simats.wealth_wave.responses.GeminiResponse;
import com.simats.wealth_wave.retrofit.ApiClient;
import com.simats.wealth_wave.retrofit.ApiService;
import com.simats.wealth_wave.retrofit.GeminiApiClient;
import com.simats.wealth_wave.retrofit.GeminiApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private ImageButton buttonSend;

    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private LinearLayout welcomeLayout;


    private static final String API_KEY = "AIzaSyDE_I-wdeByHlDmksCFz5tGp5owZzIkPxw"; // Your Gemini API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot);

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChat.setAdapter(chatAdapter);

        welcomeLayout = findViewById(R.id.welcomeLayout);


        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // 👇 Hide the welcome layout
                if (welcomeLayout.getVisibility() == View.VISIBLE) {
                    welcomeLayout.setVisibility(View.GONE);
                }

                addMessage(new ChatMessage(userMessage, true)); // User message
                editTextMessage.setText("");
                sendMessageToServer(userMessage);
            }
        });

        AppCompatButton btnFinancialGuide = findViewById(R.id.financialGuide);
        AppCompatButton btnSavingGuide = findViewById(R.id.savingGuide);
        AppCompatButton btnSavingTips = findViewById(R.id.btnSavingTips);


        setupQuickSendButton(btnFinancialGuide);
        setupQuickSendButton(btnSavingGuide);
        setupQuickSendButton(btnSavingTips);


        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.top_bar));
    }

    private void setupQuickSendButton(AppCompatButton button) {
        button.setOnClickListener(v -> {
            String message = button.getText().toString();

            if (welcomeLayout.getVisibility() == View.VISIBLE) {
                welcomeLayout.setVisibility(View.GONE);
            }

            addMessage(new ChatMessage(message, true));
            sendMessageToServer(message);
        });
    }

    private void addMessage(ChatMessage message) {
        chatMessages.add(message);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
    }

    private void sendMessageToServer(String userMessage) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        ChatbotRequest request = new ChatbotRequest(userMessage);

        // typing indicator (same as before)
        ChatMessage typingMessage = new ChatMessage(true);
        chatMessages.add(typingMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);

        apiService.sendMessage(request).enqueue(new Callback<ChatbotResponse>() {
            @Override
            public void onResponse(Call<ChatbotResponse> call, Response<ChatbotResponse> response) {
                int index = chatMessages.indexOf(typingMessage);
                String reply = "Sorry, no reply.";

                if (response.isSuccessful() && response.body() != null) {
                    ChatbotResponse body = response.body();
                    if (body.isSuccess() && body.getReply() != null) {
                        reply = body.getReply();
                    } else if (body.getError() != null) {
                        reply = "Error: " + body.getError();
                    } else if (body.getResponse() != null) {
                        reply = body.getResponse();
                    }
                } else {
                    // server responded with non-2xx or parsing failed
                    if (response.errorBody() != null) {
                        try { reply = "Server error."; } catch (Exception ignored) {}
                    }
                }

                // Shorten reply as a safeguard (keeps 1-2 sentences)
                reply = shortenReply(reply);

                if (index != -1) {
                    chatMessages.set(index, new ChatMessage(reply, false));
                    chatAdapter.notifyItemChanged(index);
                    recyclerViewChat.scrollToPosition(index);
                } else {
                    // fallback: append
                    chatMessages.add(new ChatMessage(reply, false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<ChatbotResponse> call, Throwable t) {
                int index = chatMessages.indexOf(typingMessage);
                String failMsg = "Network error: " + t.getMessage();
                failMsg = shortenReply(failMsg);
                if (index != -1) {
                    chatMessages.set(index, new ChatMessage(failMsg, false));
                    chatAdapter.notifyItemChanged(index);
                } else {
                    chatMessages.add(new ChatMessage(failMsg, false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                }
            }
        });
    }

    private String shortenReply(String text) {
        if (text == null) return "";
        // remove markdown-like characters (already done server-side, but safe)
        text = text.replaceAll("[*_#`>-]+", "").trim();

        // get first two sentences (split on .!? + whitespace)
        String[] sentences = text.split("(?<=[.!?])\\s+");
        if (sentences.length == 0) return text.length() > 200 ? text.substring(0, 200) + "..." : text;
        String result = sentences[0];
        if (sentences.length > 1) result = result + " " + sentences[1];
        // final crop: max 220 chars
        if (result.length() > 220) result = result.substring(0, 217) + "...";
        return result.trim();
    }


}
