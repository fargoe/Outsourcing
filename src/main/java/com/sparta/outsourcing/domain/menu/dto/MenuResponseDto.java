package com.sparta.outsourcing.domain.menu.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
public class MenuResponseDto {
    private String message;
    private Data data;

    private Long menuId;
    private String menuName;
    private BigDecimal price;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Data{
        private String status;
    }
}