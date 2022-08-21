package com.safayildirim.findmyticket.services;

import com.google.gson.Gson;
import com.safayildirim.findmyticket.OnCompleteListener;
import com.safayildirim.findmyticket.models.AddWatchingRequest;
import com.safayildirim.findmyticket.models.GetSessionsRequest;
import com.safayildirim.findmyticket.models.Session;
import com.safayildirim.findmyticket.models.SetTokenRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Client {
    private final String baseUrl = "http://10.0.2.2:5000";
    private final OkHttpClient client;
    private final Gson gson;

    public Client() {
        this.client = new OkHttpClient();
        gson = new Gson();
    }

    public void getSessions(GetSessionsRequest requestParams, OnCompleteListener<List<Session>> completeListener) {
        new Thread(() -> {
            long start = System.currentTimeMillis();
            List<Session> sessions = new ArrayList<>();
            String url = baseUrl + "/sessions";
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(gson.toJson(requestParams), JSON);
            Request request = new Request.Builder().url(url).post(body).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    completeListener.onFailure(response.message());
                    return;
                }
                JSONObject responseJSON = new JSONObject(response.body().string());
                JSONArray list = new JSONArray(responseJSON.getString("data"));
                for (int i = 0; i < list.length(); i++) {
                    sessions.add(Session.parseJSON(list.getJSONObject(i)));
                }
                long end = System.currentTimeMillis();
                System.out.println("Elapsed time: " + (end - start));
                completeListener.onSuccess(sessions);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setWatching(AddWatchingRequest requestParams, AddWatchingRequest.CommandType type, OnCompleteListener<Void> listener) {
        new Thread(() -> {
            Request request;
            if (type == AddWatchingRequest.CommandType.ADD) {
                String url = baseUrl + "/sessions/watching";
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(gson.toJson(requestParams), JSON);
                request = new Request.Builder().url(url).post(body).build();
            } else {
                String url = String.format("%s/sessions/watching/%s?time=%s", baseUrl, requestParams.getDate(), requestParams.getTime());
                request = new Request.Builder().url(url).delete().build();
            }
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    listener.onFailure(response.message());
                    return;
                }
                JSONObject responseJSON = new JSONObject(response.body().string());
                String error = responseJSON.getString("error");
                if (!error.equals("")) {
                    listener.onFailure(error);
                } else {
                    listener.onSuccess(null);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void getToken(OnCompleteListener<String> listener) {
        new Thread(() -> {
            String url = baseUrl + "/devices";
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    listener.onFailure(response.message());
                    return;
                }
                JSONObject responseJSON = new JSONObject(response.body().string());
                String error = responseJSON.getString("error");
                if (!error.equals("")) {
                    listener.onFailure(error);
                } else {
                    listener.onSuccess(responseJSON.getString("data"));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setToken(SetTokenRequest requestParams, OnCompleteListener<Void> listener) {
        new Thread(() -> {
            Request request;
            String url = baseUrl + "/devices";
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(gson.toJson(requestParams), JSON);
            request = new Request.Builder().url(url).post(body).build();
            try {
                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    listener.onFailure(response.message());
                    return;
                }
                JSONObject responseJSON = new JSONObject(response.body().string());
                String error = responseJSON.getString("error");
                if (!error.equals("")) {
                    listener.onFailure(error);
                } else {
                    listener.onSuccess(null);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
