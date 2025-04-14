package com.feastora.food_ordering.repository;

import com.feastora.food_ordering.entity.Order;
import com.feastora.food_ordering.enums.OrderStatusEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findOrdersByUserId(String userId);
    Order findOrderByUserIdAndOrderId(String userId, String orderId);

    List<Order> findOrdersByUserIdAndDateCreatedBetween(String userId, long startOfDay, long endOfDay);

//    List<Order> findOrdersByUserIdandStatus(String userId, OrderStatusEnum status);

    List<Order> findByUserIdAndStatus(String userId, String status);
}
