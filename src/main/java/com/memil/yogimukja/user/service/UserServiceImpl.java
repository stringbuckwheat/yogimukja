package com.memil.yogimukja.user.service;

import com.memil.yogimukja.auth.service.AuthService;
import com.memil.yogimukja.common.error.ErrorMessage;
import com.memil.yogimukja.common.error.exception.HasSameUsernameException;
import com.memil.yogimukja.user.dto.LocationRequest;
import com.memil.yogimukja.user.dto.UserRequest;
import com.memil.yogimukja.user.dto.UserResponse;
import com.memil.yogimukja.user.entity.User;
import com.memil.yogimukja.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public String hasSameUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()) {
            throw new HasSameUsernameException(username + "은/는 이미 사용중인 아이디입니다.");
        }

        return username;
    }

    @Transactional
    @Override
    public void register(UserRequest registerRequest) {
        // 아이디 중복 검사
        hasSameUsername(registerRequest.getUsername());

        // 비밀번호 암호화
        registerRequest.encryptPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // 저장
        userRepository.save(registerRequest.toEntity());
    }

    @Transactional
    @Override
    public void updateLocation(LocationRequest locationRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        user.updateLocation(locationRequest.getLatitude(), locationRequest.getLongitude());
    }

    @Transactional
    @Override
    public void updateLunchRecommendationStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        user.updateLunchRecommendationEnabled();
    }

    // 회원 정보
    @Transactional(readOnly = true)
    @Override
    public UserResponse getDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        return new UserResponse(user);
    }

    // 회원 정보 수정
    @Transactional
    @Override
    public UserResponse update(UserRequest userRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        user.update(userRequest.getName());
        return new UserResponse(user);
    }

    // 탈퇴
    public void delete(Long userId, String refreshToken) {
        // TODO 쿠키 삭제

        // Redis 삭제
        authService.removeAuthentication(refreshToken);

        // 회원 삭제
        userRepository.deleteById(userId);
    }
}
