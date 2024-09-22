package com.sparta.outsourcing.domain.review.controller;

import com.sparta.outsourcing.domain.review.dto.ReviewRequestDto;
import com.sparta.outsourcing.domain.review.dto.ReviewResponseDto;
import com.sparta.outsourcing.domain.review.service.ReviewService;
import com.sparta.outsourcing.domain.user.dto.AuthUser;
import com.sparta.outsourcing.global.annotation.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    //리뷰 생성
    @PostMapping("/orders/{orderId}/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable Long orderId,
                                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                                          @Auth AuthUser authUser) {
        Long userId = authUser.getId();
        return ResponseEntity.ok(reviewService.createReview(orderId, reviewRequestDto, userId));
    }

    // 리뷰 조회
    @GetMapping("/shops/{shopId}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getShopReviews(@PathVariable Long shopId,
                                                                  @RequestParam(value = "minRating", required = false) Integer minRating,
                                                                  @RequestParam(value = "maxRating", required = false) Integer maxRating) {
        return ResponseEntity.ok(reviewService.getShopReviews(shopId, minRating, maxRating));
    }
}
