package com.feastora.food_ordering.controller;

import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.entity.Order;
import com.feastora.food_ordering.entity.Product;
import com.feastora.food_ordering.enums.OrderStatusEnum;
import com.feastora.food_ordering.model.OrderModel;
import com.feastora.food_ordering.repository.OrderRepository;
import com.feastora.food_ordering.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController extends BaseResponse {
    @Autowired
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping("/get")
    public ResponseEntity<GenericResponse<OrderModel>> getOrderDetail(@RequestParam String orderId, @RequestParam String userId) {
        GenericResponse<OrderModel> response = new GenericResponse<>();
        response = orderService.getOrder(orderId, userId);
        if (ObjectUtils.isEmpty(response) || response.getData() == null) {
            return notFound(response);
        }
        return newResponseOk(response);
    }

    @PostMapping("/create")
    public ResponseEntity<GenericResponse<OrderModel>> createOrder(@RequestParam String userId, @RequestParam long tableNumber, List<Product> products) {
        GenericResponse<OrderModel> response = new GenericResponse<>();
        OrderModel orderCreated = orderService.createOrder(userId, tableNumber, products);
        if (ObjectUtils.isEmpty(orderCreated)) {
            return notFound(response);
        }
        return newResponseOk(newRestResponseData(orderCreated));
    }

    // This is for admin to get all orders for day end
    @GetMapping("/all")
    public ResponseEntity<GenericResponse<List<OrderModel>>> getAllOrders(@RequestParam String userId) {
        GenericResponse<List<OrderModel>> response = new GenericResponse<>();
        List<OrderModel> orders = orderService.getAllOrders(userId);
        if (ObjectUtils.isEmpty(orders)) {
            return notFound(response);
        }
        response.setData(orders);
        return newResponseOk(response);
    }

    @GetMapping("/by-status")
    public ResponseEntity<GenericResponse<List<OrderModel>>> getActiveOrders(@RequestParam String userId, @RequestParam OrderStatusEnum status) {
        GenericResponse<List<OrderModel>> response = new GenericResponse<>();
        List<OrderModel> orderModels = orderService.getOrdersByStatus(userId, status);
        if (ObjectUtils.isEmpty(orderModels)) {
            return notFound(response);
        }
        return newResponseOk(newRestResponseData(orderModels));
    }

    @DeleteMapping("/delete/by-orderId")
    public ResponseEntity<GenericResponse<String>> deleteOrder(@RequestParam String orderId, @RequestParam String userId) {
        GenericResponse<String> response = new GenericResponse<>();
        if (orderId == null || userId == null) {
            return badRequest(response);
        }
        response = orderService.deleteOrder(orderId, userId);
        if (ObjectUtils.isEmpty(response) || response.getData() == null) {
            return notFound(response);
        }
        return newResponseOk(response);
    }


    // this for admin
//    @PutMapping("/modify")
//    public ResponseEntity<GenericResponse<OrderModel>> modifyOrder(@RequestParam String orderId, List<Product> products) {
//        GenericResponse<OrderModel> response = new GenericResponse<>();
//        if(ObjectUtils.isEmpty(order)) {
//            return notFound(response);
//        }
//        String orderId = order.getOrderId();
//        OrderModel orderModel = orderService.updateOrder(orderId, order);
//        if(ObjectUtils.isEmpty(orderModel)) {
//            return notFound(response);
//        }
//        response.setData(orderModel);
//        return newResponseOk(response);
//    }
}
