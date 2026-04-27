package com.example.quizapp_elalami;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class QuizActivity extends AppCompatActivity {

    TextView question;
    Button b1, b2, b3;
    CardView cvQuestion;
    View llAnswers;

    int index = 0;
    int score = 0;
    String city;

    String[] questions;
    String[][] answers;
    int[] correct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        question = findViewById(R.id.question);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        cvQuestion = findViewById(R.id.cvQuestion);
        llAnswers = findViewById(R.id.llAnswers);

        city = getIntent().getStringExtra("city");
        if (city == null) city = "default";

        loadQuestionsData();

        if (questions == null || answers == null || correct == null) {
            Toast.makeText(this, "Error loading questions", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        displayQuestion();

        b1.setOnClickListener(v -> handleAnswerSelection(0, v));
        b2.setOnClickListener(v -> handleAnswerSelection(1, v));
        b3.setOnClickListener(v -> handleAnswerSelection(2, v));
    }

    private void loadQuestionsData() {
        city = city.toLowerCase().trim();

        if (city.equals("casablanca")) {
            questions = new String[]{
                    "Casablanca est la plus grande ville du Maroc ?",
                    "Quel club est à Casablanca ?",
                    "La mer à Casa ?",
                    "Stade principal ?",
                    "Quartier connu ?"
            };
            answers = new String[][]{
                    {"Oui", "Non", "Peut-être"},
                    {"Wydad", "FUS", "IRT"},
                    {"Atlantique", "Méditerranée", "Aucune"},
                    {"Mohammed V", "Adrar", "Marrakech"},
                    {"Maarif", "Agdal", "Guéliz"}
            };
            correct = new int[]{0, 0, 0, 0, 0};
        } else if (city.equals("rabat")) {
            questions = new String[]{
                    "Rabat est la capitale ?",
                    "Monument célèbre ?",
                    "Fleuve ?",
                    "Equipe ?",
                    "Ville proche ?"
            };
            answers = new String[][]{
                    {"Oui", "Non", "Peut-être"},
                    {"Tour Hassan", "Koutoubia", "Menara"},
                    {"Bouregreg", "Sebou", "Oum Rabia"},
                    {"FUS", "WAC", "IRT"},
                    {"Salé", "Agadir", "Oujda"}
            };
            correct = new int[]{0, 0, 0, 0, 0};
        } else if (city.equals("tanger")) {
            questions = new String[]{
                    "Tanger est au nord ?",
                    "Mer principale ?",
                    "Port connu ?",
                    "Ville proche ?",
                    "Climat ?"
            };
            answers = new String[][]{
                    {"Oui", "Non", "Peut-être"},
                    {"Méditerranée", "Atlantique", "Aucune"},
                    {"Tanger Med", "Casa Port", "Agadir"},
                    {"Tétouan", "Fès", "Oujda"},
                    {"Humide", "Sec", "Froid"}
            };
            correct = new int[]{0, 0, 0, 0, 0};
        } else if (city.equals("marrakech")) {
            questions = new String[]{
                    "Marrakech est touristique ?",
                    "Place célèbre ?",
                    "Climat ?",
                    "Couleur de la ville ?",
                    "Souk connu ?"
            };
            answers = new String[][]{
                    {"Oui", "Non", "Peut-être"},
                    {"Jamaa el-Fna", "Agdal", "Maarif"},
                    {"Chaud", "Froid", "Humide"},
                    {"Rouge", "Bleu", "Vert"},
                    {"Souk Semmarine", "Mall", "Marché"}
            };
            correct = new int[]{0, 0, 0, 0, 0};
        } else {
            questions = new String[]{
                    "Le Maroc est en Afrique ?",
                    "Monnaie ?",
                    "Langue ?",
                    "Ville touristique ?",
                    "Plat marocain ?"
            };
            answers = new String[][]{
                    {"Oui", "Non", "Peut-être"},
                    {"Dirham", "Euro", "Dollar"},
                    {"Arabe", "Chinois", "Russe"},
                    {"Marrakech", "Paris", "Rome"},
                    {"Couscous", "Pizza", "Burger"}
            };
            correct = new int[]{0, 0, 0, 0, 0};
        }
    }

    private void displayQuestion() {
        question.setText(questions[index]);
        b1.setText(answers[index][0]);
        b2.setText(answers[index][1]);
        b3.setText(answers[index][2]);

        // Fade in animation for new content
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        cvQuestion.startAnimation(fadeIn);
        llAnswers.startAnimation(fadeIn);
    }

    private void handleAnswerSelection(int selectedAnswer, View buttonView) {
        // 1. Animate button (scale)
        buttonView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_click));

        if (selectedAnswer == correct[index]) {
            score++;
        }

        // 2. Fade out current content
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                index++;
                if (index >= questions.length) {
                    Intent i = new Intent(QuizActivity.this, Score.class);
                    i.putExtra("score", score);
                    startActivity(i);
                    finish();
                } else {
                    // 3. Load next content and fade it in
                    displayQuestion();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        cvQuestion.startAnimation(fadeOut);
        llAnswers.startAnimation(fadeOut);
    }
}