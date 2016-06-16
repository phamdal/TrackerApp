package com.example.dalena.trackerapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class itemListActivity extends AppCompatActivity {

    public static final String TAG = itemListActivity.class.getSimpleName();

    private TextView trackingNumText;
    private TextView carrierText;
    private TextView statusText;

    private EditText nickNameEdit;
    private EditText descriptionEdit;

    private Button saveChangesButton;
    private Button sendButton;
    private ImageButton deleteButton;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    private String username;
    private String trackingNum;
    private String status;
    private String carrier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.click_item_layout);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        trackingNum = intent.getStringExtra("trackingNum");

        trackingNumText = (TextView) findViewById(R.id.clickTracking);
        carrierText = (TextView) findViewById(R.id.clickCarrier);
        statusText = (TextView) findViewById(R.id.clickStatus);
        nickNameEdit = (EditText) findViewById(R.id.clickNickName);
        descriptionEdit = (EditText) findViewById(R.id.clickDescription);

        saveChangesButton = (Button) findViewById(R.id.clickSave);
        sendButton = (Button) findViewById(R.id.clickShare);
        deleteButton = (ImageButton) findViewById(R.id.clickDelete);


        trackingNumText.setText(trackingNum);

        String getRequest = "http://52.33.77.245:3001/track/" + trackingNum + "?username=" + username;

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
                        try {
                            JSONObject jsonData = new JSONObject(responseData);
                            Log.v(TAG, responseData.toString());
                            packageTrack currentPackage = new packageTrack();
                            currentPackage.setStatus(jsonData.getString("status"));
                            currentPackage.setNickName(jsonData.getString("nickname"));
                            currentPackage.setCarrier(jsonData.getString("carrier"));
                            currentPackage.setDescription(jsonData.getString("description"));

                            carrier = currentPackage.getCarrier();
                            status = currentPackage.getStatus();

                            carrierText.setText(carrier);
                            statusText.setText(status);

                            if(currentPackage.getNickName().equals("-")) {
                                nickNameEdit.setHint("Add a nickname");
                            } else {
                                nickNameEdit.setHint(currentPackage.getNickName());
                            }

                            if(currentPackage.getDescription().equals(("-"))) {
                                descriptionEdit.setHint("Add a description");
                            } else {
                                descriptionEdit.setHint(currentPackage.getDescription());
                            }


                            saveChangesButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String newNickName = nickNameEdit.getText().toString();
                                    String newDescription = descriptionEdit.getText().toString();

                                    if((newNickName == null || newNickName.isEmpty()) && (newDescription == null || newDescription.isEmpty())) {
                                        Toast.makeText(getApplicationContext(),
                                               "Please include at least one field to update" , Toast.LENGTH_LONG).show();
                                    } else {
                                        try {
                                            runSave(newNickName, newDescription);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });

                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(itemListActivity.this);
                                    builder.setMessage("Are you sure you want to delete?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    try {
                                                        runDelete();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = builder.create();
                                    alert.show();
                                }
                            });

                            sendButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String sms_message = "Package " + trackingNum + ", carrier: " + carrier  + ", has status: " + status;
                                    Intent it = new Intent(Intent.ACTION_VIEW);
                                    it.putExtra("sms_body", sms_message);
                                    it.setType("vnd.android-dir/mms-sms");
                                    startActivity(it);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }


    public void runSave(String newNickName, String newDescription) throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("trackingNum", trackingNum);
        json.addProperty("nickname", newNickName);
        json.addProperty("description", newDescription);
        String jsonString = json.toString();

        String urlString = "http://52.33.77.245:3001/track/" + username;
        RequestBody body = RequestBody.create(JSON, jsonString);
        Request request = new Request.Builder()
                .header("content-type", "application/json")
                .url(urlString)
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Updated.", Toast.LENGTH_LONG).show();
                        startDeliveryActivity(username);
                    }
                });
            }
        });
    }

    public void runDelete() throws Exception {
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        String jsonString = json.toString();

        String urlString = "http://52.33.77.245:3001/track/" + trackingNum;
        RequestBody body = RequestBody.create(JSON, jsonString);
        Request request = new Request.Builder()
                .header("content-type", "application/json")
                .url(urlString)
                .delete(body)
                .build();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Item has been deleted.", Toast.LENGTH_LONG).show();
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
