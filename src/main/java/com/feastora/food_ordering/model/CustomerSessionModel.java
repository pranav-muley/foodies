package com.feastora.food_ordering.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CustomerSessionModel {
    private String sessionToken;
    private String userId;
    private Long tableNumber;
    private long createdAt;
    private long expiresAt;
    private String userAgent;
    private String ipAddress;
}
