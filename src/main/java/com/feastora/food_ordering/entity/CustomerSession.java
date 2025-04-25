package com.feastora.food_ordering.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("customer_sessions")
public class CustomerSession {

    private String sessionToken;
    private String userId;
    private Long tableNumber;

    private long createdAt;
    private long expiresAt;

    private Date expiresAtDate; // used for MongoDB TTL

    private String userAgent;
    private String ipAddress;

    public static CustomerSession create(String sessionId, String userId, Long tableNumber,
                                         String userAgent, String ipAddress, long durationMillis) {
        long now = System.currentTimeMillis();
        long expiry = now + durationMillis;

        return CustomerSession.builder()
                .sessionToken(sessionId)
                .userId(userId)
                .tableNumber(tableNumber)
                .createdAt(now)
                .expiresAt(expiry)
                .expiresAtDate(new Date(expiry)) // for Mongo TTL
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .build();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}
