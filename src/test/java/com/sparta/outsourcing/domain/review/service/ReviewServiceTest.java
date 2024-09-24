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
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ShopRepository shopRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("리뷰 생성 테스트")
    class CreateReviewTests {
        @Test
        @DisplayName("리뷰 생성 성공")
        void createReview_success() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            Long shopId = 1L;
            ReviewRequestDto reviewRequestDto = new ReviewRequestDto(5, "Excellent!");

            // Mocking Order and Shop objects
            Order order = mock(Order.class);
            Shop shop = mock(Shop.class);

            when(order.getUserId()).thenReturn(userId);
            when(order.getOrderStatus()).thenReturn(OrderStatus.COMPLETED);
            when(order.getShop()).thenReturn(shop);
            when(shop.getId()).thenReturn(shopId);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
            when(reviewRepository.existsByOrderId(orderId)).thenReturn(false); // 리뷰가 존재하지 않음

            Review review = mock(Review.class);
            when(reviewRepository.save(any(Review.class))).thenReturn(review);  // 리뷰 저장 시 mock 리턴

            // when
            ReviewResponseDto response = reviewService.createReview(orderId, reviewRequestDto, userId);

            // then
            assertNotNull(response);
            assertEquals(5, response.getRating());
            assertEquals("Excellent!", response.getReviewContent());
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        @DisplayName("리뷰 생성 시 주문을 찾지 못했을 경우 예외 발생")
        void createReview_orderNotFound_throwsEntityNotFoundException() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            ReviewRequestDto reviewRequestDto = new ReviewRequestDto(5, "좋은 리뷰");

            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            // when & then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> reviewService.createReview(orderId, reviewRequestDto, userId));

            assertEquals("주문을 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("리뷰 생성 시 주문한 사용자와 리뷰 작성자가 다를 경우 예외 발생")
        void createReview_userMismatch_throwsSecurityException() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            ReviewRequestDto reviewRequestDto = new ReviewRequestDto(5, "Excellent!");

            Order order = mock(Order.class);
            when(order.getUserId()).thenReturn(2L);  // 주문한 사용자와 다른 userId
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            SecurityException exception = assertThrows(SecurityException.class,
                    () -> reviewService.createReview(orderId, reviewRequestDto, userId));

            assertEquals("해당 주문의 리뷰를 작성할 권한이 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("리뷰 생성 시 주문 상태가 완료되지 않은 경우 예외 발생")
        void createReview_orderNotCompleted_throwsIllegalStateException() {
            // given
            Long orderId = 1L;
            Long userId = 1L;
            ReviewRequestDto reviewRequestDto = new ReviewRequestDto(5, "Excellent!");

            Order order = mock(Order.class);
            when(order.getUserId()).thenReturn(userId);
            when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);  // 주문이 완료되지 않음
            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

            // when & then
            IllegalStateException exception = assertThrows(IllegalStateException.class,
                    () -> reviewService.createReview(orderId, reviewRequestDto, userId));

            assertEquals("배달 완료된 주문에만 리뷰를 작성할 수 있습니다.", exception.getMessage());
        }

//        @Test
//        @DisplayName("이미 리뷰가 존재할 경우 예외 발생")
//        void createReview_alreadyReviewed_throwsIllegalArgumentException() {
//            // given
//            Long orderId = 1L;
//            Long userId = 1L;
//            Long shopId = 1L;
//            ReviewRequestDto reviewRequestDto = new ReviewRequestDto(5, "Excellent!");
//
//            // Mocking Order and Shop objects
//            Order order = mock(Order.class);
//            Shop shop = mock(Shop.class);
//
//            when(order.getUserId()).thenReturn(userId);
//            when(order.getOrderStatus()).thenReturn(OrderStatus.COMPLETED);
//            when(order.getShop()).thenReturn(shop);
//            when(shop.getId()).thenReturn(shopId);
//
//            // Mock that the order already has a review
//            when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
//            when(reviewRepository.existsByOrderId(orderId)).thenReturn(true); // 리뷰가 이미 존재하는 상황 설정
//
//            System.out.println("Starting test for already existing review");
//
//            // when & then
//            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//                    () -> reviewService.createReview(orderId, reviewRequestDto, userId));
//
//            System.out.println("Exception thrown: " + exception.getMessage());
//
//            // Check the exception message
//            assertEquals("해당 주문에는 이미 리뷰가 존재합니다.", exception.getMessage());
//
//            // Verify that reviewRepository.existsByOrderId() is called
//            verify(reviewRepository, times(1)).existsByOrderId(orderId);
//        }
    }

    @Nested
    @DisplayName("리뷰 조회 테스트")
    class GetReviewTests{
        @Test
        @DisplayName("가게가 존재하지 않을 때 예외 발생")
        void getShopReviews_shopNotFound_throwsEntityNotFoundException() {
            // given
            Long shopId = 1L;
            when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

            // when & then
            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> reviewService.getShopReviews(shopId, null, null));

            assertEquals("해당 가게를 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("가게에 리뷰가 없을 때 예외 발생")
        void getShopReviews_noReviewsForShop_throwsIllegalArgumentException() {
            // given
            Long shopId = 1L;
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(mock(Shop.class)));
            when(reviewRepository.findByShopIdAndRatingBetweenOrderByReviewTimeDesc(shopId, 1, 5)).thenReturn(new ArrayList<>()); // 빈 리스트

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> reviewService.getShopReviews(shopId, null, null));

            assertEquals("해당 가게에 대한 리뷰가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("범위 내 리뷰가 존재하지 않을 때 예외 발생")
        void getShopReviews_noReviewsInRatingRange_throwsIllegalArgumentException() {
            // given
            Long shopId = 1L;
            List<Review> allReviews = new ArrayList<>();
            allReviews.add(mock(Review.class));  // 가게에 리뷰는 있지만 범위 내에는 없음

            when(shopRepository.findById(shopId)).thenReturn(Optional.of(mock(Shop.class)));
            when(reviewRepository.findByShopIdAndRatingBetweenOrderByReviewTimeDesc(shopId, 1, 5)).thenReturn(allReviews);  // 가게에 리뷰 존재
            when(reviewRepository.findByShopIdAndRatingBetweenOrderByReviewTimeDesc(shopId, 4, 5)).thenReturn(new ArrayList<>());  // 범위 내 리뷰 없음

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> reviewService.getShopReviews(shopId, 4, 5));

            // 예외 메시지 확인
            assertEquals("해당 범위 내 리뷰가 존재하지 않습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("리뷰 조회 성공")
        void getShopReviews_success() {
            // given
            Long shopId = 1L;
            Shop shop = mock(Shop.class);
            Review review = mock(Review.class);
            List<Review> reviews = new ArrayList<>();
            reviews.add(review);

            when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));
            when(reviewRepository.findByShopIdAndRatingBetweenOrderByReviewTimeDesc(shopId, 1, 5)).thenReturn(reviews);

            // when
            List<ReviewResponseDto> response = reviewService.getShopReviews(shopId, null, null);

            // then
            assertNotNull(response);
            assertFalse(response.isEmpty());

            // verify that the method is called twice: once for checking if reviews exist, and once for getting reviews in the rating range
            verify(reviewRepository, times(2)).findByShopIdAndRatingBetweenOrderByReviewTimeDesc(shopId, 1, 5);
        }

        @Test
        @DisplayName("리뷰 조회 시 별점 범위가 잘못된 경우 예외 발생")
        void getShopReviews_invalidRatingRange() {
            // given
            Long shopId = 1L;
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(mock(Shop.class)));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> reviewService.getShopReviews(shopId, 6, 5));  // minRating이 maxRating보다 큼

            assertEquals("별점은 1점부터 5점까지만 가능합니다.", exception.getMessage());
        }

        @Test
        @DisplayName("최소 별점이 최대 별점보다 클 수 없음")
        void getShopReviews_minGreaterThanMax() {
            // given
            Long shopId = 1L;
            when(shopRepository.findById(shopId)).thenReturn(Optional.of(mock(Shop.class)));

            // when & then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> reviewService.getShopReviews(shopId, 4, 3));  // minRating이 maxRating보다 큼

            assertEquals("최소 별점은 최대 별점보다 클 수 없습니다.", exception.getMessage());
        }
    }
}
