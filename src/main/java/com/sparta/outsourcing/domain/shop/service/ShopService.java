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

    public ShopResponseDto createShop(ShopRequestDto shopRequest) {
        String username = shopRequest.getUsername();

        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 유저의 권한을 Enum으로 체크
        if (!userService.hasRole(owner, UserRoleEnum.OWNER)) {
            throw new SecurityException("사장님만 가게를 생성할 수 있습니다.");
        }

        // 사장님이 3개 이상의 가게를 운영할 수 없도록 체크
        if (shopRepository.findByOwnerAndClosedFalse(owner).size() >= 3) {
            throw new IllegalStateException("가게는 최대 3개까지만 운영할 수 있습니다.");
        }

        Shop shop = Shop.builder()
                .owner(owner)
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
}
