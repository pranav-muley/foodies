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

    @Id
    private String id;

    private String token;
    private Long tableNumber;
    private String userId;

    private long createdAtEpoch;

    private long expiresAtEpoch;

    private Date expiresAt; // Use for Mongo TTL (index separately)
}
