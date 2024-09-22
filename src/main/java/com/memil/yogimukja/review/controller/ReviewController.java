package com.memil.yogimukja.review.controller;

import com.memil.yogimukja.auth.model.UserCustom;
import com.memil.yogimukja.review.dto.ReviewRequest;
import com.memil.yogimukja.review.dto.ReviewResponse;
import com.memil.yogimukja.review.dto.ReviewUpdateRequest;
import com.memil.yogimukja.review.service.ReviewServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/api/restaurant/review")
    public ResponseEntity<List<ReviewResponse>> getMyAllReview(@AuthenticationPrincipal UserCustom user) {
        return ResponseEntity.ok().body(reviewService.getMyAllReview(user.getId()));
    }

    @PostMapping("/api/restaurant/{restaurantId}/review")
    public ResponseEntity<ReviewResponse> add(@RequestBody @Valid ReviewRequest request,
                                              @PathVariable(name = "restaurantId") Long restaurantId,
                                              @AuthenticationPrincipal UserCustom user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.add(request, restaurantId, user.getId()));
    }

    @PutMapping("/api/restaurant/{restaurantId}/review/{reviewId}")
    public ResponseEntity<ReviewResponse> update(@RequestBody @Valid ReviewRequest request,
                                                 @PathVariable(name = "restaurantId") Long restaurantId,
                                                 @PathVariable(name = "reviewId") Long reviewId,
                                                 @AuthenticationPrincipal UserCustom user) {

        ReviewUpdateRequest reviewRequest = ReviewUpdateRequest.builder()
                .restaurantId(restaurantId)
                .reviewId(reviewId)
                .userId(user.getId())
                .reviewRequest(request)
                .build();

        return ResponseEntity.ok()
                .body(reviewService.update(reviewRequest));
    }

    @DeleteMapping("/api/restaurant/review/{reviewId}")
    public ResponseEntity<Void> delete(@PathVariable(name = "reviewId") Long reviewId,
                                       @AuthenticationPrincipal UserCustom user) {

        reviewService.delete(reviewId, user.getId());

        return ResponseEntity.noContent().build();
    }
}
