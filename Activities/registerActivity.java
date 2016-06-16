package com.example.dalena.trackerapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import com.google.gson.JsonObject;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class registerActivity extends AppCompatActivity {

    public static final String TAG = registerActivity.class.getSimpleName();
    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText password2Edit;
    private Button regSubmit;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        usernameEdit = (EditText)findViewById(R.id.regUsername);
        passwordEdit = (EditText)findViewById(R.id.regpassword);
        password2Edit = (EditText)findViewById(R.id.regpassword2);
        regSubmit = (Button)findViewById(R.id.regSubmit);

        regSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password =  passwordEdit.getText().toString();
                String password2 = password2Edit.getText().toString();
                String username = usernameEdit.getText().toString();

                if(!password.equals(password2)) {
                    Toast.makeText(getApplicationContext(),
                            "Passwords don't match! Please try again.", Toast.LENGTH_LONG).show();
                } else if (username == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter a username.", Toast.LENGTH_LONG).show();
                    // Check for password strength
                } else if (password == null || password.length() < 6) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter a password of 6 characters or longer.", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        run(username, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void run(String userid, String password) throws Exception {
        final String username = userid;
        JsonObject json = new JsonObject();
        json.addProperty("password", password);
        String jsonString = json.toString();

        String urlString = "http://52.33.77.245:3001/user/" + username;
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
                        if (responseDb.startsWith("Error")) {
                            Toast.makeText(getApplicationContext(),
                                    "Username already exists. Please pick another one (: ", Toast.LENGTH_LONG).show();
                        } else {
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
        intent.putExtra("newUser", "yes");
        startActivity(intent);
        finish();
    }
}
