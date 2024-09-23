package com.sparta.outsourcing.domain.shop.dto;

import com.sparta.outsourcing.domain.menu.dto.MenuResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ShopResponseDto {
    private Long id;
    private String name;
    private LocalTime opentime;
    private LocalTime closetime;
    private BigDecimal minOrderAmount;
    private final List<MenuResponseDto> menuList;
}
