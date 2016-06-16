package com.example.dalena.trackerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class updateActivity extends AppCompatActivity {

    public static final String TAG = updateActivity.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_layout);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");

        try {
            run(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String userid) throws Exception {
        final String username = userid;
        JsonObject json = new JsonObject();
        String jsonString = json.toString();

        String urlString = "http://52.33.77.245:3001/track/updateall/" + username;
        RequestBody body = RequestBody.create(JSON, jsonString);

        Request request = new Request.Builder()
                .header("content-type", "application/json")
                .url(urlString)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException(
                        "Unexpected code " + response);
                final String responseDb = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startDeliveryActivity(username);
                    }
                });
            }
        });
    }

    private void startDeliveryActivity(String username) {
        Intent intent = new Intent(this, deliveryListActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
