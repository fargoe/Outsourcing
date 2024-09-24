package com.sparta.outsourcing.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserRequestDto {
    @Email
    private String email;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\p{Punct}])[A-Za-z\\d\\p{Punct}]{8,20}$",
            message = "비밀번호는 8자에서 20자 사이여야 하며, 최소한 하나의 문자, 하나의 숫자, 그리고 하나의 특수 문자를 포함해야 합니다.")
    private String password;
    @Builder.Default
    private boolean owner = false;
    @Builder.Default
    private String ownerToken ="";

    public UserRequestDto(String email) {
        this.email = email;
    }
}
