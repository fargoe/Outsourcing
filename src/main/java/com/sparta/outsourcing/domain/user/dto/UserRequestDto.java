package com.sparta.outsourcing.domain.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;

@Getter
public class UserRequestDto {
    @Email
    private String email;
    private String password;
}
