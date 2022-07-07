package com.bynex.android.app.wordgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button play, newWord, listWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newWord = findViewById(R.id.new_word);
        listWords = findViewById(R.id.list);
        play = findViewById(R.id.play);

        newWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.new_word_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(v).setTitle("Add new word").setNegativeButton("Cancel", null).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextView tvNewWord = v.findViewById(R.id.etxt_new_word);
                        TextView tvMeaning = v.findViewById(R.id.etxt_new_word_meaning);

                        String newWord = tvNewWord.getText().toString();
                        String meaning = tvMeaning.getText().toString();

                        if (!newWord.equals("") && !meaning.equals("")) {
                            SharedPreferences words = getApplicationContext().getSharedPreferences("words", Context.MODE_PRIVATE);

                            if (words.getString(newWord, null) != null) {
                                Toast.makeText(getApplicationContext(), "Word already existed", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            SharedPreferences.Editor editor = words.edit();

                            editor.putString(newWord, meaning);
                            editor.commit();
                        } else {
                            Log.i("mylog", "is empty");
                        }
                    }
                }).setCancelable(false);
                builder.show();
            }
        });

        listWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListWordsActivity.class);
                startActivity(intent);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}