package com.sparta.outsourcing.domain.shop.repository;

import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {
    List<Shop> findByOwnerAndClosedFalse(User owner);
}
