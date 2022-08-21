package com.safayildirim.findmyticket.models;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Session {
    private String departure;
    private String arrival;
    private String duration;
    private int availableSeats;
    private boolean isWatching;


    public static Session parseJSON(JSONObject object) {
        Session session = new Session();
        try {
            session.departure = object.getString("departure");
            session.arrival = object.getString("arrival");
            session.duration = object.getString("duration");
            session.availableSeats = object.getInt("available_seats");
            session.isWatching = object.getBoolean("is_watching");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return session;
    }
}

