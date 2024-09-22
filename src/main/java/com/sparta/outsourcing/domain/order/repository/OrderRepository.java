package com.sparta.outsourcing.domain.order.repository;

import com.sparta.outsourcing.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.shop.id = :shopId")
    List<Order> findByShopId(@Param("shopId") Long shopId);

    List<Order> findByUserId(Long userId);
}
