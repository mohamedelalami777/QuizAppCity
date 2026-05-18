package com.example.quizapp_elalami;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<Message> messages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar pbLoading;
    private OllamaApi ollamaApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        pbLoading = findViewById(R.id.pbLoading);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Setup Retrofit for Ollama API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:11434/") // 10.0.2.2 points to localhost from Android Emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ollamaApi = retrofit.create(OllamaApi.class);

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
            }
        });

        // Initial AI message
        addMessage("Bonjour ! Je suis votre assistant Quiz City. Comment puis-je vous aider avec le tourisme au Maroc aujourd'hui ?", Message.TYPE_AI);
    }

    private void sendMessage(String text) {
        addMessage(text, Message.TYPE_USER);
        etMessage.setText("");
        pbLoading.setVisibility(View.VISIBLE);

        // Construct a prompt to specialize the assistant in Moroccan tourism
        String systemPrompt = "Vous êtes un assistant spécialisé dans les villes du Maroc et le tourisme. Répondez de manière concise et professionnelle.\nQuestion: ";
        OllamaApi.OllamaRequest request = new OllamaApi.OllamaRequest("gemma3", systemPrompt + text);

        ollamaApi.generateResponse(request).enqueue(new Callback<OllamaApi.OllamaResponse>() {
            @Override
            public void onResponse(Call<OllamaApi.OllamaResponse> call, Response<OllamaApi.OllamaResponse> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    addMessage(response.body().getResponse(), Message.TYPE_AI);
                } else {
                    addMessage("Désolé, je ne peux pas répondre pour le moment.", Message.TYPE_AI);
                }
            }

            @Override
            public void onFailure(Call<OllamaApi.OllamaResponse> call, Throwable t) {
                pbLoading.setVisibility(View.GONE);
                addMessage("Erreur de connexion à l'assistant IA. Assurez-vous qu'Ollama est lancé localement.", Message.TYPE_AI);
                Toast.makeText(ChatActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMessage(String text, int type) {
        messages.add(new Message(text, type));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }
}