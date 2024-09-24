package com.sparta.outsourcing.domain.order.service;

import com.sparta.outsourcing.domain.menu.entity.Menu;
import com.sparta.outsourcing.domain.menu.repository.MenuRepository;
import com.sparta.outsourcing.domain.order.dto.OrderRequestDto;
import com.sparta.outsourcing.domain.order.dto.OrderResponseDto;
import com.sparta.outsourcing.domain.order.dto.OrderStatusRequestDto;
import com.sparta.outsourcing.domain.order.entity.Order;
import com.sparta.outsourcing.domain.order.entity.OrderStatus;
import com.sparta.outsourcing.domain.order.repository.OrderRepository;
import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.shop.repository.ShopRepository;
import com.sparta.outsourcing.domain.user.dto.AuthUser;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("주문 생성 테스트")
    class CreateOrderTests {

        @Test
        @DisplayName("주문 생성 성공")
        void createOrder_success() {
            // given
            Long shopId = 1L;
            Long userId = 1L;
            String email = "user@example.com";
            AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, email);
            OrderRequestDto orderRequestDto = new OrderRequestDto(shopId, 1L, "address", "phone");

            Shop shop = mock(Shop.class);
            Menu menu = mock(Menu.class);
            when(shop.getOpentime()).thenReturn(LocalTime.of(0, 0));
            when(shop.getClosetime()).thenReturn(LocalTime.of(23, 59));
            when(menu.getPrice()).thenReturn(new BigDecimal("10000.0"));
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())).thenReturn(Optional.of(menu));
            when(shop.getMinOrderAmount()).thenReturn(new BigDecimal("10000.0"));

            Order order = new Order(userId, shop, menu, orderRequestDto.getAddress(), orderRequestDto.getPhoneNumber());
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // when
            OrderResponseDto responseDto = orderService.createOrder(orderRequestDto, shopId, userId, authUser);

            // then
            assertNotNull(responseDto);
            assertEquals(order.getId(), responseDto.getOrderId());
            verify(orderRepository, times(1)).save(any(Order.class));
        }

//        @Test
//        @DisplayName("주문 생성 실패 - 영업 시간이 아님")
//        void createOrder_fail_notOpen() {
//            // given
//            Long shopId = 1L;
//            Long userId = 1L;
//            AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, "user@example.com");
//            OrderRequestDto orderRequestDto = new OrderRequestDto(shopId, 1L, "address", "phone");
//
//            Shop shop = mock(Shop.class);
//            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
//
//            // 영업 시간이 아닌 경우 설정
//            when(shop.getOpentime()).thenReturn(LocalTime.of(9, 0));
//            when(shop.getClosetime()).thenReturn(LocalTime.of(17, 0));
//
//            // LocalTime.now()를 모킹
//            try (MockedStatic<LocalTime> mockedLocalTime = mockStatic(LocalTime.class)) {
//                mockedLocalTime.when(LocalTime::now).thenReturn(LocalTime.of(8, 0)); // 영업 시간 외
//
//                Menu menu = mock(Menu.class);
//                when(menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())).thenReturn(Optional.of(menu));
//                when(menu.getPrice()).thenReturn(new BigDecimal("15000.0"));
//                when(shop.getMinOrderAmount()).thenReturn(new BigDecimal("10000.0"));
//
//                // when & then
//                IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
//                        orderService.createOrder(orderRequestDto, shopId, userId, authUser)
//                );
//                assertEquals("가게의 영업 시간이 아닙니다.", exception.getMessage());
//            }
//        }
//
//        @Test
//        @DisplayName("주문 생성 성공 - 영업 시간 내")
//        void createOrder_success_openShop() {
//            // given
//            Long shopId = 1L;
//            Long userId = 1L;
//            String email = "user@example.com";
//            AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, email);
//            OrderRequestDto orderRequestDto = new OrderRequestDto(shopId, 1L, "address", "phone");
//
//            Shop shop = mock(Shop.class);
//            Menu menu = mock(Menu.class);
//
//            // Mock repository returns
//            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
//            when(menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())).thenReturn(Optional.of(menu));
//
//            // 설정: 가게의 영업 시간과 최소 주문 금액
//            when(shop.getOpentime()).thenReturn(LocalTime.of(0, 0));
//            when(shop.getClosetime()).thenReturn(LocalTime.of(23, 59));
//            when(menu.getPrice()).thenReturn(new BigDecimal("10000.0"));
//            when(shop.getMinOrderAmount()).thenReturn(new BigDecimal("10000.0"));
//
//            // 시간 모킹: 현재 시간을 영업 시간 내로 설정
//            try (MockedStatic<LocalTime> mockedLocalTime = mockStatic(LocalTime.class)) {
//                mockedLocalTime.when(LocalTime::now).thenReturn(LocalTime.of(10, 0)); // 영업 시간 내
//
//                // Order mock
//                Order order = new Order(userId, shop, menu, orderRequestDto.getAddress(), orderRequestDto.getPhoneNumber());
//                when(orderRepository.save(any(Order.class))).thenReturn(order);
//
//                // when
//                OrderResponseDto responseDto = orderService.createOrder(orderRequestDto, shopId, userId, authUser);
//
//                // then
//                assertNotNull(responseDto);
//                assertEquals(order.getId(), responseDto.getOrderId());
//                verify(orderRepository, times(1)).save(any(Order.class));
//            }
//        }

        @Test
        @DisplayName("주문 생성 실패 - 사장님 계정으로 주문 불가")
        void createOrder_fail_ownerCannotOrder() {
            // given
            Long shopId = 1L;
            Long userId = 1L;
            AuthUser authUser = new AuthUser(userId, UserRoleEnum.OWNER, "owner@example.com");
            OrderRequestDto orderRequestDto = new OrderRequestDto(shopId, 1L, "address", "phone");

            // when & then
            assertThrows(SecurityException.class, () ->
                    orderService.createOrder(orderRequestDto, shopId, userId, authUser)
            );
        }

        @Test
        @DisplayName("주문 생성 실패 - 가게 없음")
        void createOrder_fail_shopNotFound() {
            // given
            Long shopId = 1L;
            Long userId = 1L;
            AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, "user@example.com");
            OrderRequestDto orderRequestDto = new OrderRequestDto(shopId, 1L, "address", "phone");

            when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(EntityNotFoundException.class, () ->
                    orderService.createOrder(orderRequestDto, shopId, userId, authUser)
            );
        }

        @Test
        @DisplayName("주문 생성 실패 - 메뉴 없음")
        void createOrder_fail_menuNotFound() {
            // given
            Long shopId = 1L;
            Long userId = 1L;
            AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, "user@example.com");
            OrderRequestDto orderRequestDto = new OrderRequestDto(shopId, 1L, "address", "phone");

            Shop shop = mock(Shop.class);
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

            when(shop.getOpentime()).thenReturn(LocalTime.of(0, 0));
            when(shop.getClosetime()).thenReturn(LocalTime.of(23, 59));

            when(menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())).thenReturn(Optional.empty());

            // when & then
            assertThrows(EntityNotFoundException.class, () ->
                    orderService.createOrder(orderRequestDto, shopId, userId, authUser)
            );
        }

        @Test
        @DisplayName("주문 생성 실패 - 메뉴 가격 또는 최소 주문 금액이 null")
        void createOrder_fail_nullPriceOrMinOrderAmount() {
            // given
            Long shopId = 1L;
            Long userId = 1L;
            AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, "user@example.com");
            OrderRequestDto orderRequestDto = new OrderRequestDto(shopId, 1L, "address", "phone");

            Shop shop = mock(Shop.class);
            Menu menu = mock(Menu.class);
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())).thenReturn(Optional.of(menu));

            when(shop.getOpentime()).thenReturn(LocalTime.of(9, 0));
            when(shop.getClosetime()).thenReturn(LocalTime.of(21, 0));

            // 메뉴 가격이 null인 경우
            when(menu.getPrice()).thenReturn(null);
            when(shop.getMinOrderAmount()).thenReturn(new BigDecimal("10000.0"));

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    orderService.createOrder(orderRequestDto, shopId, userId, authUser)
            );
            assertEquals("메뉴 가격 또는 최소 주문 금액이 잘못 설정되었습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("주문 생성 실패 - 최소 주문 금액 미만")
        void createOrder_fail_minOrderAmountNotMet() {
            // given
            Long shopId = 1L;
            Long userId = 1L;
            AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, "user@example.com");
            OrderRequestDto orderRequestDto = new OrderRequestDto(shopId, 1L, "address", "phone");

            Shop shop = mock(Shop.class);
            Menu menu = mock(Menu.class);

            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())).thenReturn(Optional.of(menu));

            when(shop.getOpentime()).thenReturn(LocalTime.of(0, 0));
            when(shop.getClosetime()).thenReturn(LocalTime.of(23, 59));

            when(shop.getMinOrderAmount()).thenReturn(new BigDecimal("10000.0"));
            when(menu.getPrice()).thenReturn(new BigDecimal("5000.0"));

            // when & then
            assertThrows(IllegalStateException.class, () ->
                    orderService.createOrder(orderRequestDto, shopId, userId, authUser)
            );
        }
    }

    @Nested
    @DisplayName("주문 상태 변경 테스트")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("주문 상태 변경 성공")
        void updateOrderStatus_success() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto("ACCEPTED");

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);

            // when
            String response = orderService.updateOrderStatus(orderId, orderStatusRequestDto.getNewStatus(), ownerId);

            // then
            assertEquals("주문 상태가 성공적으로 변경되었습니다.", response);
            verify(orderRepository, times(1)).save(order);
        }

        @Test
        @DisplayName("주문 상태 변경 실패 - 권한 없음")
        void updateOrderStatus_fail_noPermission() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;
            OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto("ACCEPTED");

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);
            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(2L);

            // when & then
            assertThrows(SecurityException.class, () ->
                    orderService.updateOrderStatus(orderId, orderStatusRequestDto.getNewStatus(), ownerId)
            );
        }

        @Test
        @DisplayName("주문 상태 변경 실패 - 상태 전환 불가")
        void updateOrderStatus_fail_invalidStatusTransition() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // 현재 주문 상태가 COMPLETED인 경우
            when(order.getOrderStatus()).thenReturn(OrderStatus.COMPLETED);

            OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto("CANCELED");

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    orderService.updateOrderStatus(orderId, orderStatusRequestDto.getNewStatus(), ownerId)
            );

            // 예외 메시지 검증 추가
            assertEquals("이미 완료된 주문의 상태는 변경할 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("주문 상태 변경 실패 - 완료된 주문은 상태 전환 불가")
        void updateOrderStatus_fail_completedOrder() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // 주문 상태가 COMPLETED인 경우
            when(order.getOrderStatus()).thenReturn(OrderStatus.COMPLETED);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    orderService.updateOrderStatus(orderId, "CANCELED", ownerId)
            );
            assertEquals("이미 완료된 주문의 상태는 변경할 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("주문 상태 변경 실패 - 취소된 주문은 상태 전환 불가")
        void updateOrderStatus_fail_canceledOrder() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // 주문 상태가 CANCELED인 경우
            when(order.getOrderStatus()).thenReturn(OrderStatus.CANCELED);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    orderService.updateOrderStatus(orderId, "PENDING", ownerId)
            );
            assertEquals("취소된 주문의 상태는 변경할 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("잘못된 상태 전환 시 IllegalArgumentException 발생")
        void testInvalidStatusTransition() {
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            assertThrows(IllegalArgumentException.class, () -> {
                orderService.updateOrderStatus(orderId, "COMPLETED", ownerId);
            });
        }

        @Test
        @DisplayName("주문 상태 변경 실패 - 가게를 찾을 수 없음")
        void updateOrderStatus_fail_shopNotFound() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // when & then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    orderService.updateOrderStatus(orderId, "ACCEPTED", ownerId)
            );

            assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
        }
    }


    @Nested
    @DisplayName("주문 조회 테스트")
    class GetOrderTests {

        @Test
        @DisplayName("오너 주문 조회 성공")
        void getShopOrders_success() {
            // given
            Long shopId = 1L;
            Long ownerId = 1L;

            Shop shop = mock(Shop.class);
            User owner = mock(User.class);
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            Order order = mock(Order.class);
            Menu menu = mock(Menu.class);
            when(order.getShop()).thenReturn(shop);
            when(order.getMenu()).thenReturn(menu);
            when(menu.getId()).thenReturn(1L);
            when(orderRepository.findByShopId(shopId)).thenReturn(List.of(order));

            // when
            List<OrderResponseDto> orders = orderService.getShopOrders(shopId, ownerId);

            // then
            assertNotNull(orders);
            assertEquals(1, orders.size());
            verify(orderRepository, times(1)).findByShopId(shopId);
        }

        @Test
        @DisplayName("사용자 주문 조회 성공")
        void getUserOrders_success() {
            // given
            Long userId = 1L;

            Order order1 = mock(Order.class);
            Order order2 = mock(Order.class);

            Shop shop = mock(Shop.class);
            Menu menu = mock(Menu.class);

            when(order1.getShop()).thenReturn(shop);
            when(order2.getShop()).thenReturn(shop);
            when(order1.getMenu()).thenReturn(menu);
            when(order2.getMenu()).thenReturn(menu);
            when(menu.getId()).thenReturn(1L);

            when(orderRepository.findByUserId(userId)).thenReturn(List.of(order1, order2));

            // when
            List<OrderResponseDto> orders = orderService.getUserOrders(userId);

            // then
            assertNotNull(orders);
            assertEquals(2, orders.size());
            verify(orderRepository, times(1)).findByUserId(userId);
        }

        @Test
        @DisplayName("오너 주문 조회 실패 - 주문이 없는 경우")
        void getShopOrders_noOrders() {
            // given
            Long shopId = 1L;
            Long ownerId = 1L;

            Shop shop = mock(Shop.class);
            User owner = mock(User.class);
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // 주문이 없는 경우 빈 리스트 반환
            when(orderRepository.findByShopId(shopId)).thenReturn(Collections.emptyList());

            // when
            List<OrderResponseDto> orders = orderService.getShopOrders(shopId, ownerId);

            // then
            assertNotNull(orders);
            assertTrue(orders.isEmpty());
            verify(orderRepository, times(1)).findByShopId(shopId);
        }

        @Test
        @DisplayName("사용자 주문 조회 실패 - 주문이 없는 경우")
        void getUserOrders_noOrders() {
            // given
            Long userId = 1L;

            when(orderRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

            // when
            List<OrderResponseDto> orders = orderService.getUserOrders(userId);

            // then
            assertNotNull(orders);
            assertTrue(orders.isEmpty());
            verify(orderRepository, times(1)).findByUserId(userId);
        }


        @Test
        @DisplayName("주문 조회 실패 - 가게를 찾을 수 없음")
        void getShopOrders_fail_shopNotFound() {
            // given
            Long shopId = 1L;
            Long ownerId = 1L;

            when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

            // when & then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                    orderService.getShopOrders(shopId, ownerId)
            );
            assertEquals("가게를 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("주문 조회 실패 - 소유자가 아님")
        void getShopOrders_fail_notOwner() {
            // given
            Long shopId = 1L;
            Long ownerId = 1L;

            Shop shop = mock(Shop.class);
            User owner = mock(User.class);
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(2L); // 다른 소유자

            // when & then
            SecurityException exception = assertThrows(SecurityException.class, () ->
                    orderService.getShopOrders(shopId, ownerId)
            );
            assertEquals("가게 소유자가 아닙니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("주문 상태 전환 불가 메시지 테스트")
    class InvalidStatusTransitionMessageTests {

        @Test
        @DisplayName("상태 전환 불가 메시지 - PENDING에서 수락 또는 취소로만 전환 가능")
        void testPendingToInvalidStatus() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    orderService.updateOrderStatus(orderId, "COMPLETED", ownerId)
            );
            assertEquals("현재 상태에서는 수락 또는 취소만 가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("상태 전환 불가 메시지 - ACCEPTED에서 진행 중으로만 전환 가능")
        void testAcceptedToInvalidStatus() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            when(order.getOrderStatus()).thenReturn(OrderStatus.ACCEPTED);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    orderService.updateOrderStatus(orderId, "COMPLETED", ownerId)
            );
            assertEquals("주문이 수락된 상태입니다. 진행 중으로 변경만 가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("상태 전환 불가 메시지 - IN_PROGRESS에서 완료로만 전환 가능")
        void testInProgressToInvalidStatus() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            when(order.getOrderStatus()).thenReturn(OrderStatus.IN_PROGRESS);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    orderService.updateOrderStatus(orderId, "CANCELED", ownerId)
            );
            assertEquals("주문이 진행 중인 상태입니다. 완료만 가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("상태 전환 불가 메시지 - COMPLETED 상태는 변경 불가")
        void testCompletedToInvalidStatus() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            when(order.getOrderStatus()).thenReturn(OrderStatus.COMPLETED);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    orderService.updateOrderStatus(orderId, "CANCELED", ownerId)
            );
            assertEquals("이미 완료된 주문의 상태는 변경할 수 없습니다.", exception.getMessage());
        }
        @Test
        @DisplayName("상태 전환 불가 메시지 - CANCELED 상태는 변경 불가")
        void testCanceledToInvalidStatus() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            when(order.getOrderStatus()).thenReturn(OrderStatus.CANCELED);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    orderService.updateOrderStatus(orderId, "PENDING", ownerId)
            );
            assertEquals("취소된 주문의 상태는 변경할 수 없습니다.", exception.getMessage());
        }
    }
}
