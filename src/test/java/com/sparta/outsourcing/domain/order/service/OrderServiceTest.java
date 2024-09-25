package com.sparta.outsourcing.domain.order.service;

import com.sparta.outsourcing.domain.user.entity.User;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalTime;
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

    private Shop shopIsNotOpening() {
        Shop shop = mock(Shop.class);

        // 영업시간 설정
        when(shop.getOpentime()).thenReturn(LocalTime.now().plusHours(1));  // 현재 시간 이후로 opentime 설정
        when(shop.getClosetime()).thenReturn(LocalTime.now().plusHours(2)); // 현재 시간 이후로 closetime 설정

        return shop;
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
            AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, "user@example.com");
            OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                    .shopId(shopId)
                    .menuId(1L)
                    .address("address")
                    .phoneNumber("010-1234-5678")
                    .build();

            Shop shop = mock(Shop.class);
            Menu menu = mock(Menu.class);

            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(menuRepository.findByShopIdAndId(shopId, 1L)).thenReturn(Optional.of(menu));

            // 가게 영업 시간 및 최소 주문 금액 설정
            when(shop.getOpentime()).thenReturn(LocalTime.of(0, 0));
            when(shop.getClosetime()).thenReturn(LocalTime.of(23, 59));
            when(shop.getMinOrderAmount()).thenReturn(BigDecimal.valueOf(10000));
            when(menu.getPrice()).thenReturn(BigDecimal.valueOf(12000));

            Order order = new Order(userId, shop, menu, "address", "010-1234-5678");
            when(orderRepository.save(any(Order.class))).thenReturn(order);

            // when
            OrderResponseDto responseDto = orderService.createOrder(orderRequestDto, shopId, userId, authUser);

            // then
            assertNotNull(responseDto);
            assertEquals(order.getId(), responseDto.getOrderId());
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("주문 생성 실패 - 영업 시간이 아님")
        void createOrder_fail_notOpen() {
            // given
            Long shopId = 1L;
            Long userId = 1L;
            AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, "user@example.com");
            OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                    .shopId(shopId)
                    .menuId(1L)
                    .address("address")
                    .phoneNumber("010-1234-5678")
                    .build();

            // 가게가 영업 중이 아닌 상태 설정
            Shop shop = shopIsNotOpening();
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

            Menu menu = mock(Menu.class);
            when(menuRepository.findByShopIdAndId(shopId, 1L)).thenReturn(Optional.of(menu));
            when(menu.getPrice()).thenReturn(BigDecimal.valueOf(15000));
            when(shop.getMinOrderAmount()).thenReturn(BigDecimal.valueOf(10000));

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    orderService.createOrder(orderRequestDto, shopId, userId, authUser)
            );
            assertEquals("가게의 영업 시간이 아닙니다.", exception.getMessage());
        }

        @Test
        @DisplayName("주문 생성 실패 - 사장님 계정으로 주문 불가")
        void createOrder_fail_ownerCannotOrder() {
            // given
            Long shopId = 1L;
            Long userId = 1L;
            AuthUser authUser = new AuthUser(userId, UserRoleEnum.OWNER, "owner@example.com");
            OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                    .shopId(shopId)
                    .menuId(1L)
                    .address("address")
                    .phoneNumber("010-1234-5678")
                    .build();

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
            OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                    .shopId(shopId)
                    .menuId(1L)
                    .address("address")
                    .phoneNumber("010-1234-5678")
                    .build();

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
            OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                    .shopId(shopId)
                    .menuId(1L)
                    .address("address")
                    .phoneNumber("010-1234-5678")
                    .build();

            Shop shop = mock(Shop.class);
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(menuRepository.findByShopIdAndId(shopId, 1L)).thenReturn(Optional.empty());

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
            OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                    .shopId(shopId)
                    .menuId(1L)
                    .address("address")
                    .phoneNumber("010-1234-5678")
                    .build();

            Shop shop = mock(Shop.class);
            Menu menu = mock(Menu.class);
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())).thenReturn(Optional.of(menu));

            when(shop.getOpentime()).thenReturn(LocalTime.of(9, 0));
            when(shop.getClosetime()).thenReturn(LocalTime.of(21, 0));

            // 메뉴 가격이 null인 경우
            when(menu.getPrice()).thenReturn(null);
            when(shop.getMinOrderAmount()).thenReturn(BigDecimal.valueOf(10000));

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
            OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                    .shopId(shopId)
                    .menuId(1L)
                    .address("address")
                    .phoneNumber("010-1234-5678")
                    .build();

            Shop shop = mock(Shop.class);
            Menu menu = mock(Menu.class);

            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())).thenReturn(Optional.of(menu));

            when(shop.getOpentime()).thenReturn(LocalTime.of(0, 0));
            when(shop.getClosetime()).thenReturn(LocalTime.of(23, 59));

            when(shop.getMinOrderAmount()).thenReturn(BigDecimal.valueOf(10000));
            when(menu.getPrice()).thenReturn(BigDecimal.valueOf(5000));

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

            // Mock 객체 반환 값 설정
            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);
            when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when
            OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto("ACCEPTED");
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

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(2L);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto("ACCEPTED");

            // when & then
            assertThrows(SecurityException.class, () ->
                    orderService.updateOrderStatus(orderId, orderStatusRequestDto.getNewStatus(), ownerId)
            );
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

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            when(order.getOrderStatus()).thenReturn(OrderStatus.COMPLETED);

            OrderStatusRequestDto orderStatusRequestDto = new OrderStatusRequestDto("CANCELED");

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                    orderService.updateOrderStatus(orderId, orderStatusRequestDto.getNewStatus(), ownerId)
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

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            when(order.getOrderStatus()).thenReturn(OrderStatus.CANCELED);

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
        @DisplayName("오너가 주문 조회 성공")
        void getShopOrders_success() {
            // given
            Long shopId = 1L;
            Long ownerId = 1L;

            // Mock 객체 생성
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);
            Order order = mock(Order.class);
            Menu menu = mock(Menu.class);

            // Mock 반환 값 설정
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);
            when(order.getShop()).thenReturn(shop);
            when(order.getMenu()).thenReturn(menu);

            // 주문 리스트 반환
            when(orderRepository.findByShopId(shopId)).thenReturn(List.of(order));

            // when
            List<OrderResponseDto> orderResponseDtos = orderService.getShopOrders(shopId, ownerId);

            // then
            assertNotNull(orderResponseDtos);
            assertEquals(1, orderResponseDtos.size());
            verify(orderRepository, times(1)).findByShopId(shopId);
        }

        @Test
        @DisplayName("오너가 아님 - 주문 조회 실패")
        void getShopOrders_fail_notOwner() {
            // given
            Long shopId = 1L;
            Long ownerId = 1L;
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(2L);

            // when & then
            SecurityException exception = assertThrows(SecurityException.class, () ->
                    orderService.getShopOrders(shopId, ownerId)
            );

            assertEquals("가게 소유자가 아닙니다.", exception.getMessage());
        }

        @Test
        @DisplayName("사용자 주문 조회 성공")
        void getUserOrders_success() {
            // given
            Long userId = 1L;

            Order order1 = mock(Order.class);
            Order order2 = mock(Order.class);
            Menu menu = mock(Menu.class);
            Shop shop = mock(Shop.class);

            when(order1.getShop()).thenReturn(shop);
            when(order1.getMenu()).thenReturn(menu);
            when(order2.getShop()).thenReturn(shop);
            when(order2.getMenu()).thenReturn(menu);

            // 주문 리스트 반환
            when(orderRepository.findByUserId(userId)).thenReturn(List.of(order1, order2));

            // when
            List<OrderResponseDto> orderResponseDtos = orderService.getUserOrders(userId);

            // then
            assertNotNull(orderResponseDtos);
            assertEquals(2, orderResponseDtos.size());
            verify(orderRepository, times(1)).findByUserId(userId);
        }
    }
    @Nested@DisplayName("주문 상태 전환 테스트")class OrderStatusTransitionTests {

        @Test
        @DisplayName("잘못된 상태 전환 시 적절한 에러 메시지 반환")
        void testInvalidStatusTransitionMessage() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // 상태가 PENDING인 경우
            when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                orderService.updateOrderStatus(orderId, "COMPLETED", ownerId);
            });

            assertEquals("현재 상태에서는 수락 또는 취소만 가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("수락된 주문은 진행 중으로만 전환 가능")
        void testAcceptedToInProgress() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // 상태가 ACCEPTED인 경우
            when(order.getOrderStatus()).thenReturn(OrderStatus.ACCEPTED);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                orderService.updateOrderStatus(orderId, "COMPLETED", ownerId);
            });

            assertEquals("주문이 수락된 상태입니다. 진행 중으로 변경만 가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("진행 중인 주문은 완료로만 전환 가능")
        void testInProgressToCompleted() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            // Mock 반환 값 설정
            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // 상태가 IN_PROGRESS인 경우
            when(order.getOrderStatus()).thenReturn(OrderStatus.IN_PROGRESS);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                orderService.updateOrderStatus(orderId, "CANCELED", ownerId);
            });

            assertEquals("주문이 진행 중인 상태입니다. 완료만 가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("완료된 주문은 상태를 변경할 수 없음")
        void testCompletedOrder() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            // Mock 반환 값 설정
            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // 상태가 COMPLETED인 경우
            when(order.getOrderStatus()).thenReturn(OrderStatus.COMPLETED);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                orderService.updateOrderStatus(orderId, "PENDING", ownerId);
            });

            assertEquals("이미 완료된 주문의 상태는 변경할 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("취소된 주문은 상태를 변경할 수 없음")
        void testCanceledOrder() {
            // given
            Long orderId = 1L;
            Long ownerId = 1L;

            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);
            User owner = mock(User.class);

            // Mock 반환 값 설정
            when(order.getShop()).thenReturn(shop);
            when(shop.getOwner()).thenReturn(owner);
            when(owner.getId()).thenReturn(ownerId);

            // 상태가 CANCELED인 경우
            when(order.getOrderStatus()).thenReturn(OrderStatus.CANCELED);
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                orderService.updateOrderStatus(orderId, "PENDING", ownerId);
            });

            assertEquals("취소된 주문의 상태는 변경할 수 없습니다.", exception.getMessage());
        }
    }

}
