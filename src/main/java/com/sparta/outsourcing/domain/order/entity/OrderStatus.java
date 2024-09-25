package com.sparta.outsourcing.domain.order.entity;

public enum OrderStatus {
    PENDING,       // 주문 대기
    ACCEPTED,      // 주문 수락됨
    IN_PROGRESS,   // 진행 중
    COMPLETED,     // 완료됨
    CANCELED       // 취소됨
}
