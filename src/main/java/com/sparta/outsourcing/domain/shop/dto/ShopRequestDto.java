package com.sparta.outsourcing.domain.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ShopRequestDto {
    private String name;
    private LocalTime opentime;
    private LocalTime closetime;
    private BigDecimal minOrderAmount;
}
