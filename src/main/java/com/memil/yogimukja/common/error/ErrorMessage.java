package com.memil.yogimukja.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessage {
    WRONG_USERNAME("아이디를 확인해주세요"),
    WRONG_PASSWORD("비밀번호를 확인해주세요"),
    INVALID_ACCESS_TOKEN("유효하지 않은 액세스 토큰입니다"),
    CANNOT_LOGIN("로그인 할 수 없음"),
    ACCESS_TOKEN_EXPIRED("액세스 토큰 만료, 재발급 해주세요."),
    REFRESH_TOKEN_EXPIRED("리프레쉬 토큰 만료. 다시 로그인해주세요"),
    REFRESH_TOKEN_NOT_FOUND("Redis에 리프레쉬 토큰이 존재하지 않습니다."),
    NO_REFRESH_TOKEN("리프레쉬 토큰이 존재하지 않습니다."),
    PLEASE_LOGIN("로그인이 필요한 엔드포인트입니다."),

    ///// NOT_ FOUND
    USER_NOT_FOUND("해당 유저를 찾을 수 없습니다."),
    RESTAURANT_NOT_FOUND("해당 음식점을 찾을 수 없습니다."),
    REVIEW_NOT_FOUND("해당 리뷰를 찾을 수 없습니다."),

    ///// ACCESS_DENIED
    NOT_YOUR_REVIEW("해당 리뷰를 수정/삭제할 권한이 없어요"),

    ///// CONFLICT
    ALREADY_RATED_RESTAURANT("이 맛집에는 이미 리뷰를 작성했습니다."),

    ///// ILLEGAL
    ILLEGAL_RESTAURANT_SORT_OPTION("맛집 정렬은 거리 순, 평점 순 중 하나여야 합니다."),


    UNEXPECTED_ERROR_OCCUR("잠시 뒤 다시 시도해주세요");

    private String message;
}
