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
            throw new IllegalArgumentException("사장님 계정으로는 주문을 할 수 없습니다.");
        }

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        if (!isShopOpen(shop)) {
            throw new IllegalArgumentException("가게의 영업 시간이 아닙니다.");
        }

        Menu menu = menuRepository.findByShopIdAndId(shopId, orderRequestDto.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("해당 가게에 메뉴가 존재하지 않습니다."));

        if (menu.getPrice().compareTo(shop.getMinOrderAmount()) < 0) {
            throw new IllegalArgumentException("최소 주문 금액을 만족하지 않습니다.");
        }

        Order order = new Order(userId, shop, menu, orderRequestDto.getAddress(), orderRequestDto.getPhoneNumber());
        orderRepository.save(order);

        return new OrderResponseDto(order);
    }

    //영업 시간 확인
    private boolean isShopOpen(Shop shop) {
        LocalTime now = LocalTime.now();
        LocalTime opentime = shop.getOpentime();
        LocalTime closetime = shop.getClosetime();

        // 오픈 시간이 마감 시간보다 늦는 경우
        if (opentime.isAfter(closetime)) {
            return now.isAfter(opentime) || now.isBefore(closetime);
        } else {
            return now.isAfter(opentime) && now.isBefore(closetime);
        }
    }

}
