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
@Document(collation = "cartItems")
public class CartItems {
    @Id
    private String id;
    private long tableNumber;
    private String userId;
    private List<Product> products;
}
