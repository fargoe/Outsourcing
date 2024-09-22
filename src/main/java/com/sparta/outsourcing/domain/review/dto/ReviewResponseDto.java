package com.sparta.outsourcing.domain.review.dto;

import com.sparta.outsourcing.domain.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {
    private Long reviewId;
    private Long orderId;
    private Long userId;
    private Long shopId;
    private int rating;
    private String reviewContent;
    private LocalDateTime reviewTime;

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getId();
        this.orderId = review.getOrder().getId();
        this.userId = review.getUserId();
        this.shopId = review.getShopId();
        this.rating = review.getRating();
        this.reviewContent = review.getReviewContent();
        this.reviewTime = review.getReviewTime();
    }
}
