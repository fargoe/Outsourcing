package com.sparta.outsourcing.domain.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderRequestDto {

    private Long shopId;
    private Long menuId;

    @NotNull(message = "주소는 필수 값입니다.")
    @NotBlank(message = "주소는 공백일 수 없습니다.")
    private String address;

    @NotNull(message = "전화번호는 필수 값입니다.")
    @NotBlank(message = "전화번호는 공백일 수 없습니다.")
    private String phoneNumber;
}
