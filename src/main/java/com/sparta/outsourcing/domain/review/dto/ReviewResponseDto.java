package com.sparta.outsourcing.domain.review.dto;

import com.sparta.outsourcing.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private Long userId;
    private int rating;
    private String reviewContent;
    private LocalDateTime reviewTime;

    public ReviewResponseDto(Long reviewId, Long userId, int rating, String reviewContent, LocalDateTime reviewTime) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.reviewTime = reviewTime;
    }

    public ReviewResponseDto(Review review) {
        this.reviewId = review.getId();
        this.userId = review.getUserId();
        this.rating = review.getRating();
        this.reviewContent = review.getReviewContent();
        this.reviewTime = review.getReviewTime();
    }
}
