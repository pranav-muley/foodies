package com.feastora.food_ordering.enums;

public enum CategoryEnum {
    BEVERAGE("beverage"),
    VEGETATION("vegetation"),
    SNACK("snack"),
    BREAK_FAST_FOOD("breakfast"),
    NON_VEGETARIAN("non-vegetarian");

    CategoryEnum(String category) {
    }

    public  CategoryEnum getEnum(String category) {
        return CategoryEnum.valueOf(category);
    }
}
