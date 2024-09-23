package com.sparta.outsourcing.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuRequestDto {
    private Long menuId;
    private Long shopId;
    private String menu_name;
    private BigDecimal price;
}