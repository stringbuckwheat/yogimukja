package com.memil.yogimukja.review.service;

import com.memil.yogimukja.review.dto.ReviewDetail;
import com.memil.yogimukja.review.dto.ReviewRequest;
import com.memil.yogimukja.review.dto.ReviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

public interface ReviewService {
    /**
     * 사용자가 작성한 모든 리뷰 조회
     *
     * @param userId 사용자의 ID
     * @param pageable 페이지 정보
     * @return 사용자가 작성한 리뷰의 상세 정보 리스트
     */
    @Transactional(readOnly = true)
    List<ReviewDetail> getMyAllReview(Long userId, Pageable pageable);


    /**
     * 특정 레스토랑 리뷰 조회
     *
     * @param restaurantId 레스토랑의 ID
     * @param pageable 페이지 정보
     * @return 레스토랑 리뷰 리스트
     */
    @Transactional(readOnly = true)
    List<ReviewResponse> getReviewsBy(Long restaurantId, Pageable pageable);

    /**
     * 최근 작성된 리뷰 조회
     *
     * @param pageable 페이지 정보
     * @return 최근 작성된 리뷰 리스트
     */
    @Transactional(readOnly = true)
    List<ReviewResponse> getRecentReviews(Pageable pageable);

    /**
     * 맛집 리뷰 등록
     *
     * @param reviewRequest 리뷰 요청 객체 (평점 및 내용)
     * @param restaurantId 레스토랑 ID
     * @param userId 사용자 ID
     * @return 등록된 리뷰 응답 객체
     * @throws IllegalStateException 이미 평가한 레스토랑일 경우
     * @throws NoSuchElementException 레스토랑이나 사용자 정보가 없을 경우
     */
    @Transactional
    ReviewResponse add(ReviewRequest reviewRequest, Long restaurantId, Long userId);

    /**
     * 맛집 리뷰 수정
     *
     * @param reviewId 수정할 리뷰 ID
     * @param userId 사용자 ID
     * @param request 수정할 리뷰 요청 객체 (평점 및 내용)
     * @return 수정된 리뷰의 응답 객체
     * @throws NoSuchElementException 리뷰가 존재하지 않을 경우
     * @throws AccessDeniedException 사용자가 해당 리뷰의 소유자가 아닐 경우
     */
    @Transactional
    ReviewResponse update(Long reviewId, Long userId, ReviewRequest request);

    /**
     * 맛집 리뷰 삭제
     *
     * @param reviewId 삭제할 리뷰 ID
     * @param userId 사용자 ID
     * @throws NoSuchElementException 리뷰가 존재하지 않을 경우
     * @throws AccessDeniedException 사용자가 해당 리뷰의 소유자가 아닐 경우
     */
    @Transactional
    void delete(Long reviewId, Long userId);
}
