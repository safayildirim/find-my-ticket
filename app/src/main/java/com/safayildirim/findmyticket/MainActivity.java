package com.safayildirim.findmyticket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.safayildirim.findmyticket.models.GetSessionsRequest;
import com.safayildirim.findmyticket.models.Session;
import com.safayildirim.findmyticket.models.SetTokenRequest;
import com.safayildirim.findmyticket.services.Client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private EditText timeEditText;
    private EditText departureEditText;
    private EditText arrivalEditText;
    private Client client;
    private MyRecyclerView myRecyclerView;
    private Activity currentActivity;
    List<Session> sessions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentActivity = this;
        client = new Client();
        timeEditText = findViewById(R.id.time_et);
        departureEditText = findViewById(R.id.departure_station_et);
        arrivalEditText = findViewById(R.id.arrival_station_et);
        String currentDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        timeEditText.setText(currentDate);
        recyclerView = findViewById(R.id.recyclerview);
        myRecyclerView = new MyRecyclerView(sessions, departureEditText.getText().toString(), arrivalEditText.getText().toString(), timeEditText.getText().toString());
        recyclerView.setLayoutManager(new LinearLayoutManager(currentActivity.getApplicationContext()));
        recyclerView.setAdapter(myRecyclerView);
        FirebaseApp.initializeApp(getApplicationContext());

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    client.getToken(new OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String tokenOnServer) {
                            if (!token.equals(tokenOnServer)) {
                                SetTokenRequest request = new SetTokenRequest(token);
                                client.setToken(request, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "getToken:onSuccess: true");
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Log.d(TAG, "getToken:onFailure: " + error);
                                        showDialog(error);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            showDialog(error);
                        }
                    });
                    Log.d(TAG, token);
                });
        GetSessionsRequest request =
                new GetSessionsRequest(departureEditText.getText().toString(), arrivalEditText.getText().toString(), currentDate);
        client.getSessions(request, new OnCompleteListener<List<Session>>() {
            @Override
            public void onSuccess(List<Session> sessions) {
                Log.d(TAG, "getSessions:onSuccess: true");
                currentActivity.runOnUiThread(() -> {
                    myRecyclerView.setSessions(sessions);
                    myRecyclerView.notifyDataSetChanged();
                    System.out.println(sessions);
                });
            }

            @Override
            public void onFailure(String error) {
                currentActivity.runOnUiThread(() -> {
                    Log.d(TAG, "getSessions:onFailure: " + error);
                    showDialog(error);
                });
            }
        });

        timeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                myRecyclerView.setDate(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void showDialog(String error) {
        currentActivity.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            String message = "An error occurred when getting information from server.\nError: " + error;
            builder.setMessage(message).setTitle("Error")
                    .setPositiveButton("Tamam", (dialog, id) -> {
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    public void fetchSessions(View view) {
        String date = timeEditText.getText().toString();
        GetSessionsRequest request =
                new GetSessionsRequest(departureEditText.getText().toString(), arrivalEditText.getText().toString(), date);
        client.getSessions(request, new OnCompleteListener<List<Session>>() {
            @Override
            public void onSuccess(List<Session> sessions) {
                currentActivity.runOnUiThread(() -> {
                    myRecyclerView.setSessions(sessions);
                    myRecyclerView.notifyDataSetChanged();
                    System.out.println(sessions);
                });
            }

            @Override
            public void onFailure(String error) {
                showDialog(error);
                System.out.println(error);
            }
        });
    }

    public void onSwitchButtonClicked(View view) {
        String departureStation = departureEditText.getText().toString();
        String arrivalStation = arrivalEditText.getText().toString();
        departureEditText.setText(arrivalStation);
        arrivalEditText.setText(departureStation);
    }
}

