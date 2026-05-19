package com.example.quizapp_elalami;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    private static final int MIC_PERMISSION_CODE = 200;

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<Message> messages;
    private EditText etMessage;
    private ImageButton btnSend, btnMic;
    private ProgressBar pbLoading;
    private OllamaApi ollamaApi;

    // Speech recognition variables
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnMic = findViewById(R.id.btnMic);
        pbLoading = findViewById(R.id.pbLoading);

        messages = new ArrayList<>();
        adapter = new ChatAdapter(messages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Setup Retrofit for Ollama API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:11434/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ollamaApi = retrofit.create(OllamaApi.class);

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
            }
        });

        // --- Voice Recognition Setup ---
        setupSpeechRecognizer();

        btnMic.setOnClickListener(v -> {
            checkPermissionAndStartListening();
        });

        // Initial AI message
        addMessage("Bonjour ! Je suis votre assistant Quiz City. Comment puis-je vous aider avec le tourisme au Maroc aujourd'hui ?", Message.TYPE_AI);
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                Toast.makeText(ChatActivity.this, "Écoute en cours...", Toast.LENGTH_SHORT).show();
                btnMic.setColorFilter(ContextCompat.getColor(ChatActivity.this, R.color.primary_green));
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                btnMic.setColorFilter(null);
            }

            @Override
            public void onError(int error) {
                String message;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO: message = "Erreur audio"; break;
                    case SpeechRecognizer.ERROR_CLIENT: message = "Erreur client"; break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: message = "Permissions insuffisantes"; break;
                    case SpeechRecognizer.ERROR_NETWORK: message = "Erreur réseau"; break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: message = "Délai réseau dépassé"; break;
                    case SpeechRecognizer.ERROR_NO_MATCH: message = "Aucune correspondance trouvée"; break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: message = "Service occupé"; break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: message = "Aucune parole détectée"; break;
                    default: message = "Erreur inconnue"; break;
                }
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
                btnMic.setColorFilter(null);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null && !data.isEmpty()) {
                    etMessage.setText(data.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void checkPermissionAndStartListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_CODE);
        } else {
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MIC_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée", Toast.LENGTH_SHORT).show();
                speechRecognizer.startListening(speechRecognizerIntent);
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendMessage(String text) {
        addMessage(text, Message.TYPE_USER);
        etMessage.setText("");
        pbLoading.setVisibility(View.VISIBLE);

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
            }
        });
    }

    private void addMessage(String text, int type) {
        messages.add(new Message(text, type));
        adapter.notifyItemInserted(messages.size() - 1);
        rvChat.scrollToPosition(messages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}