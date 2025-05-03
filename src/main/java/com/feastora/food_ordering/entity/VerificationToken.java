package com.feastora.food_ordering.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "verificationToken")
public class VerificationToken {

    private static final int EXPIRATION_MINUTES = 10;

    @Id
    private String id;

    private String token;
    private Long tableNumber;
    private String userId;

    private long createdAtEpoch;

    private long expiresAtEpoch;

    private Date expiresAt; // Use for Mongo TTL (index separately)

    public VerificationToken(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public static VerificationToken create(String userId, String token) {
        long now = System.currentTimeMillis();
        long expiry = now + EXPIRATION_MINUTES * 60 * 1000L;
        return VerificationToken.builder()
                .userId(userId)
                .token(token)
                .createdAtEpoch(now)
                .expiresAtEpoch(expiry)
                .expiresAt(new Date(expiry)) // required for TTL
                .build();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > this.expiresAtEpoch;
    }
}
