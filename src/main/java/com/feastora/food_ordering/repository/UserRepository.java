package com.feastora.food_ordering.repository;

import com.feastora.food_ordering.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User findUserByEmail(String email);
    User findUserByMobileNum(String mobileNum);
    User findUserByUserNameAndMobileNum(String userName, String mobileNum);

    @Query("{ 'userId' : ?0 }")
    @Update("{ '$set' : { 'enabled' : true } }")
    void enableUserByUserId(String userId);

    @Query("{ 'userId' : ?0 }")
    @Update("{'$set' :  {'lastModified' : System.currentTimeMillis() } }")
    void updateLastModifiedUserByUserId(String userId);
}
