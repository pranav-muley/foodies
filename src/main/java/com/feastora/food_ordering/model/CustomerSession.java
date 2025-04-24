package com.feastora.food_ordering.model;

import lombok.Data;

@Data
public class CustomerSession {

    private String sessionToken;
    private String restaurantId;
    private int tableNumber;
    private String userAgent;
    private String ip;
    private long createdAt;
    private long expiresAt;

    public void setSessionToken(String sessionToken) {
    }
}
