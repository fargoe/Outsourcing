package com.sparta.outsourcing.domain.review.repository;

import com.sparta.outsourcing.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByOrderId(Long orderId);
    List<Review> findByShopIdAndRatingBetweenOrderByReviewTimeDesc(Long shopId, Integer minRating, Integer maxRating);
}
