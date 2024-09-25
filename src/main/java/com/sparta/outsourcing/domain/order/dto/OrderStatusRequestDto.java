package com.sparta.outsourcing.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderStatusRequestDto {
    @NotBlank
    private String newStatus;

    public OrderStatusRequestDto(String newStatus) {
        this.newStatus = newStatus;
    }
}
