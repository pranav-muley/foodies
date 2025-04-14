package com.feastora.food_ordering.repository;

import com.feastora.food_ordering.entity.CartItems;
import com.feastora.food_ordering.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepo extends MongoRepository<CartItems, String> {
    CartItems findByUserIdAndTableNumber(String userId, long tableNumber);

    @Query("{ 'userId' : ?0, 'tableNumber' : ?1, 'products.id' : ?2 }")
    CartItems findCartItemContainingProduct(String userId, long tableNumber, String productId);

}
