package com.memil.yogimukja.restaurant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum RestaurantType {
    KOREAN("한식", List.of("한식", "냉면집", "복어취급", "식육(숯불구이)", "탕류(보신용)")),
    BUNSIK("분식", List.of("분식", "김밥(도시락)")),
    WESTERN("양식/경양식", List.of("양식", "경양식", "패밀리레스트랑")), // 레스'트'랑 오타 아니므로 수정 X
    JAPANESE("돈까스/회/일식", List.of("일식", "횟집")),
    CHICKEN("치킨", List.of("치킨", "통닭(치킨)", "호프/통닭")),
    PIZZA("피자", List.of("피자")),
    BAR("술집", List.of("술집", "감성주점", "정종/대포집/소주방")),
    CAFE("카페/디저트", List.of("까페", "카페", "커피숍", "전통찻집", "제과점영업")),
    BUFFET("뷔페", List.of("뷔페", "뷔페식")),
    CHINESE("중식", List.of("중국식")),
    WORLD("세계음식", List.of("세계음식", "외국음식전문점(인도,태국등)", "외국음식전문점")),
    FAST_FOOD("패스트푸드", List.of("패스트푸드")),
    ETC("기타", List.of("기타"));

    private String title;
    private List<String> values;


    public static RestaurantType getType(String rawType) {
        String type = rawType.trim();
        return Arrays.stream(RestaurantType.values())
                .filter(restaurantType -> restaurantType.values.contains(type))
                .findFirst()
                .orElse(ETC); // 기본값은 '기타' 카테고리
    }

    public static RestaurantType getInstance(String rawValue) {
        String value = rawValue.trim().toUpperCase();
        return RestaurantType.valueOf(value);
    }
}
