package com.feastora.food_ordering.service;

import ch.qos.logback.core.util.StringUtil;
import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.entity.CartItems;
import com.feastora.food_ordering.entity.Product;
import com.feastora.food_ordering.mapping.MapperUtils;
import com.feastora.food_ordering.model.CartItemModel;
import com.feastora.food_ordering.repository.CartItemRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartItemService extends BaseResponse {
    private final CartItemRepo cartItemRepo;

    public CartItemService(CartItemRepo cartItemRepo) {
        this.cartItemRepo = cartItemRepo;
    }

    public List<Product> getCartItems(String userId, long tableNumber) {
        CartItems cartItem = cartItemRepo.findByUserIdAndTableNumber(userId, tableNumber);
        if (cartItem == null) {
            return null;
        }
        return cartItem.getProducts();
    }

    public CartItemModel addProductInCart(Product product, String userId, long tableNumber) {
        if(ObjectUtils.isEmpty(product) || StringUtil.isNullOrEmpty(userId) || tableNumber <= 0) {
            return null;
        }

        CartItems cartItem = cartItemRepo.findCartItemContainingProduct(userId, tableNumber, product.getProductId());
        if (!ObjectUtils.isEmpty(cartItem)) {
            return null;
        }

        List<Product> products = cartItem.getProducts();
        products.add(product);
        cartItem.setProducts(products);
        CartItems saved = cartItemRepo.save(cartItem);
        return MapperUtils.convertObjectValueToResponseObject(saved, CartItemModel.class);
    }

    public GenericResponse<CartItemModel> removeProductFromCart(Product product, String userId, long tableNumber) {
        if(ObjectUtils.isEmpty(product) || StringUtil.isNullOrEmpty(userId) || tableNumber <= 0 || product.getProductId() == null) {
            return newRestErrorResponse(404, "Parameter passed is null or empty");
        }

        CartItems cartItem = cartItemRepo.findCartItemContainingProduct(userId, tableNumber, product.getProductId());
        if (cartItem == null) {
            return newRestErrorResponse(400, "CartItem List Empty", "User Not added product in Cart");
        }
        try {
            List<Product> products = cartItem.getProducts();
            products.remove(product);
            CartItems saved = cartItemRepo.save(cartItem);
            CartItemModel model = MapperUtils.convertObjectValueToResponseObject(saved, CartItemModel.class);
            return newRestResponseData(model);
        } catch (Exception e) {
            e.printStackTrace();
            return newRestErrorResponse(500, "Internal Server Error", "Internal Server Error");
        }
    }

    public GenericResponse<CartItemModel> addOrUpdateProductInCart(Product product, String userId, long tableNumber) {
        if(ObjectUtils.isEmpty(product) || StringUtil.isNullOrEmpty(userId) || StringUtil.isNullOrEmpty(String.valueOf(tableNumber))) {
            return newRestErrorResponse(400, "Parameter passed is null or empty");
        }

        CartItems cartItem = cartItemRepo.findByUserIdAndTableNumber(userId, tableNumber);
        CartItemModel cartItemModel = null;
        if (cartItem == null) {
            // Cart does not exist: create a new one with this product.
            List<Product> products = new ArrayList<>();
            products.add(product);
            CartItems cartItemNew = CartItems.builder()
                    .userId(userId)
                    .tableNumber(tableNumber)
                    .products(products)
                    .build();
            CartItems saved = cartItemRepo.save(cartItemNew);
            if(saved == null) {
                return newRestErrorResponse(404, "CartItem List Empty", "User Not added product in Cart");
            }
            cartItemModel =  MapperUtils.convertObjectValueToResponseObject(saved, CartItemModel.class);
            if(cartItemModel == null) {
                return newRestErrorResponse(404, "Mapping Issue", "Something went wrong while mapping CartItem to CartItemModel");
            }
            return newRestResponseData(cartItemModel);
        } else {
            // Cart exists: check if the product is already there.
            List<Product> products = cartItem.getProducts();
            boolean updated = false;

            for (int i = 0; i < products.size(); i++) {
                Product existingProduct = products.get(i);
                if (existingProduct.getProductId().equals(product.getProductId())) {
                    // If product exists: update the details (example: quantity or price).
                    product.setProductId(existingProduct.getProductId());
                    product.set_id(existingProduct.get_id());
                    existingProduct = product;
                    products.set(i, existingProduct);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                products.add(product);
            }
            cartItem.setProducts(products);
            CartItems updatedCart = cartItemRepo.save(cartItem);
            cartItemModel = MapperUtils.convertObjectValueToResponseObject(updatedCart, CartItemModel.class);
            if(cartItemModel == null) {
                return newRestErrorResponse(404, "Mapping Issue", "Something went wrong while mapping CartItem to CartItemModel");
            }
            return newRestResponseData(cartItemModel);
        }
    }
}
