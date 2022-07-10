package com.bynex.android.app.wordgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PlayingActivity extends AppCompatActivity {

    TextView word, meaning;
    Button play;
    ImageButton speek;
    CheckBox correctAns;
    private TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        SharedPreferences words = getApplicationContext().getSharedPreferences("words", Context.MODE_PRIVATE);

        Map<String, ?> map = words.getAll();
        Set<String> set = map.keySet();
        Iterator iterator = set.iterator();
        ArrayList<String> wordList = new ArrayList<>();

        while(iterator.hasNext()) {
            String str = iterator.next().toString();
            wordList.add(str);
        }

        play = findViewById(R.id.btn_play);
        word = findViewById(R.id.txt_word_dis);
        meaning = findViewById(R.id.txt_meaning_dis);
        speek = findViewById(R.id.speek);
        correctAns = findViewById(R.id.main_chekbox_currect);

        if (wordList.size() <= 0) {
            Toast.makeText(getApplicationContext(), "Please add some words", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PlayingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        word.setText(wordList.get(0));
        meaning.setText(words.getString(wordList.get(0), "null"));

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (play.getText().toString().equals("Show") && wordList.size() > 0) {
                    meaning.setVisibility(View.VISIBLE);
                    correctAns.setVisibility(View.VISIBLE);
                    correctAns.setChecked(true);
                    play.setText("Next");
                    wordList.remove(0);
                    Collections.shuffle(wordList);
                    if (wordList.size() == 0) {
                        play.setText("End Game");
                    }
                } else if (play.getText().toString().equals("Next") && wordList.size() > 0) {
                    meaning.setVisibility(View.INVISIBLE);
                    correctAns.setVisibility(View.INVISIBLE);
                    word.setText(wordList.get(0));
                    meaning.setText(words.getString(wordList.get(0), "null"));
                    play.setText("Show");
                    if (wordList.size() == 0) {
                        play.setText("End Game");
                    }
                } else if (play.getText().toString().equals("End Game")) {
                    Intent intent = new Intent(PlayingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        speek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if (i == TextToSpeech.SUCCESS) {
                            Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();
                            tts.setLanguage(Locale.getDefault());
                            tts.setSpeechRate(1.0f);
                            tts.speak(word.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlayingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}