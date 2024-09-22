package com.sparta.outsourcing.domain.review.service;

import com.sparta.outsourcing.domain.order.entity.Order;
import com.sparta.outsourcing.domain.order.entity.OrderStatus;
import com.sparta.outsourcing.domain.order.repository.OrderRepository;
import com.sparta.outsourcing.domain.review.dto.ReviewRequestDto;
import com.sparta.outsourcing.domain.review.dto.ReviewResponseDto;
import com.sparta.outsourcing.domain.review.entity.Review;
import com.sparta.outsourcing.domain.review.repository.ReviewRepository;
import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;

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

    //리뷰 조회
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getShopReviews(Long shopId, Integer minRating, Integer maxRating) {
        shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

        // 별점 범위 검증
        if (minRating != null && (minRating < 1 || minRating > 5)) {
            throw new IllegalArgumentException("별점은 1점부터 5점까지만 가능합니다.");
        }
        if (maxRating != null && (maxRating < 1 || maxRating > 5)) {
            throw new IllegalArgumentException("별점은 1점부터 5점까지만 가능합니다.");
        }
        if (minRating != null && maxRating != null && minRating > maxRating) {
            throw new IllegalArgumentException("최소 별점은 최대 별점보다 클 수 없습니다.");
        }

        // 리뷰 조회
        List<Review> reviews = reviewRepository.findByShopIdAndRatingBetweenOrderByReviewTimeDesc(shopId,
                minRating == null ? 1 : minRating,
                maxRating == null ? 5 : maxRating);

        if (reviews.isEmpty()) {
            throw new IllegalArgumentException("해당 가게에 대한 리뷰가 존재하지 않습니다.");
        }

        return reviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }
}
