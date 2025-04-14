package com.feastora.food_ordering.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Calendar;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "verificationToken")

public class VerificationToken {

    private static final int EXPIRATION_TIME = 10;

    @Id
    private String _id;

    @Indexed(unique = true)
    private String token;
    private Long tableNumber;
    private String userId;
    public VerificationToken(String userId, long tableNumber, String token) {
        this.userId = userId;
        this.tableNumber = tableNumber;
        this.token = token;
    }
    public VerificationToken(String token) {
        super();
        this.token = token;
    }
}
