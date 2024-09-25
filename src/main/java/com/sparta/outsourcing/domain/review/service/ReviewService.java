package com.sparta.outsourcing.domain.review.service;

import com.sparta.outsourcing.domain.order.entity.Order;
import com.sparta.outsourcing.domain.order.entity.OrderStatus;
import com.sparta.outsourcing.domain.order.repository.OrderRepository;
import com.sparta.outsourcing.domain.review.dto.ReviewRequestDto;
import com.sparta.outsourcing.domain.review.dto.ReviewResponseDto;
import com.sparta.outsourcing.domain.review.entity.Review;
import com.sparta.outsourcing.domain.review.repository.ReviewRepository;
import com.sparta.outsourcing.domain.shop.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;

    // 리뷰 생성
    @Transactional
    public ReviewResponseDto createReview(Long orderId, ReviewRequestDto reviewRequestDto, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        validateReviewCreation(order, userId);
        Review review = Review.builder()
                .order(order)
                .userId(userId)
                .shopId(order.getShop().getId())
                .rating(reviewRequestDto.getRating())
                .reviewContent(reviewRequestDto.getReviewContent())
                .reviewTime(LocalDateTime.now())
                .build();

        reviewRepository.save(review);

        return ReviewResponseDto.builder()
                .reviewId(review.getId())
                .userId(review.getUserId())
                .rating(review.getRating())
                .reviewContent(review.getReviewContent())
                .reviewTime(review.getReviewTime())
                .build();
    }

    // 리뷰 조회
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getShopReviews(Long shopId, Integer minRating, Integer maxRating) {
        // 기본값 처리: minRating이 없으면 1, maxRating이 없으면 5로 설정
        minRating = (minRating == null) ? 1 : minRating;
        maxRating = (maxRating == null) ? 5 : maxRating;

        // 별점 범위 검증
        validateRatingRange(minRating, maxRating);

        shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 가게를 찾을 수 없습니다."));

        List<Review> allReviews = reviewRepository.findByShopIdAndRatingBetweenOrderByReviewTimeDesc(shopId, 1, 5);
        if (allReviews.isEmpty()) {
            throw new IllegalArgumentException("해당 가게에 대한 리뷰가 존재하지 않습니다.");
        }

        List<Review> reviews = reviewRepository.findByShopIdAndRatingBetweenOrderByReviewTimeDesc(shopId, minRating, maxRating);

        if (reviews.isEmpty()) {
            throw new IllegalArgumentException("해당 범위 내 리뷰가 존재하지 않습니다.");
        }

        return reviews.stream()
                .map(review -> ReviewResponseDto.builder()
                        .reviewId(review.getId())
                        .userId(review.getUserId())
                        .rating(review.getRating())
                        .reviewContent(review.getReviewContent())
                        .reviewTime(review.getReviewTime())
                        .build())
                .collect(Collectors.toList());
    }

    // 리뷰 생성 검증 로직
    private void validateReviewCreation(Order order, Long userId) {
        if (order == null) {
            throw new EntityNotFoundException("주문이 존재하지 않습니다.");
        }

        if (!order.getUserId().equals(userId)) {
            throw new SecurityException("해당 주문의 리뷰를 작성할 권한이 없습니다.");
        }

        if (reviewRepository.existsByOrderId(order.getId())) {
            throw new IllegalArgumentException("해당 주문에는 이미 리뷰가 존재합니다.");
        }

        if (order.getOrderStatus() == null || order.getOrderStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("배달 완료된 주문에만 리뷰를 작성할 수 있습니다.");
        }
    }

    // 별점 범위 검증 로직
    private void validateRatingRange(Integer minRating, Integer maxRating) {
        if (minRating < 1 || minRating > 5 || maxRating < 1 || maxRating > 5) {
            throw new IllegalArgumentException("별점은 1점부터 5점까지만 가능합니다.");
        }
        if (minRating > maxRating) {
            throw new IllegalArgumentException("최소 별점은 최대 별점보다 클 수 없습니다.");
        }
    }

}

