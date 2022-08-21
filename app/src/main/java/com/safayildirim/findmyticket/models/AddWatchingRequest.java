package com.safayildirim.findmyticket.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddWatchingRequest {
    private String departure;
    private String arrival;
    private String date;
    private String time;

    public enum CommandType {
        ADD,
        REMOVE
    }
}
