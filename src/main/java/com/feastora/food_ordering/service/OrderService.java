package com.feastora.food_ordering.service;

import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.entity.Order;
import com.feastora.food_ordering.entity.Product;
import com.feastora.food_ordering.enums.OrderStatusEnum;
import com.feastora.food_ordering.mapping.MapperUtils;
import com.feastora.food_ordering.model.OrderModel;
import com.feastora.food_ordering.repository.OrderRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class OrderService extends BaseResponse {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public GenericResponse<OrderModel> getOrder(String orderId, String userId) {
        if (StringUtils.isBlank(orderId)) {
            return newRestErrorResponse(HttpStatus.BAD_REQUEST.value(), "orderId is required");
        }
        Order fetchedOrder = orderRepository.findOrderByUserIdAndOrderId(userId, orderId);
        if (fetchedOrder == null) {
            return newRestErrorResponse(404, "Order not found");
        }
        OrderModel orderModel = MapperUtils.convertObjectValueToResponseObject(fetchedOrder, OrderModel.class);
        if(orderModel == null) {
            return newRestErrorResponse(HttpStatus.NOT_FOUND.value(), "Can't able to Map Order to OrderModel");
        }
        return newRestResponseData(orderModel);
    }

    public OrderModel createOrder(String userId, long tableNumber, List<Product> products) {
        if(StringUtils.isBlank(userId) || CollectionUtils.isEmpty(products)) {
            return null;
        }
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(userId);
        order.setTableNumber(tableNumber);
        order.setProducts(products);
        order.setStatus("PENDING");
        order.setDateCreated(System.currentTimeMillis());
        order.setDateModified(System.currentTimeMillis());

       Order savedOrder = orderRepository.save(order);
       return  MapperUtils.convertObjectValueToResponseObject(savedOrder, OrderModel.class);
    }

    public List<Order> getTodayOrders(String userId) {
        try {
            LocalDate LocalDate = null;
            long startOfDay = java.time.LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endOfDay = java.time.LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

            return orderRepository.findOrdersByUserIdAndDateCreatedBetween(userId, startOfDay, endOfDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<OrderModel> getOrdersByStatus(String userId, OrderStatusEnum status) {
        if(StringUtils.isBlank(userId) || status == null) {
            return null;
        }
        List<Order> orders = orderRepository.findByUserIdAndStatus(userId, status.name());
        if(CollectionUtils.isEmpty(orders)) {
            return Collections.emptyList();
        }
        return MapperUtils.convertListOfObjectsToResponseObjects(orders, OrderModel.class);
    }


    public List<OrderModel> getAllOrders(String userId) {
        if (StringUtils.isBlank(userId)) {
            return Collections.emptyList();
        }

        List<Order> orders = orderRepository.findOrdersByUserId(userId);
        List<OrderModel> orderModels = new ArrayList<>();
        for (Order order : orders) {
            orderModels.add(MapperUtils.convertObjectValueToResponseObject(order, OrderModel.class));
        }
        return orderModels;
    }

    public GenericResponse<String> deleteOrder(String userId, String orderId) {
        if(orderId == null) {
            return newRestErrorResponse(400, "orderId is null");
        }
        try{
            Order order = orderRepository.findOrderByUserIdAndOrderId(userId, orderId);
            if(order == null) {
                return newRestErrorResponse(400, "order not found");
            }
            orderRepository.delete(order);
            return newRestResponseData("order deleted successfully");
        } catch (Exception e){
            return newRestErrorResponse(500, e.getMessage());
        }
    }

//    public OrderModel updateOrder(String orderId, Order order) {
//        if (StringUtils.isBlank(orderId)) {
//            return null;
//        }
//        String userId = null;
//        Order orderByUserId = orderRepository.findOrderByUserIdAndOrderId(userId, orderId);
//        if (orderByUserId == null) {
//            return null;
//        }
//        orderRepository.insert(order);
//        return MapperUtils.convertObjectValueToResponseObject(orderByUserId, OrderModel.class);
//
//
//    }
}
