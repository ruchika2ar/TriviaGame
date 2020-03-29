package com.ruchika.triviagame;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    Button play, btnTry;
    ProgressBar mainPbar;
    TextView tvNoInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = findViewById(R.id.play);
        mainPbar = findViewById(R.id.mainPbar);
        btnTry = findViewById(R.id.btnTry);
        tvNoInt = findViewById(R.id.tvNoInt);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryNetworkCall();

            }
        });
    }

    void tryNetworkCall() {
        play.setVisibility(View.GONE);
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo state = cm.getActiveNetworkInfo();
        if (state != null && state.isConnected()){
            mainPbar.setVisibility(View.VISIBLE);
            tvNoInt.setVisibility(View.GONE);
            btnTry.setVisibility(View.GONE);
            makeNetworkCall("https://opentdb.com/api.php?amount=50&difficulty=medium");
        } else {
            tvNoInt.setVisibility(View.VISIBLE);
            btnTry.setVisibility(View.VISIBLE);
            btnTry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tryNetworkCall();
                }
            });
        }
    }

    void makeNetworkCall(String url) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String result = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(MainActivity.this, GameActivity.class);
                        i.putExtra("result", result);
                        startActivity(i);
                    }
                });
            }
        });
    }

}