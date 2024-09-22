package com.sparta.outsourcing.domain.order.controller;

import com.sparta.outsourcing.domain.order.dto.OrderRequestDto;
import com.sparta.outsourcing.domain.order.dto.OrderResponseDto;
import com.sparta.outsourcing.domain.order.dto.OrderStatusRequestDto;
import com.sparta.outsourcing.domain.order.service.OrderService;
import com.sparta.outsourcing.domain.user.dto.AuthUser;
import com.sparta.outsourcing.global.annotation.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping("/shops/{shopId}/orders")
    public ResponseEntity<OrderResponseDto> createOrder(@PathVariable Long shopId,
                                                        @RequestBody OrderRequestDto orderRequestDto,
                                                        @Auth AuthUser authUser) {
        Long userId = authUser.getId();
        return ResponseEntity.ok(orderService.createOrder(orderRequestDto, shopId, userId, authUser));
    }

    // 주문 조회 (Owner)
    @GetMapping("/shops/{shopId}/orders")
    public ResponseEntity<List<OrderResponseDto>> getShopOrders(@PathVariable Long shopId,
                                                                @Auth AuthUser authUser) {
        Long ownerId = authUser.getId();
        return ResponseEntity.ok(orderService.getShopOrders(shopId, ownerId));
    }

    // 주문 조회 (User)
    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponseDto>> getUserOrders(@Auth AuthUser authUser) {
        Long userId = authUser.getId();
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    // 주문 상태 변경
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId,
                                                    @RequestBody OrderStatusRequestDto orderStatusRequestDto,
                                                    @Auth AuthUser authUser) {
        Long ownerId = authUser.getId();
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, orderStatusRequestDto.getNewStatus(), ownerId));
    }
}
