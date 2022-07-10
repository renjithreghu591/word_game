package com.bynex.android.app.wordgame;

import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button play, newWord, listWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        newWord = findViewById(R.id.main_btn_addword);
        listWords = findViewById(R.id.main_btn_category);
        play = findViewById(R.id.main_btn_playgame);

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
                            SharedPreferences words = getApplicationContext().getSharedPreferences(categoryName, Context.MODE_PRIVATE);

                            if (words.getString(newWord, null) != null) {
                                Toast.makeText(getApplicationContext(), "Word already existed", Toast.LENGTH_SHORT).show();
                            } else {
                                words.edit().putString(newWord, meaning).commit();
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
                Intent intent = new Intent(MainActivity.this, PlayingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}