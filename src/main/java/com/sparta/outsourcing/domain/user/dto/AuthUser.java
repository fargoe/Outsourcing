package com.sparta.outsourcing.domain.user.dto;

import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import lombok.Getter;

@Getter
public class AuthUser {
    private final Long id;
    private final UserRoleEnum role;
    private final String email;

    public AuthUser(Long id,UserRoleEnum role,String email){
        this. id = id;
        this.role = role;
        this.email = email;
    }
}