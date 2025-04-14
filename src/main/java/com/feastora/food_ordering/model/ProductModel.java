package com.feastora.food_ordering.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private String productId;
    private String userId;
    private String productName;
    private String productDescription;
    private int productPrice;
    private int productQuantity;
    private String productCategory;
    private String productImageUrl;
}
