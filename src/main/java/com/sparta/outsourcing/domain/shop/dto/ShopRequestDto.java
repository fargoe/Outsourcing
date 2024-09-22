package com.sparta.outsourcing.domain.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class ShopRequestDto {
    private Long id;

    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;  // 상점 이름

    @NotBlank(message = "사장님 이름은 필수입니다.")
    private String username;  // 사장님의 사용자 이름

    @NotNull(message = "오픈 시간은 필수입니다.")
    private LocalTime opentime;  // 오픈 시간 (LocalTime)

    @NotNull(message = "마감 시간은 필수입니다.")
    private LocalTime closetime;  // 마감 시간 (LocalTime)

    @NotNull(message = "최소 주문 금액은 필수입니다.")
    private BigDecimal minOrderAmount;  // 최소 주문 금액
}
