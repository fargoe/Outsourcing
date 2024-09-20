package com.sparta.outsourcing.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class ShopResponseDto {
    private Long id;
    private String name;
    private LocalTime opentime;
    private LocalTime closetime;
    private BigDecimal minOrderAmount;
}
