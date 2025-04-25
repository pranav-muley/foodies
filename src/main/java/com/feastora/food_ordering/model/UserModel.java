package com.feastora.food_ordering.model;

import com.feastora.food_ordering.entity.Order;
import com.feastora.food_ordering.entity.Product;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    @Builder.Default
    private String userId = UUID.randomUUID().toString();
    private String userName;
    private String password;
    private String email;
    private String mobileNum;
    private String role;
    private List<Order> prevOrders;
    private int rewardPts;
    private List<Product> favouriteProducts;
    private boolean enabled;
}
