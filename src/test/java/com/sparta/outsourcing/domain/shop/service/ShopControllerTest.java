package com.sparta.outsourcing.domain.shop.service;

import com.sparta.outsourcing.domain.shop.controller.ShopController;
import com.sparta.outsourcing.domain.shop.dto.ShopRequestDto;
import com.sparta.outsourcing.domain.shop.dto.ShopResponseDto;
import com.sparta.outsourcing.domain.user.dto.AuthUser;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShopControllerTest {
    @InjectMocks
    private ShopController shopController;

    @Mock
    private ShopService shopService;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    private AuthUser authUser;
    private ShopRequestDto shopRequest;
    private User user;
    private ShopResponseDto shopResponseDto;
    private Long shopId;

    @BeforeEach
    void setUp() {
        authUser = AuthUser.builder()
                .id(1L) // ID 설정
                .build();

        shopId = 1L;

        shopRequest = ShopRequestDto.builder()
                .name("Test Shop") // 적절한 샵 이름 설정
                .opentime(LocalTime.of(9, 0)) // 오픈 시간 설정
                .closetime(LocalTime.of(21, 0)) // 마감 시간 설정
                .minOrderAmount(BigDecimal.valueOf(10000)) // 최소 주문 금액 설정
                .build();

        user = User.builder()
                .id(1L) // ID 설정
                .build();

        shopResponseDto = ShopResponseDto.builder()
                .id(1L) // 샵 ID 설정
                .name("Test Shop") // 샵 이름 설정
                .opentime(LocalTime.of(9, 0)) // 오픈 시간 설정
                .closetime(LocalTime.of(21, 0)) // 마감 시간 설정
                .minOrderAmount(BigDecimal.valueOf(10000)) // 최소 주문 금액 설정
                .build();
    }

    @Test
    @DisplayName("가게 생성 성공")
    void testCreateShop_Success() {
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(shopService.createShop(shopRequest, user)).thenReturn(shopResponseDto);

        ResponseEntity<ShopResponseDto> response = shopController.createShop(shopRequest, authUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(shopResponseDto, response.getBody());
        verify(userRepository).findById(authUser.getId());
        verify(shopService).createShop(shopRequest, user);
    }

    @Test
    @DisplayName("가게 생성 시 사용자를 찾을 수 없는 경우")
    void testCreateShop_UserNotFound() {
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            shopController.createShop(shopRequest, authUser);
        });

        verify(userRepository).findById(authUser.getId());
        verify(shopService, never()).createShop(any(), any());
    }

    @Test
    @DisplayName("가게 수정 성공")
    void testUpdateShop_Success() {
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
        when(shopService.updateShop(shopId, shopRequest, user)).thenReturn(shopResponseDto);

        ResponseEntity<ShopResponseDto> response = shopController.updateShop(shopId, shopRequest, authUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shopResponseDto, response.getBody());
        verify(userRepository).findById(authUser.getId());
        verify(shopService).updateShop(shopId, shopRequest, user);
    }

    @Test
    @DisplayName("가게 수정 시 사용자 찾을 수 없는 경우")
    void testUpdateShop_UserNotFound() {
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            shopController.updateShop(shopId, shopRequest, authUser);
        });

        verify(userRepository).findById(authUser.getId());
        verify(shopService, never()).updateShop(anyLong(), any(), any()); // shopService는 호출되지 않아야 함
    }

    @Test
    @DisplayName("가게 전체 조회 성공")
    void testGetShopsByName_Success() {
        List<ShopResponseDto> shops = Collections.singletonList(shopResponseDto);
        when(shopService.getShopsByName("Test Shop")).thenReturn(shops);

        ResponseEntity<List<ShopResponseDto>> response = shopController.getShopsByName("Test Shop");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shops, response.getBody());
        verify(shopService).getShopsByName("Test Shop");
    }

    @Test
    @DisplayName("가게 단건 조회 성공")
    void testGetShopById_Success() {
        when(shopService.getShopById(shopId)).thenReturn(shopResponseDto);

        ResponseEntity<ShopResponseDto> response = shopController.getShopById(shopId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shopResponseDto, response.getBody());
        verify(shopService).getShopById(shopId);
    }

    @Test
    @DisplayName("가게 폐업 성공")
    void testCloseShop_Success() {
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));

        ResponseEntity<Void> response = shopController.closeShop(shopId, authUser);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userRepository).findById(authUser.getId());
        verify(shopService).closeShop(shopId, user);
    }

    @Test
    @DisplayName("가게 폐업 시 사용자 찾을 수 없는 경우")
    void testCloseShop_UserNotFound() {
        when(userRepository.findById(authUser.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            shopController.closeShop(shopId, authUser);
        });

        verify(userRepository).findById(authUser.getId());
        verify(shopService, never()).closeShop(anyLong(), any()); // shopService는 호출되지 않아야 함
    }
}
