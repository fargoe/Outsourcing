package com.sparta.outsourcing.domain.menu.repository;

import com.sparta.outsourcing.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByShopIdAndStatus(Long shopId, String status);
}
