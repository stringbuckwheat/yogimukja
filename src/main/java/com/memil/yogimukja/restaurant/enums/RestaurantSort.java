package com.memil.yogimukja.restaurant.enums;


import com.memil.yogimukja.common.error.ErrorMessage;

// 정렬 기능
public enum RestaurantSort {
    DISTANCE,
    RATING;

    public static RestaurantSort from(String value) {
        try {
            System.out.println(value);
            return RestaurantSort.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(ErrorMessage.ILLEGAL_RESTAURANT_SORT_OPTION.getMessage());
        }
    }
}
