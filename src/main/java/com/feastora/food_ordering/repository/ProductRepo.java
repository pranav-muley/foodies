package com.feastora.food_ordering.repository;

import com.feastora.food_ordering.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepo extends MongoRepository<Product, String> {
    Product findByProductId(String productId);
    List<Product> getProductsByCategory(String category);
}
