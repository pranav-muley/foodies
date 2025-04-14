package com.feastora.food_ordering.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String _id;

    private String orderId;
    private String userId;
    private long tableNumber;
    private String status;
    private List<Product> products;
    private long dateCreated;
    private long dateModified;
}
