package com.safayildirim.findmyticket.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetSessionsRequest {
    private String departure;
    private String arrival;
    private String date;
}
