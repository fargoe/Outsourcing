package com.sparta.outsourcing.domain.shop.service;

import com.sparta.outsourcing.domain.shop.dto.ShopRequestDto;
import com.sparta.outsourcing.domain.shop.dto.ShopResponseDto;
import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.shop.repository.ShopRepository;
import com.sparta.outsourcing.domain.user.dto.UserRequestDto;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import com.sparta.outsourcing.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShopServiceTest {
    @InjectMocks
    private ShopService shopService;

    @Mock
    private UserService userService;

    @Mock
    private ShopRepository shopRepository;

    private User ownerUser;
    private User nonOwnerUser;
    private ShopRequestDto shopRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트용 사용자 요청 DTO 초기화
//        UserRequestDto ownerRequest = new UserRequestDto("owner@example.com"); // 이메일만 포함된 DTO 예시
//        UserRequestDto customerRequest = new UserRequestDto("customer@example.com");
//
//        String ownerPassword = "ownerPassword"; // 비밀번호
//        String customerPassword = "customerPassword"; // 비밀번호
//
//        // 테스트용 사용자 초기화
//        ownerUser = new User(ownerRequest, ownerPassword, UserRoleEnum.OWNER); // OWNER 역할의 사용자
//        nonOwnerUser = new User(customerRequest, customerPassword, UserRoleEnum.USER); // USER 역할의 사용자

        //shopRequest = new ShopRequestDto("테스트 가게", LocalTime.of(9, 0), LocalTime.of(21, 0), new BigDecimal("10000")); // 생성자를 사용
    }

    @Test
    void testCreateShop_NotOwner() {
        // given
        User authUser = nonOwnerUser;

        // when & then
        assertThrows(SecurityException.class, () -> {
            shopService.createShop(shopRequest, authUser);
        });
    }

    @Test
    void testCreateShop_TooManyShops() {
        // given
        User authUser = ownerUser;
        when(userService.hasRole(authUser, UserRoleEnum.OWNER)).thenReturn(true);
        when(shopRepository.findByOwnerAndClosedFalse(authUser)).thenReturn(new ArrayList<>(List.of(new Shop(), new Shop(), new Shop())));

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            shopService.createShop(shopRequest, authUser);
        });
    }

    @Test
    void testCreateShop_Success() {
        // given
        User authUser = ownerUser;
        when(userService.hasRole(authUser, UserRoleEnum.OWNER)).thenReturn(true);
        when(shopRepository.findByOwnerAndClosedFalse(authUser)).thenReturn(new ArrayList<>()); // 가게가 없음
        when(shopRepository.save(any(Shop.class))).thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 Shop 반환

        // when
        ShopResponseDto response = shopService.createShop(shopRequest, authUser);

        // then
        assertNotNull(response);
        assertEquals("테스트 가게", response.getName());
        assertEquals(LocalTime.of(9, 0), response.getOpentime());
        assertEquals(LocalTime.of(21, 0), response.getClosetime());
        assertEquals(new BigDecimal("10000"), response.getMinOrderAmount());
    }
}
