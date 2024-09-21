package com.sparta.outsourcing.domain.shop.entity;

import com.sparta.outsourcing.domain.user.entity.Timestamped;
import com.sparta.outsourcing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "shop")
public class Shop extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String name;
    private LocalTime opentime;
    private LocalTime closetime;

    @Column(name = "minOrderAmount", nullable = false)
    private BigDecimal minOrderAmount;

    private boolean closed;

    @Builder
    public Shop(User owner, String name, LocalTime opentime, LocalTime closetime, BigDecimal minOrderAmount, boolean closed) {
        this.owner = owner;
        this.name = name;
        this.opentime = opentime;
        this.closetime = closetime;
        this.minOrderAmount = minOrderAmount;
        this.closed = closed;
    }

    // 가게 정보를 업데이트하는 메서드
    public void updateShopDetails(String name, LocalTime opentime, LocalTime closetime, BigDecimal minOrderAmount) {
        this.name = name;
        this.opentime = opentime;
        this.closetime = closetime;
        this.minOrderAmount = minOrderAmount;
    }
}
