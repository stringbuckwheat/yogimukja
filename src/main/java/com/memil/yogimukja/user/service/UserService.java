package com.memil.yogimukja.user.service;

import com.memil.yogimukja.auth.dto.AuthTokens;
import com.memil.yogimukja.common.error.exception.HasSameUsernameException;
import com.memil.yogimukja.user.dto.LunchRequest;
import com.memil.yogimukja.user.dto.UserRequest;
import com.memil.yogimukja.user.dto.UserResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public interface UserService {
    /**
     * 아이디 중복 검사
     *
     * @param username 확인할 사용자명
     * @return 유효한 아이디
     * @throws HasSameUsernameException 아이디 중복 시
     */
    String hasSameUsername(String username);

    /**
     * 회원가입
     *
     * @param userRequest 회원가입 요청 객체
     * @return 새로 등록된 사용자의 인증 토큰(액세스 토큰 및 리프레시 토큰)
     * @throws HasSameUsernameException 아이디 중복 시
     */
    AuthTokens register(UserRequest userRequest);

    /**
     * 사용자의 점심 추천 상태 업데이트
     *
     * @param lunchRequest 위도, 경도, 디스코드 Webhook
     * @param userId 사용자 ID
     * @throws UsernameNotFoundException 주어진 ID의 사용자가 존재하지 않을 경우
     */
    void updateLunchRecommendationStatus(LunchRequest lunchRequest, Long userId);

    /**
     * 사용자 상세 정보
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자의 상세 정보를 포함하는 응답 객체
     * @throws UsernameNotFoundException 주어진 ID의 사용자를 찾을 수 없는 경우
     */
    UserResponse getDetail(Long userId);

    /**
     * 사용자의 정보 수정
     *
     * @param userRequest 업데이트할 사용자 정보를 포함한 요청 객체
     * @param userId 업데이트할 사용자의 ID
     * @return 업데이트된 사용자 정보를 포함한 응답 객체
     * @throws UsernameNotFoundException 주어진 ID의 사용자를 찾을 수 없는 경우
     */
    UserResponse update(UserRequest userRequest, Long userId);

    /**
     * 회원 탈퇴 (리프레쉬 토큰 무효화)
     *
     * @param userId 탈퇴할 사용자 ID
     * @param refreshToken 무효화할 리프레쉬 토큰
     */
    void delete(Long userId, String refreshToken);
}
