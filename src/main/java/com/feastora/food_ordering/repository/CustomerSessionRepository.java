package com.feastora.food_ordering.repository;

import com.feastora.food_ordering.model.CustomerSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerSessionRepository extends MongoRepository<CustomerSession, String> {
    CustomerSession findBySessionToken(String sessionToken);
    void deleteByExpiresAtBefore(long expiry);
}
