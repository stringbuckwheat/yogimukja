package com.memil.yogimukja.review.controller;

import com.memil.yogimukja.auth.model.UserCustom;
import com.memil.yogimukja.review.dto.ReviewDetail;
import com.memil.yogimukja.review.dto.ReviewRequest;
import com.memil.yogimukja.review.dto.ReviewResponse;
import com.memil.yogimukja.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 사용자가 작성한 모든 리뷰 조회
     *
     * @param user 사용자의 인증 정보
     * @param sort 정렬 기준 (최신: latest, 최고: highest, 최저: lowest)
     * @param pageable 페이지 정보 (페이지 번호 및 크기)
     * @return 사용자가 작성한 리뷰의 상세 정보 리스트
     */
    @GetMapping("/api/user/review")
    public ResponseEntity<List<ReviewDetail>> getMyAllReview(@AuthenticationPrincipal UserCustom user,
                                                             @RequestParam(name = "sort", defaultValue = "latest") String sort,
                                                             @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Pageable sortedPageable = createSortedPageable(sort, pageable);
        return ResponseEntity.ok().body(reviewService.getMyAllReview(user.getId(), sortedPageable));
    }

    /**
     * 특정 레스토랑에 대한 리뷰 조회
     *
     * @param restaurantId 레스토랑 ID
     * @param sort 정렬 기준 (최신: latest, 최고: highest, 최저: lowest)
     * @param pageable 페이지 정보 (페이지 번호 및 크기)
     * @return 레스토랑에 대한 리뷰 리스트
     */
    @GetMapping("/api/restaurant/{id}/review")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable(name = "id") Long restaurantId,
                                                           @RequestParam(name = "sort", defaultValue = "latest") String sort,
                                                           @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Pageable sortedPageable = createSortedPageable(sort, pageable);
        return ResponseEntity.ok().body(reviewService.getReviewsBy(restaurantId, sortedPageable));
    }

    /**
     * 최근 작성된 리뷰 조회
     *
     * @param pageable 페이지 정보 (페이지 번호 및 크기)
     * @return 최근 작성된 리뷰 리스트
     */
    @GetMapping("/api/review")
    public ResponseEntity<List<ReviewResponse>> getRecentReviews(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(reviewService.getRecentReviews(pageable));
    }

    /**
     * 맛집 리뷰 등록
     *
     * @param request 리뷰 요청 객체 (평점 및 내용)
     * @param restaurantId 레스토랑 ID
     * @param user 사용자의 인증 정보
     * @return 등록된 리뷰의 응답 객체
     */
    @PostMapping("/api/restaurant/{id}/review")
    public ResponseEntity<ReviewResponse> add(@RequestBody @Valid ReviewRequest request,
                                              @PathVariable(name = "id") Long restaurantId,
                                              @AuthenticationPrincipal UserCustom user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.add(request, restaurantId, user.getId()));
    }

    /**
     * 맛집 리뷰 수정
     *
     * @param request 수정할 리뷰 요청 객체 (평점 및 내용)
     * @param reviewId 수정할 리뷰 ID
     * @param user 사용자의 인증 정보
     * @return 수정된 리뷰의 응답 객체
     */
    @PutMapping("/api/review/{id}")
    public ResponseEntity<ReviewResponse> update(@RequestBody @Valid ReviewRequest request,
                                                 @PathVariable(name = "id") Long reviewId,
                                                 @AuthenticationPrincipal UserCustom user) {
        return ResponseEntity.ok().body(reviewService.update(reviewId, user.getId(), request));
    }

    /**
     * 맛집 리뷰 삭제
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @param user 사용자의 인증 정보
     * @return HTTP 상태 코드 204 No Content
     */
    @DeleteMapping("/api/review/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long reviewId,
                                       @AuthenticationPrincipal UserCustom user) {
        reviewService.delete(reviewId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 정렬 기준에 따른 PageRequest 생성
     *
     * @param sort 정렬 기준
     * @param pageable 기존 페이지 정보
     * @return 정렬된 페이지 정보
     */
    private Pageable createSortedPageable(String sort, Pageable pageable) {
        Sort sortCondition = getSortCondition(sort); // 정렬 객체
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortCondition);
    }

    /**
     * 정렬 기준에 따라 Sort 객체 반환
     *
     * @param sort 정렬 기준
     * @return 정렬 조건 객체
     */
    private Sort getSortCondition(String sort) {
        return switch (sort) {
            case "highest" ->
                // 평점 높은 순, 같으면 최신순
                    Sort.by(Sort.Order.desc("rate"), Sort.Order.desc("createdDate"));
            case "lowest" ->
                // 평점 낮은 순, 같으면 최신순
                    Sort.by(Sort.Order.asc("rate"), Sort.Order.desc("createdDate"));
            default ->
                // 기본 최신순
                    Sort.by(Sort.Order.desc("createdDate"));
        };
    }
}
