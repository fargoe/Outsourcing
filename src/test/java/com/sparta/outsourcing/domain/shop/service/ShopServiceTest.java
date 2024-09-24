package com.sparta.outsourcing.domain.shop.service;

import com.sparta.outsourcing.domain.shop.dto.ShopRequestDto;
import com.sparta.outsourcing.domain.shop.dto.ShopResponseDto;
import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.shop.repository.ShopRepository;
import com.sparta.outsourcing.domain.user.dto.UserRequestDto;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import com.sparta.outsourcing.domain.user.service.UserService;
import com.sparta.outsourcing.global.config.PasswordEncoder;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private Shop shop;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트용 사용자 초기화
        UserRequestDto ownerRequest = new UserRequestDto("owner@email.com");
        ownerUser = new User(ownerRequest, "ownerPassword", UserRoleEnum.OWNER);

        UserRequestDto customerRequest = new UserRequestDto("customer@email.com");
        nonOwnerUser = new User(customerRequest, "customerPassword", UserRoleEnum.USER);

        // builder 패턴을 사용한 가게 초기화
        shop = Shop.builder()
                .owner(ownerUser)
                .name("테스트 가게")
                .opentime(LocalTime.of(9, 0))
                .closetime(LocalTime.of(21, 0))
                .minOrderAmount(new BigDecimal("10000"))
                .closed(false)
                .build();

        // 테스트용 ShopRequestDto 초기화
        shopRequest = new ShopRequestDto("테스트 가게", LocalTime.of(10, 0), LocalTime.of(22, 0), new BigDecimal("12000"));
    }

    @Test
    @DisplayName("사장님 유저가 아닐 때 가게 생성 실패")
    void testCreateShop_NotOwner() {
        // given
        User authUser = nonOwnerUser;

        // when & then
        assertThrows(SecurityException.class, () -> {
            shopService.createShop(shopRequest, authUser);
        });
    }

    @Test
    @DisplayName("사장님이 3개 이상 가게를 운영할 떄 가게 생성 실패")
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
    @DisplayName("가게 생성 성공")
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
        assertEquals(LocalTime.of(10, 0), response.getOpentime());
        assertEquals(LocalTime.of(22, 0), response.getClosetime());
        assertEquals(new BigDecimal("12000"), response.getMinOrderAmount());
    }

    @Mock
    private PasswordEncoder passwordEncoder; // PasswordEncoder 모킹

    @Test
    @DisplayName("비밀번호 불일치 실패")
    void testCreateShop_InvalidPassword() {
        // given
        User authUser = ownerUser;
        String wrongPassword = "wrongPassword";

        // 비밀번호가 일치하지 않는 경우를 모킹
        when(passwordEncoder.matches(wrongPassword, authUser.getPassword())).thenReturn(false);

        // when & then
        assertThrows(SecurityException.class, () -> {
            shopService.createShop(shopRequest, authUser);
        });
    }

    @Test
    @DisplayName("가게가 존재하지 않는 경우")
    void testUpdateShop_ShopNotFound() {
        // given
        when(shopRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then: 가게를 찾을 수 없다는 예외 발생
        assertThrows(EntityNotFoundException.class, () -> {
            shopService.updateShop(1L, shopRequest, ownerUser);
        });
    }

    @Test
    @DisplayName("가게 이름으로 검색했을 때 결과가 없는 경우")
    void testGetShopsByName_NoShopsFound() {
        // given
        String shopName = "없는 가게";
        when(shopRepository.findByNameContaining(shopName)).thenReturn(new ArrayList<>());

        // when: 가게를 검색
        List<ShopResponseDto> response = shopService.getShopsByName(shopName);

        // then: 빈 리스트가 반환되는지 확인
        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("가게 이름으로 검색했을 때 결과가 있는 경우")
    void testGetShopsByName_ShopsFound() {
        // given
        List<Shop> shops = List.of(
                Shop.builder()
                        .id(1L)
                        .name("테스트 가게1")
                        .opentime(LocalTime.of(9, 0))
                        .closetime(LocalTime.of(21, 0))
                        .minOrderAmount(new BigDecimal("10000"))
                        .menus(null) // 메뉴 리스트가 없을 경우 null로 설정
                        .closed(false)
                        .build(),
                Shop.builder()
                        .id(2L)
                        .name("테스트 가게2")
                        .opentime(LocalTime.of(10, 0))
                        .closetime(LocalTime.of(22, 0))
                        .minOrderAmount(new BigDecimal("15000"))
                        .menus(null) // 메뉴 리스트가 없을 경우 null로 설정
                        .closed(false)
                        .build()
        );
        String shopName = "테스트";
        when(shopRepository.findByNameContaining(shopName)).thenReturn(shops);

        // when: 가게를 검색
        List<ShopResponseDto> response = shopService.getShopsByName(shopName);

        // then: 올바른 가게 정보가 반환되는지 확인
        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getId());
        assertEquals("테스트 가게1", response.get(0).getName());
        assertEquals(LocalTime.of(9, 0), response.get(0).getOpentime());
        assertEquals(LocalTime.of(21, 0), response.get(0).getClosetime());
        assertEquals(new BigDecimal("10000"), response.get(0).getMinOrderAmount());

        assertEquals(2L, response.get(1).getId());
        assertEquals("테스트 가게2", response.get(1).getName());
        assertEquals(LocalTime.of(10, 0), response.get(1).getOpentime());
        assertEquals(LocalTime.of(22, 0), response.get(1).getClosetime());
        assertEquals(new BigDecimal("15000"), response.get(1).getMinOrderAmount());
    }

    @Test
    @DisplayName("단건 조회 시 가게가 존재하지 않는 경우")
    void testGetShopById_NotFound() {
        // given
        Long shopId = 1L;
        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

        // when & then: 가게를 찾을 수 없는 경우
        assertThrows(EntityNotFoundException.class, () -> {
            shopService.getShopById(shopId);
        });
    }
}