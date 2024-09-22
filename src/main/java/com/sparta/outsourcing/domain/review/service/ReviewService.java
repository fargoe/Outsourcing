package com.sparta.outsourcing.domain.review.service;

import com.sparta.outsourcing.domain.order.entity.Order;
import com.sparta.outsourcing.domain.order.entity.OrderStatus;
import com.sparta.outsourcing.domain.order.repository.OrderRepository;
import com.sparta.outsourcing.domain.review.dto.ReviewRequestDto;
import com.sparta.outsourcing.domain.review.dto.ReviewResponseDto;
import com.sparta.outsourcing.domain.review.entity.Review;
import com.sparta.outsourcing.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    //리뷰 생성
    @Transactional
    public ReviewResponseDto createReview(Long orderId, ReviewRequestDto reviewRequestDto, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 주문의 리뷰를 작성할 권한이 없습니다.");
        }

        if (order.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new IllegalArgumentException("배달 완료된 주문에만 리뷰를 작성할 수 있습니다.");
        }

        if (reviewRepository.existsByOrderId(orderId)) {
            throw new IllegalArgumentException("해당 주문에는 이미 리뷰가 존재합니다.");
        }

        if (reviewRequestDto.getRating() < 1 || reviewRequestDto.getRating() > 5) {
            throw new IllegalArgumentException("별점은 1점부터 5점까지만 가능합니다.");
        }

        Review review = new Review(order, userId, reviewRequestDto.getRating(), reviewRequestDto.getReviewContent());
        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }
}
