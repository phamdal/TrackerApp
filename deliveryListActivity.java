package com.example.dalena.trackerapp;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class deliveryListActivity extends ListActivity {

    public static final String TAG = deliveryListActivity.class.getSimpleName();
    private String username;
    private String newUser;

    private Button addNewButton;
    private Button updateButton;
    private ImageButton exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_list_layout);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        newUser = intent.getStringExtra("newUser");

        TextView usernameDisplay = (TextView) findViewById(R.id.delieveryUserid);
        usernameDisplay.setText(username);

        addNewButton = (Button)findViewById(R.id.deliveryAdd);
        updateButton = (Button) findViewById(R.id.delieveryUpdate);
        exitButton = (ImageButton) findViewById(R.id.deliveryExit);


        final loadingDialog dialog = new loadingDialog(this);
        dialog.show();

        String getRequest = "http://52.33.77.245:3001/trackall/" + username;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(getRequest).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        packageTrack[] packageArray;
                        try {
                            if(newUser == null) {
                                packageArray = getPackages(jsonData);
                                packageAdapter adapter = new packageAdapter(deliveryListActivity.this, packageArray);
                                setListAdapter(adapter);
                            }
                            dialog.hide();

                            addNewButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startAddActivity();
                                }
                            });

                            updateButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startUpdateActivity();
                                }
                            });

                            exitButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(deliveryListActivity.this);
                                    builder.setMessage("Are you sure you want to sign out?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    try {
                                                        runSplash();
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }

    private packageTrack[] getPackages(String jsonData) throws JSONException {
        JSONArray data = new JSONArray(jsonData);

        packageTrack[] packageArray = new packageTrack[data.length()];
        for(int i = 0; i < data.length(); i++) {
            JSONObject currentPackage = data.getJSONObject(i);
            packageTrack itemPackage = new packageTrack();
            itemPackage.setTracking_num(currentPackage.getString("trackingNum"));
            itemPackage.setCarrier(currentPackage.getString("carrier"));
            itemPackage.setDescription(currentPackage.getString("description"));
            itemPackage.setNickName(currentPackage.getString("nickname"));
            itemPackage.setStatus(currentPackage.getString("status"));
            packageArray[i] = itemPackage;
        }

        return packageArray;
    }

    private void startAddActivity() {
        Intent intent = new Intent(this, addActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void startUpdateActivity() {
        Intent intent = new Intent(this, updateActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void startItemListActivity(String trackingNumber) {
        Intent intent = new Intent(this, itemListActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("trackingNum", trackingNumber);
        Log.v("trackingNum", trackingNumber);
        startActivity(intent);
    }

    public void onListItemClick(ListView listView, View view, int position, long id) {
        ListView myListView = getListView();
        Object itemClicked = myListView.getAdapter().getItem(position);
        startItemListActivity(itemClicked.toString());
    }

    public void runSplash() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
