package com.sparta.outsourcing.domain.review.controller;

import com.sparta.outsourcing.domain.review.dto.ReviewRequestDto;
import com.sparta.outsourcing.domain.review.dto.ReviewResponseDto;
import com.sparta.outsourcing.domain.review.service.ReviewService;
import com.sparta.outsourcing.domain.user.dto.AuthUser;
import com.sparta.outsourcing.global.annotation.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 생성
    @PostMapping("/orders/{orderId}/reviews")
    public ResponseEntity<Map<String, Object>> createReview(
            @PathVariable Long orderId,
            @Valid @RequestBody ReviewRequestDto reviewRequestDto,
            @Auth AuthUser authUser) {

        Long userId = authUser.getId();
        ReviewResponseDto reviewResponse = reviewService.createReview(orderId, reviewRequestDto, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "리뷰 작성 완료");
        response.put("data", reviewResponse);

        return ResponseEntity.ok(response);
    }

    // 리뷰 조회
    @GetMapping("/shops/{shopId}/reviews")
    public ResponseEntity<Map<String, Object>> getShopReviews(@PathVariable Long shopId,
                                                              @RequestParam(value = "minRating", required = false) Integer minRating,
                                                              @RequestParam(value = "maxRating", required = false) Integer maxRating) {
        List<ReviewResponseDto> reviews = reviewService.getShopReviews(shopId, minRating, maxRating);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "리뷰 조회 성공");
        response.put("data", reviews);

        return ResponseEntity.ok(response);
    }
}
