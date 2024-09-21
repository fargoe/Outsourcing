package com.sparta.outsourcing.domain.shop.service;

import com.sparta.outsourcing.domain.shop.dto.ShopRequestDto;
import com.sparta.outsourcing.domain.shop.dto.ShopResponseDto;
import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.shop.repository.ShopRepository;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import com.sparta.outsourcing.domain.user.repository.UserRepository;
import com.sparta.outsourcing.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // 가게 생성
    public ShopResponseDto createShop(ShopRequestDto shopRequest, User authUser) {
        // 사용자가 사장님인지 확인
        if (!userService.hasRole(authUser, UserRoleEnum.OWNER)) {
            throw new SecurityException("사장님만 가게를 생성할 수 있습니다.");
        }

        // 사장님이 운영하는 가게가 3개 이상인지 확인
        if (shopRepository.findByOwnerAndClosedFalse(authUser).size() >= 3) {
            throw new IllegalStateException("가게는 최대 3개까지만 운영할 수 있습니다.");
        }

        // 새로운 가게 생성
        Shop shop = Shop.builder()
                .owner(authUser)
                .name(shopRequest.getName())
                .opentime(shopRequest.getOpentime())
                .closetime(shopRequest.getClosetime())
                .minOrderAmount(shopRequest.getMinOrderAmount())
                .closed(false)
                .build();

        // Shop을 저장하고 ShopResponseDto로 변환
        Shop savedShop = shopRepository.save(shop);
        return new ShopResponseDto(savedShop.getId(), savedShop.getName(), savedShop.getOpentime(), savedShop.getClosetime(), savedShop.getMinOrderAmount());
    }

    // 가게 수정
    public ShopResponseDto updateShop(Long shopId, ShopRequestDto shopRequest, User authUser) {
        // 해당 가게 찾기
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 가게를 찾을 수 없습니다."));

        // 사용자가 가게 소유자인지 확인
        if (!shop.getOwner().equals(authUser)) {
            throw new SecurityException("가게 수정 권한이 없습니다.");
        }

        // 새로운 가게 정보를 설정
        shop.updateShopDetails(
                shopRequest.getName(),
                shopRequest.getOpentime(),
                shopRequest.getClosetime(),
                shopRequest.getMinOrderAmount()
        );

        // 변경 사항 저장
        Shop updatedShop = shopRepository.save(shop);

        return new ShopResponseDto(
                updatedShop.getId(),
                updatedShop.getName(),
                updatedShop.getOpentime(),
                updatedShop.getClosetime(),
                updatedShop.getMinOrderAmount()
        );
    }
}
