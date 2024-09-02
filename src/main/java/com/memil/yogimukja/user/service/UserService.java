package com.memil.yogimukja.user.service;

import com.memil.yogimukja.user.dto.LocationRequest;
import com.memil.yogimukja.user.dto.UserRequest;
import com.memil.yogimukja.user.dto.UserResponse;

public interface UserService {
    String hasSameUsername(String username);
    void register(UserRequest userRequest);
    void updateLocation(LocationRequest locationRequest, Long userId);
    void updateLunchRecommendationStatus(Long userId);
    UserResponse getDetail(Long userId);
    UserResponse update(UserRequest userRequest, Long userId);
    void delete(Long userId, String refreshToken);
}
