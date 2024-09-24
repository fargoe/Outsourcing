package com.sparta.outsourcing.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {
    //권한 종류
    USER(Authority.USER),  // 사용자 권한
    OWNER(Authority.OWNER);  // 관리자 권한

    private final String authority;

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String OWNER = "ROLE_OWNER";
    }
}