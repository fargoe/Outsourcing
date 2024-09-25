package com.sparta.outsourcing.domain.order.repository;

import com.sparta.outsourcing.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByShopId(@Param("shopId") Long shopId);

    List<Order> findByUserId(Long userId);
}
