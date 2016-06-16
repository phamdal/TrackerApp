package com.example.dalena.trackerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class loginActivity extends AppCompatActivity {

    public static final String TAG = loginActivity.class.getSimpleName();
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button logSubmit;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        usernameEdit = (EditText)findViewById(R.id.logUsername);
        passwordEdit = (EditText)findViewById(R.id.logPassword);
        logSubmit = (Button)findViewById(R.id.logSubmit);

        logSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                Log.v(TAG, "USERNAME: " + username + " PASSWORD: " + password);

                if(username == null || username.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter a username.", Toast.LENGTH_LONG).show();
                } else if (password == null || password.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your password", Toast.LENGTH_LONG).show();
                } else {
                    String getRequest = "http://52.33.77.245:3001/user/" + username + "?password=" + password;

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(getRequest).build();

                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseData = response.body().string();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(responseData.equals("PE: Password Error")) {
                                        Toast.makeText(getApplicationContext(),
                                                "Incorrect Password. Please try again.", Toast.LENGTH_LONG).show();
                                    } else if(responseData.equals("Error: No User")) {
                                        Toast.makeText(getApplicationContext(),
                                                "No such user. Please try again.", Toast.LENGTH_LONG).show();
                                    } else {

                                        startDeliveryActivity(username);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void startDeliveryActivity(String username) {
        Intent intent = new Intent(this, deliveryListActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}
