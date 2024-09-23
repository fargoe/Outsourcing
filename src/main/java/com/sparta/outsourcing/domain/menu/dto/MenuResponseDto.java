package com.sparta.outsourcing.domain.menu.dto;

import lombok.*;

@Getter
@Builder
public class MenuResponseDto {
    private String message;
    private Data data;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Data{
        private String status;
    }
}