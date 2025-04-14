package com.feastora.food_ordering.entity;

import com.feastora.food_ordering.enums.CategoryEnum;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "product")
public class Product {
    @Id
    private ObjectId _id;

    private String productId;
    private String productName;
    private String productDescription;
    private Long price;
    private String imgUrl;
    private int qty;
    private String discount;
    private String category;
    private List<Review> reviews;
    private long dateCreated;
    private long lastModified;
}
