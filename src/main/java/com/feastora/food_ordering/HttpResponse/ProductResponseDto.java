package com.feastora.food_ordering.HttpResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private String productId;
    private String name;
    private long price;
    private String imgUrl;
    private int qty;
    private int discount;
    private long dateCreated;
    private long dateModified;
}
