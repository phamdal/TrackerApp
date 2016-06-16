package com.example.dalena.trackerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class addActivity extends AppCompatActivity {
    /*Spinner spinner = (Spinner)findViewById(R.id.spinner);
    String text = spinner.getSelectedItem().toString();*/

    public static final String TAG = addActivity.class.getSimpleName();
    private Spinner spinner;
    private EditText descriptionEdit;
    private EditText nickNameEdit;
    private EditText trackingNumEdit;
    private Button addButton;
    String username;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_layout);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        spinner = (Spinner) findViewById(R.id.addSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.carrier_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        trackingNumEdit = (EditText) findViewById(R.id.addTrackNum);
        nickNameEdit = (EditText) findViewById(R.id.addNickname);
        descriptionEdit = (EditText) findViewById(R.id.addDescription);


        addButton = (Button) findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackingNum = trackingNumEdit.getText().toString();
                String nickname = nickNameEdit.getText().toString();
                String description = descriptionEdit.getText().toString();
                String carrier = spinner.getSelectedItem().toString();

                if (trackingNum == null || trackingNum.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter a tracking number.", Toast.LENGTH_LONG).show();
                } else if (carrier == null || carrier.isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Please select a carrier.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        run(username, trackingNum, carrier, nickname, description);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void run(String userid, String trackingNum, String carrier, String nickname, String description) throws Exception {
        final String username = userid;
        JsonObject json = new JsonObject();
        json.addProperty("username", userid);
        json.addProperty("trackingNum", trackingNum);
        json.addProperty("carrier", carrier);
        if(nickname != null || !nickname.isEmpty()) {
            json.addProperty("nickname", nickname);
        }
        if(description != null || !description.isEmpty()) {
            json.addProperty("description", description);
        }
        String jsonString = json.toString();
        Log.v(TAG, jsonString);

        String urlString = "http://52.33.77.245:3001/track";
        RequestBody body = RequestBody.create(JSON, jsonString);

        Request request = new Request.Builder()
                .header("content-type", "application/json")
                .url(urlString)
                .post(body)
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
                        Log.v(TAG, responseDb);
                        if (responseDb.equals("Error:Tracking Number does not exist")) {
                            Toast.makeText(getApplicationContext(),
                                    "Tracking Number does not exist with that carrier. Please check it again.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Packaged Added.", Toast.LENGTH_LONG).show();
                            startDeliveryActivity(username);
                        }
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

