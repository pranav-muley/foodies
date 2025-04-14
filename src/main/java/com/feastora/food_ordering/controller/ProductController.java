package com.feastora.food_ordering.controller;

import ch.qos.logback.core.util.StringUtil;
import com.feastora.food_ordering.Errorhandling.ResponseError;
import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.HttpResponse.ProductResponseDto;
import com.feastora.food_ordering.entity.Product;
import com.feastora.food_ordering.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/products")
public class ProductController extends BaseResponse {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public ResponseEntity<GenericResponse<ProductResponseDto>> getProductDetail(@RequestParam("productId") String productId) {
        GenericResponse<ProductResponseDto> response = new GenericResponse<>();
        response = productService.getProductDetailById(productId);
        if (ObjectUtils.isEmpty(response) || response.getData() == null) {
            return badRequest(newRestErrorResponse(400, "Product id is required"));
        }
        return newResponseOk(response);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericResponse<List<ProductResponseDto>>> getAllProducts() {
        List<ProductResponseDto> productResponseDtoList = productService.getAllProductsFromDB();
        if (CollectionUtils.isEmpty(productResponseDtoList)) {
            badRequest(newRestErrorResponse(409, "Product list is empty", "No products found in DB"));
        }

        return newResponseOk(newRestResponseData(productResponseDtoList));
    }

    @PostMapping("/add")
    public ResponseEntity<GenericResponse<String>> addProductDetails(@RequestBody Product product) {

        if (ObjectUtils.isEmpty(product)) {
            return badRequest(newRestErrorResponse(404, "Product having empty", "Please check Product Id once"));
        }

        String status = productService.addProductInDB(product);
        if (status == null) {
            return badRequest(newRestErrorResponse(404, "Product having empty", "Please check Product fields"));
        }

        return newResponseOk(newRestResponseData("Product added successfully"));
    }

    @PostMapping("/add/all")
    public ResponseEntity<GenericResponse<List<Product>>> addProductDetails(@RequestBody List<Product> products) {
        List<Product> savedProducts = productService.addAllProducts(products);

        if (ObjectUtils.isEmpty(savedProducts)) {
            return badRequest(newRestErrorResponse(404, "Product having empty", "Product Not added."));
        }
        return newResponseOk(newRestResponseData(savedProducts));
    }

    public ResponseEntity<GenericResponse<List<Product>>> getProductsByCategory(@RequestParam("categoryName") String categoryName) {
        GenericResponse<List<Product>> response = new GenericResponse<>();
        if(StringUtils.isEmpty(categoryName)) {
            return badRequest(newRestErrorResponse(400, "Category name is required"));
        }
        response = productService.getAllProductsByCategoryName(categoryName);
        if (ObjectUtils.isEmpty(response) || response.getData().isEmpty()) {
            badRequest(newRestErrorResponse(404, "Products of this category not found"));
        }
        return newResponseOk(response);
    }
}
