package com.sparta.outsourcing.domain.menu.service;

import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import com.sparta.outsourcing.domain.user.dto.UserRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MenuServiceTest {

    private User owner;

    @BeforeEach
    void setUp() {
        // UserRequestDto를 빌더 패턴으로 초기화
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email("owner@example.com")
                .password("password")
                .owner(true)
                .build();

        // User 객체 초기화
        owner = new User(userRequestDto, "password", UserRoleEnum.OWNER);
    }

    @Test
    void testOwnerEmail() {
        // 테스트: 소유자의 이메일이 올바르게 초기화되었는지 확인
        assertEquals("owner@example.com", owner.getEmail());
    }

    @Test
    void testOwnerPassword() {
        // 테스트: 소유자의 비밀번호가 올바르게 초기화되었는지 확인
        assertEquals("password", owner.getPassword());
    }

    @Test
    void testOwnerRole() {
        // 테스트: 소유자의 역할이 OWNER로 올바르게 설정되었는지 확인
        assertEquals(UserRoleEnum.OWNER, owner.getRole());
    }

    @Test
    void testOwnerInitialization() {
        // 테스트: 소유자 객체가 null이 아닌지 확인
        assertNotNull(owner);
    }

    @Test
    void testUserCreationWithInvalidEmail() {
        // 테스트: 잘못된 이메일로 유저 생성 시 예외 발생 여부 확인
        UserRequestDto invalidEmailRequestDto = UserRequestDto.builder()
                .email("invalid-email")
                .password("password")
                .owner(true)
                .build();

    }

}
