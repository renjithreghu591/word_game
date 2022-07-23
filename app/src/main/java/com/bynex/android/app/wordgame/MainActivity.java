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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button play, newWord, listWords;
    ImageButton playingConfiguration;
    String currentPlayMode;

    SharedPreferences appLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         appLog = getApplicationContext().getSharedPreferences(Values.APP_LOG, Context.MODE_PRIVATE);

        currentPlayMode = appLog.getString(Values.CURRENT_PLAY_MODE, null);

        newWord = findViewById(R.id.main_btn_addword);
        listWords = findViewById(R.id.main_btn_category);
        play = findViewById(R.id.main_btn_playgame);
        playingConfiguration = findViewById(R.id.main_imgbtn_playsettings);

        playingConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.playing_configuration_layout, null);
                com.google.android.material.textfield.MaterialAutoCompleteTextView autoCompleteTextView = v.findViewById(R.id.playingconfiguration_material_autotxt);
                com.google.android.material.textfield.TextInputLayout textInputLayout = v.findViewById(R.id.playingconfiguration_material_inputlayout);

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Values.CATEGORIES, Context.MODE_PRIVATE);

                Map<String, ?> map = sharedPreferences.getAll();
                Set<String> set = map.keySet();
                Iterator iterator = set.iterator();
                List<String> list = new ArrayList<>();

                while (iterator.hasNext()) {
                    String str = iterator.next().toString();
                    if (!str.equals(Values.WRONG_WORDS)) {
                        SharedPreferences sharedPreferences1 = getApplicationContext().getSharedPreferences(str, Context.MODE_PRIVATE);
                        Map<String, ?> map1 = sharedPreferences1.getAll();
                        if (map1.size() > 0) {
                            list.add(str);
                        }
                    }

                }

                ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.autotextview_categories_layout, list);
                autoCompleteTextView.setAdapter(arrayAdapter);

                if (currentPlayMode != null) {
                    textInputLayout.setHint(currentPlayMode);
                }

                builder.setView(v).setTitle("Playing configuration").setNegativeButton("Cancel", null).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str = autoCompleteTextView.getText().toString();

                        if (!str.equals("")) {
                            currentPlayMode = str;
                            appLog.edit().putString(Values.CURRENT_PLAY_MODE, currentPlayMode).commit();
                        }
                    }
                }).show();
            }
        });

        newWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.addword_layout, null);
                com.google.android.material.textfield.MaterialAutoCompleteTextView selectedCategory = v.findViewById(R.id.addword_autotxt);
                com.google.android.material.textfield.TextInputEditText tvNewWord = v.findViewById(R.id.addword_etxt_word);
                com.google.android.material.textfield.TextInputEditText tvMeaning = v.findViewById(R.id.addword_etxt_meaning);


                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("categories", Context.MODE_PRIVATE);
                Map<String, ?> map = sharedPreferences.getAll();
                Set<String> set = map.keySet();

                ArrayList<String> list = new ArrayList<>();

                Iterator iterator = set.iterator();

                while (iterator.hasNext()) {
                    list.add(iterator.next().toString());
                }

                ArrayAdapter adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.autotextview_categories_layout, list);
                selectedCategory.setAdapter(adapter);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(v).setTitle("Add new word").setNegativeButton("Cancel", null).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newWord = tvNewWord.getText().toString();
                        String meaning = tvMeaning.getText().toString();
                        String categoryName = selectedCategory.getText().toString();

                        if (!newWord.equals("") && !meaning.equals("") && !categoryName.equals("")) {
                            boolean isCategoryName = false;

                            for (int i1 = 0; i1 < list.size(); i1++) {
                                if (list.get(i1).equals(categoryName)) {
                                    isCategoryName = true;
                                    break;
                                }
                            }

                            if (isCategoryName) {
                                SharedPreferences words = getApplicationContext().getSharedPreferences(categoryName, Context.MODE_PRIVATE);
                                if (words.getString(newWord, null) != null) {
                                    Toast.makeText(getApplicationContext(), "Word already existed", Toast.LENGTH_SHORT).show();
                                } else {
                                    words.edit().putString(newWord, meaning).commit();
                                }
                            } else {
                                sharedPreferences.edit().putString(categoryName, "true").commit();
                                SharedPreferences sharedPreferences1 = getApplicationContext().getSharedPreferences(categoryName, Context.MODE_PRIVATE);
                                sharedPreferences1.edit().putString(newWord, meaning).commit();
                            }




                        } else {
                            Toast.makeText(getApplicationContext(), "Please fill data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setCancelable(false);
                builder.show();
            }
        });

        listWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPlayMode != null) {
                    Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
                    intent.putExtra("currentPlayMode", currentPlayMode);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please select category", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}