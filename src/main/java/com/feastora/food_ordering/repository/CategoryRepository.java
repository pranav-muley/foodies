package com.feastora.food_ordering.repository;

import com.feastora.food_ordering.entity.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<Product, ObjectId> {
}
