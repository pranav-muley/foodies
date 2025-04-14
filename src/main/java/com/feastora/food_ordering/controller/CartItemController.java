package com.feastora.food_ordering.controller;

import com.feastora.food_ordering.Errorhandling.ResponseError;
import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.entity.Product;
import com.feastora.food_ordering.model.CartItemModel;
import com.feastora.food_ordering.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/app/cartItem")
public class CartItemController extends BaseResponse {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/all")
    public ResponseEntity<GenericResponse<List<Product>>> getAllProducts(String userId, long tableNum) {
        List<Product> cartProducts = cartItemService.getCartItems(userId, tableNum);
        if (CollectionUtils.isEmpty(cartProducts)) {
            return badRequest(newRestErrorResponse(400, "For this Table Cart is Empty."));
        }
        return newResponseOk(newRestResponseData(cartProducts));
    }

    @PostMapping("/add")
    public ResponseEntity<GenericResponse<CartItemModel>> addProduct(@RequestBody Product product, @RequestParam String userId, @RequestParam long tableNumber) {
        CartItemModel cartItemModel = cartItemService.addProductInCart(product, userId, tableNumber);
        if (cartItemModel == null) {
            return badRequest(newRestErrorResponse(400, "Something went wrong, While adding Product to cartItem."));
        }

        return newResponseOk(newRestResponseData(cartItemModel));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<GenericResponse<CartItemModel>> removeProduct(@RequestBody Product product, @RequestParam String userId, @RequestParam long tableNumber) {
        GenericResponse<CartItemModel> response = new GenericResponse<>();
        response = cartItemService.removeProductFromCart(product, userId, tableNumber);
        if (response == null || response.getError() != null) {
            return badRequest(response);
        }
        return newResponseOk(response);
    }

    @PutMapping("/update")
    public ResponseEntity<GenericResponse<CartItemModel>> updateProduct(@RequestBody Product product, @RequestParam String userId, @RequestParam long tableNumber) {
        GenericResponse<CartItemModel> response = new GenericResponse<>();
        try {
            response = cartItemService.addOrUpdateProductInCart(product, userId, tableNumber);
            if (response == null || response.getError() != null) {
                return badRequest(response);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return internalServerError(newRestErrorResponse(500, e.getMessage()));
        }
    }
}
