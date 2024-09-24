package com.sparta.outsourcing.domain.shop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.outsourcing.domain.menu.dto.MenuResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ShopResponseDto {
    private Long id;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime opentime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime closetime;
    private BigDecimal minOrderAmount;
    private final List<MenuResponseDto> menuList;
}
