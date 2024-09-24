package com.sparta.outsourcing.domain.review.entity;

import com.sparta.outsourcing.domain.order.entity.Order;
import com.sparta.outsourcing.domain.user.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Long userId;
    private Long shopId;
    private int rating;
    private String reviewContent;
    private LocalDateTime reviewTime;

    @Builder
    public Review(Order order, Long userId, Long shopId, int rating, String reviewContent, LocalDateTime reviewTime) {
        this.order = order;
        this.userId = userId;
        this.shopId = shopId;
        this.rating = rating;
        this.reviewContent = reviewContent;
        this.reviewTime = reviewTime != null ? reviewTime : LocalDateTime.now();
    }
}
