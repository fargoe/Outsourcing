package com.sparta.outsourcing.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewRequestDto {
    private int rating; // 1~5점
    private String reviewContent; // 리뷰 내용
}
