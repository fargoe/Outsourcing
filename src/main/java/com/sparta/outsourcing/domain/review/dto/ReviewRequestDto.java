package com.sparta.outsourcing.domain.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewRequestDto {

    @Min(1)
    @Max(5)
    private int rating;

    @NotBlank
    @Size(max = 100)
    private String reviewContent;
}
