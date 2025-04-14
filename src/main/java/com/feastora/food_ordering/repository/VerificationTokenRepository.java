package com.feastora.food_ordering.repository;

import com.feastora.food_ordering.entity.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
     VerificationToken findVerificationTokenByTokenAndTableNumber(String token, Long tableNumber);
}
