package com.sparta.outsourcing.domain.order.dto;

import com.sparta.outsourcing.domain.order.entity.Order;
import com.sparta.outsourcing.domain.order.entity.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderResponseDto {
    private Long orderId;
    private Long shopId;
    private Long menuId;
    private String menuName;
    private double menuPrice;
    private String address;
    private String phoneNumber;
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;

    public OrderResponseDto(Order order) {
        this.orderId = order.getId();
        this.shopId = order.getShop().getId();
        this.menuId = order.getMenu().getId();
        this.menuName = order.getMenuName();  // 저장된 메뉴 이름 사용
        this.menuPrice = order.getMenuPrice();  // 저장된 메뉴 가격 사용
        this.address = order.getAddress();
        this.phoneNumber = order.getPhoneNumber();
        this.orderStatus = order.getOrderStatus();
        this.orderTime = order.getCreatedAt();
    }
}
