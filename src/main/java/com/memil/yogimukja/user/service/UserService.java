package com.memil.yogimukja.user.service;

import com.memil.yogimukja.auth.dto.AuthTokens;
import com.memil.yogimukja.user.dto.LocationRequest;
import com.memil.yogimukja.user.dto.LunchRequest;
import com.memil.yogimukja.user.dto.UserRequest;
import com.memil.yogimukja.user.dto.UserResponse;

public interface UserService {
    String hasSameUsername(String username);
    AuthTokens register(UserRequest userRequest);
    void updateLocation(LocationRequest locationRequest, Long userId);
    void updateLunchRecommendationStatus(LunchRequest lunchRequest, Long userId);
    UserResponse getDetail(Long userId);
    UserResponse update(UserRequest userRequest, Long userId);
    void delete(Long userId, String refreshToken);
}
