package com.sparta.outsourcing.domain.shop.repository;

import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {
    // 가게 소유자와 운영 중인 가게를 찾기 위한 메서드
    List<Shop> findByOwnerAndClosedFalse(User owner);

    // 가게명을 포함하는 모든 가게를 조회
    List<Shop> findByNameContaining(String name);
}
