package com.sparta.outsourcing.domain.order.entity;

import com.sparta.outsourcing.domain.menu.entity.Menu;
import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.user.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private String address;
    private String phoneNumber;

    private String menuName;  // 주문 당시 메뉴 이름 저장
    private double menuPrice;  // 주문 당시 메뉴 가격 저장

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    public Order(Long userId, Shop shop, Menu menu, String address, String phoneNumber) {
        this.userId = userId;
        this.shop = shop;
        this.menu = menu;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.menuName = menu.getMenuName();  // 주문 당시 메뉴 이름 저장
        this.menuPrice = menu.getPrice().doubleValue();  // 주문 당시 메뉴 가격 저장
    }

    public void changeOrderStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
    }
}
