package com.sparta.outsourcing.domain.order.service;

import com.sparta.outsourcing.domain.menu.entity.Menu;
import com.sparta.outsourcing.domain.menu.repository.MenuRepository;
import com.sparta.outsourcing.domain.order.dto.OrderRequestDto;
import com.sparta.outsourcing.domain.order.dto.OrderResponseDto;
import com.sparta.outsourcing.domain.order.entity.Order;
import com.sparta.outsourcing.domain.order.entity.OrderStatus;
import com.sparta.outsourcing.domain.order.repository.OrderRepository;
import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.shop.repository.ShopRepository;
import com.sparta.outsourcing.domain.user.dto.AuthUser;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ShopRepository shopRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;

    //주문생성
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto, Long shopId, Long userId, AuthUser authUser) {
        if (authUser.getRole() == UserRoleEnum.OWNER) {
            throw new SecurityException("사장님 계정으로는 주문을 할 수 없습니다.");
        }

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));

        Menu menu = menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())
                .orElseThrow(() -> new EntityNotFoundException("해당 가게에 메뉴가 존재하지 않습니다."));

        if (menu.getPrice() == null || shop.getMinOrderAmount() == null) {
            throw new IllegalStateException("메뉴 가격 또는 최소 주문 금액이 잘못 설정되었습니다.");
        }

        if (!isShopOpen(shop)) {
            throw new IllegalStateException("가게의 영업 시간이 아닙니다.");
        }

        if (menu.getPrice().compareTo(shop.getMinOrderAmount()) < 0) {
            throw new IllegalStateException("최소 주문 금액을 만족하지 않습니다.");
        }

        Order order = new Order(userId, shop, menu, orderRequestDto.getAddress(), orderRequestDto.getPhoneNumber());
        orderRepository.save(order);

        return new OrderResponseDto(order);
    }

    //주문 조회(Owner)
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getShopOrders(Long shopId, Long ownerId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));

        if (!shop.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("가게 소유자가 아닙니다.");
        }
        return orderRepository.findByShopId(shopId)
                .stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    //주문 조회(User)
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(OrderResponseDto::new)
                .collect(Collectors.toList());
    }

    //주문 상태 변경
    @Transactional
    public String updateOrderStatus(Long orderId, String newStatus, Long ownerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        Shop shop = order.getShop();
        if (!shop.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("해당 주문을 수정할 권한이 없습니다.");
        }

        // 현재 주문 상태를 가져오고, 새로 변경할 상태를 파라미터로 받음
        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newOrderStatus = OrderStatus.valueOf(newStatus.toUpperCase());

        if (currentStatus == OrderStatus.COMPLETED) {
            throw new IllegalStateException("이미 완료된 주문의 상태는 변경할 수 없습니다.");
        } else if (currentStatus == OrderStatus.CANCELED) {
            // 상태가 CANCELED일 경우 IllegalArgumentException 던짐
            throw new IllegalStateException("취소된 주문의 상태는 변경할 수 없습니다.");
        }

        // 상태 전환이 유효한지 확인
        if (!isValidStatusTransition(currentStatus, newOrderStatus)) {
            throw new IllegalArgumentException(getInvalidStatusTransitionMessage(currentStatus, newOrderStatus));
        }

        order.changeOrderStatus(newOrderStatus);
        orderRepository.save(order);

        return "주문 상태가 성공적으로 변경되었습니다.";
    }

    // 영업시간 확인 메서드
    private boolean isShopOpen(Shop shop) {
        LocalTime now = LocalTime.now();
        LocalTime openTime = shop.getOpentime();
        LocalTime closeTime = shop.getClosetime();

        // 영업 시간이 마감 시간보다 늦게 설정된 경우
        if (openTime.isAfter(closeTime)) {
            return now.isAfter(openTime) || now.isBefore(closeTime);
        } else {
            return now.isAfter(openTime) && now.isBefore(closeTime);
        }
    }

    private String getInvalidStatusTransitionMessage(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                return "현재 상태에서는 수락 또는 취소만 가능합니다.";
            case ACCEPTED:
                return "주문이 수락된 상태입니다. 진행 중으로 변경만 가능합니다.";
            case IN_PROGRESS:
                return "주문이 진행 중인 상태입니다. 완료만 가능합니다.";
            case COMPLETED:
                return "이미 완료된 주문의 상태는 변경할 수 없습니다.";
            case CANCELED:
                return "취소된 주문의 상태는 변경할 수 없습니다.";
            default:
                return "주문 상태를 이전 상태로 변경할 수 없습니다.";
        }
    }

    // 상태 전환 유효 체크
    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        // 완료 또는 취소된 주문은 상태 전환이 불가능함
        if (currentStatus == OrderStatus.COMPLETED || currentStatus == OrderStatus.CANCELED) {
            return false;
        }
        switch (currentStatus) {
            case PENDING:
                // 대기 중인 주문은 수락으로만 전환 가능
                return newStatus == OrderStatus.ACCEPTED;
            case ACCEPTED:
                // 수락된 주문은 진행 중으로만 전환 가능 (취소 불가)
                return newStatus == OrderStatus.IN_PROGRESS;
            case IN_PROGRESS:
                // 진행 중인 주문은 완료로만 전환 가능 (취소 불가)
                return newStatus == OrderStatus.COMPLETED;
            default:
                throw new IllegalArgumentException("Unexpected OrderStatus: " + currentStatus);
        }
    }
}
