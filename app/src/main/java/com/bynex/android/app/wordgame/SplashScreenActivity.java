package com.bynex.android.app.wordgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

import com.bynex.android.app.wordgame.Values;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlSerializer;

public class SplashScreenActivity extends AppCompatActivity {

    ProgressBar progressBar;
    private int i = 0, x = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setTooltipText("hello");
        startProgress();

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                progressBar.setProgress(i);
                if (i >= 100) {
                    timer.purge();
                    timer.cancel();
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 100, 100);
    }

    public void startProgress() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                i++;
                SharedPreferences appLog = getApplicationContext().getSharedPreferences(Values.APP_LOG, Context.MODE_PRIVATE);
                i++;
                i++;
                if (!appLog.getBoolean(Values.FIRST_TIME_USE, true)) {
                    i++;
                } else {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Values.CATEGORIES, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString(Values.WRONG_WORDS, "true").commit();
                    appLog.edit().putBoolean(Values.FIRST_TIME_USE, false).commit();
                    i++;
                }

                while (i < 100) {
                    if (i == 20 || i == 80) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    i++;
                }

                try {
                    getApplicationContext().openFileInput("newxml");
                    Log.i("mylog", "file founded");
                } catch (FileNotFoundException e) {

                    XmlSerializer serializer = Xml.newSerializer();
                    StringWriter writer = new StringWriter();

                    try {
                        serializer.setOutput(writer);
                        serializer.startDocument("UTF-8", true);
                        serializer.startTag("", "categories");

                        serializer.startTag("", "default_categories");
                        serializer.endTag("", "default_categories");

                        serializer.endTag("", "categories");
                        serializer.endDocument();
                        serializer.flush();

                        String result = writer.toString();
                        FileOutputStream fileOutputStream = getApplicationContext().openFileOutput("newxml", Context.MODE_PRIVATE);
                        fileOutputStream.write(result.getBytes());
                        fileOutputStream.close();

                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }


                    Log.i("mylog", "file created");
                }
            }
        });

        thread.start();

    }
}