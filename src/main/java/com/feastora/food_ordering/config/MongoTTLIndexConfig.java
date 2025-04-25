package com.feastora.food_ordering.config; // Place it in your config package (or any package)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import jakarta.annotation.PostConstruct;

// Marks this as a configuration class
@Configuration
public class MongoTTLIndexConfig {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoTTLIndexConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void createTTLIndexes() {
        // Ensure TTL Index for 'customer_sessions' collection
        mongoTemplate.indexOps("customer_sessions")
                .ensureIndex(new Index()
                        .on("expiresAtDate", org.springframework.data.domain.Sort.Direction.ASC) // Use your actual field name
                        .expire(0)); // 0 seconds for TTL, or adjust as needed

        // Ensure TTL Index for 'verificationToken' collection
        mongoTemplate.indexOps("verificationToken")
                .ensureIndex(new Index()
                        .on("expiresAtDate", org.springframework.data.domain.Sort.Direction.ASC) // Use your actual field name
                        .expire(0)); // 0 seconds for TTL, or adjust as needed
    }
}
