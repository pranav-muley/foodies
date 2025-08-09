package com.feastora.food_ordering.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

@Document(collection = "user")
public class User {
    @Id
    private String _id;

    private String role;
    private String mobileNum;
    private String userName;
    private String password;

    @Indexed(unique = true)
    private String email;

    private boolean enabled;
    private long dateCreated;
    private long lastModified;
}
