package com.sparta.outsourcing.domain.order.aop;

import com.sparta.outsourcing.domain.order.dto.OrderResponseDto;
import com.sparta.outsourcing.domain.order.entity.Order;
import com.sparta.outsourcing.domain.order.entity.OrderStatus;
import com.sparta.outsourcing.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderLoggingAspect {

    private final OrderRepository orderRepository;

    // 주문 생성 성공 및 실패 로그 기록
    @Around("execution(* com.sparta.outsourcing.domain.order.service.OrderService.createOrder(..))")
    public Object logOrderCreation(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long shopId = (Long) args[1];
        Long userId = (Long) args[2];
        LocalDateTime now = LocalDateTime.now();

        try {
            Object result = joinPoint.proceed();
            log.info("INFO: [{}] 주문 생성 성공 - 가게 ID: {}, 주문 ID: {}, 주문 유저 ID: {}", now, shopId, ((OrderResponseDto) result).getOrderId(), userId);
            return result;
        } catch (Exception ex) {
            log.error("ERROR: [{}] 주문 생성 실패 - 가게 ID: {}, 주문 유저 ID: {}, 에러 발생", now, shopId, userId);
            throw ex;
        }
    }

    // 주문 상태 변경 성공 및 실패 로그 기록
    @Around("execution(* com.sparta.outsourcing.domain.order.service.OrderService.updateOrderStatus(..))")
    public Object logOrderStatusUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Long orderId = (Long) args[0];
        String newStatus = (String) args[1]; // 변경할 상태 값
        Long userId = (Long) args[2];

        LocalDateTime now = LocalDateTime.now();

        try {
            Object result = joinPoint.proceed();

            log.info("INFO: [{}] 주문 상태 변경 성공 - 주문 ID: {}, 유저 ID: {}, 변경 상태: {}",
                    now, orderId, userId, newStatus);

            return result;
        } catch (Exception ex) {
            log.error("ERROR: [{}] 주문 상태 변경 실패 - 주문 ID: {}, 유저 ID: {}",
                    now, orderId, userId);
            throw ex;
        }
    }


}
