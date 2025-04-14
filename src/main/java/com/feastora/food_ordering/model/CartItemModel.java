package com.feastora.food_ordering.model;

import com.feastora.food_ordering.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemModel {
    private long tableNumber;
    private String userId;
    private List<Product> products;
}
