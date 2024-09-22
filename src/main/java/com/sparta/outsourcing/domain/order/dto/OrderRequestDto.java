package com.sparta.outsourcing.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderRequestDto {
    private final Long shopId;
    private final Long menuId;
    private final String address;
    private final String phoneNumber;
}
