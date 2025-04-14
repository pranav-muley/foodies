package com.feastora.food_ordering.service;

import com.feastora.food_ordering.Errorhandling.ResponseError;
import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.HttpResponse.ProductResponseDto;
import com.feastora.food_ordering.enums.CategoryEnum;
import com.feastora.food_ordering.mapping.MapperUtils;
import com.feastora.food_ordering.entity.Product;
import com.feastora.food_ordering.repository.ProductRepo;
import org.apache.catalina.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductService  extends BaseResponse {
    private final ProductRepo productRepo;

    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public GenericResponse<ProductResponseDto> getProductDetailById(String productId) {
        if(StringUtils.isEmpty(productId)) {
            return newRestErrorResponse(400, "Product ID is Required.");
        }

        Product product = productRepo.findByProductId(productId);
        ProductResponseDto productResponseDto = MapperUtils.convertObjectValueToResponseObject(product, ProductResponseDto.class);
        if (productResponseDto == null) {
            newRestErrorResponse(404, "Product not found.");
        }
        return newRestResponseData(productResponseDto);
    }

    public List<ProductResponseDto> getAllProductsFromDB() {
        List<Product> products = productRepo.findAll();
        if (ObjectUtils.isEmpty(products)) {
            return null;
        }
        return MapperUtils.convertListOfObjectsToResponseObjects(products, ProductResponseDto.class);
    }

    public String addProductInDB(Product product){
        if(product == null){
            return null;
        }
        productRepo.save(product);
        return product.getProductId();
    }

    public List<Product> addAllProducts(List<Product> products){
        if (CollectionUtils.isEmpty(products)) {
            return null;
        }
        return productRepo.saveAll(products);
    }

    public GenericResponse<List<Product>> getAllProductsByCategoryName(String categoryName) {
        if (categoryName == null) {
            return newRestErrorResponse(400, "Product category name related", "Please check categoryName is Valid");
        }

        CategoryEnum category = getCategoryByName(categoryName);
        if(category == null){
            return newRestErrorResponse(404, "Product CategoryEnum Not matchable");
        }
        List<Product> products = productRepo.getProductsByCategory(category.name());
        if (CollectionUtils.isEmpty(products)) {
            return newRestErrorResponse(404, "Products of this category not found");
        }
        return newRestResponseData(products);
    }

    private CategoryEnum getCategoryByName(String categoryName) {
        CategoryEnum[] categoryMap = CategoryEnum.values();
        for (CategoryEnum category : categoryMap) {
            if(category.name().equals(categoryName)){
                return category;
            }
        }
        return null;
    }
}

