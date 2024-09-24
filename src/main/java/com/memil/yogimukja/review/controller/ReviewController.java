package com.memil.yogimukja.review.controller;

import com.memil.yogimukja.auth.model.UserCustom;
import com.memil.yogimukja.review.dto.ReviewDetail;
import com.memil.yogimukja.review.dto.ReviewRequest;
import com.memil.yogimukja.review.dto.ReviewResponse;
import com.memil.yogimukja.review.service.ReviewServiceImpl;
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
    private final ReviewServiceImpl reviewService;

    @GetMapping("/api/user/review")
    public ResponseEntity<List<ReviewDetail>> getMyAllReview(@AuthenticationPrincipal UserCustom user,
                                                             @RequestParam(name = "sort", defaultValue = "latest") String sort,
                                                             @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Pageable sortedPageable = createSortedPageable(sort, pageable);
        return ResponseEntity.ok().body(reviewService.getMyAllReview(user.getId(), sortedPageable));
    }

    @GetMapping("/api/restaurant/{id}/review")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable(name = "id") Long restaurantId,
                                                           @RequestParam(name = "sort", defaultValue = "latest") String sort,
                                                           @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Pageable sortedPageable = createSortedPageable(sort, pageable);
        return ResponseEntity.ok().body(reviewService.getReviews(restaurantId, sortedPageable));
    }

    @GetMapping("/api/review")
    public ResponseEntity<List<ReviewResponse>> getRecentReviews(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok().body(reviewService.getRecentReviews(pageable));
    }

    @PostMapping("/api/restaurant/{id}/review")
    public ResponseEntity<ReviewResponse> add(@RequestBody @Valid ReviewRequest request,
                                              @PathVariable(name = "id") Long restaurantId,
                                              @AuthenticationPrincipal UserCustom user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.add(request, restaurantId, user.getId()));
    }

    @PutMapping("/api/review/{id}")
    public ResponseEntity<ReviewResponse> update(@RequestBody @Valid ReviewRequest request,
                                                 @PathVariable(name = "id") Long reviewId,
                                                 @AuthenticationPrincipal UserCustom user) {
        return ResponseEntity.ok().body(reviewService.update(reviewId, user.getId(), request));
    }

    @DeleteMapping("/api/review/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long reviewId,
                                       @AuthenticationPrincipal UserCustom user) {
        reviewService.delete(reviewId, user.getId());
        return ResponseEntity.noContent().build();
    }

    private Pageable createSortedPageable(String sort, Pageable pageable) {
        Sort sortCondition = getSortCondition(sort); // 정렬 객체
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortCondition);
    }

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
