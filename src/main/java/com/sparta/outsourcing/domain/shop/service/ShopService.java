package com.sparta.outsourcing.domain.shop.service;

import com.sparta.outsourcing.domain.menu.dto.MenuResponseDto;
import com.sparta.outsourcing.domain.shop.dto.ShopRequestDto;
import com.sparta.outsourcing.domain.shop.dto.ShopResponseDto;
import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.shop.repository.ShopRepository;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import com.sparta.outsourcing.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;
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
        return new ShopResponseDto(savedShop.getId(), savedShop.getName(), savedShop.getOpentime(), savedShop.getClosetime(), savedShop.getMinOrderAmount(), List.of());
    }

    // 가게 수정
    public ShopResponseDto updateShop(Long shopId, ShopRequestDto shopRequest, User user) {
        // 해당 가게 찾기
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 가게를 찾을 수 없습니다."));

        // 사용자가 가게 소유자인지 확인
        if (!shop.getOwner().getId().equals(user.getId())) {
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

        // 수정된 가게에 대해 메뉴는 그대로 유지
        List<MenuResponseDto> menuList = updatedShop.getMenus().stream()
                .map(menu -> MenuResponseDto.builder()
                        .message("메뉴 조회 성공")
                        .data(MenuResponseDto.Data.builder()
                                .status("SUCCESS")
                                .build())
                        .menuId(menu.getId())
                        .menuName(menu.getMenuName())
                        .price(menu.getPrice())
                        .build())
                .collect(Collectors.toList());

        return new ShopResponseDto(
                updatedShop.getId(),
                updatedShop.getName(),
                updatedShop.getOpentime(),
                updatedShop.getClosetime(),
                updatedShop.getMinOrderAmount(),
                menuList
        );
    }

    // 가게 다건 조회
    public List<ShopResponseDto> getShopsByName(String name) {
        List<Shop> shops = shopRepository.findByNameContaining(name);
        return shops.stream()
                .map(shop -> new ShopResponseDto(
                        shop.getId(),
                        shop.getName(),
                        shop.getOpentime(),
                        shop.getClosetime(),
                        shop.getMinOrderAmount(),
                        List.of()
                ))
                .collect(Collectors.toList());
    }

    // 가게 단건 조회
    @Transactional
    public ShopResponseDto getShopById(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("가게를 찾을 수 없습니다."));

        // 가게에 등록된 메뉴 조회
        List<MenuResponseDto> menuList = shop.getMenus().stream()
                .map(menu -> MenuResponseDto.builder()
                        .message("메뉴 조회 성공")
                        .data(MenuResponseDto.Data.builder()
                                .status("SUCCESS")
                                .build())
                        .menuId(menu.getId())
                        .menuName(menu.getMenuName())
                        .price(menu.getPrice())
                        .build())
                .collect(Collectors.toList());

        // ShopResponseDto에 메뉴 리스트 포함하여 반환
        return new ShopResponseDto(
                shop.getId(),
                shop.getName(),
                shop.getOpentime(),
                shop.getClosetime(),
                shop.getMinOrderAmount(),
                menuList
        );
    }

    // 가게 폐업
    public void closeShop(Long shopId, User user) {
        // 해당 가게 찾기
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new EntityNotFoundException("해당 가게를 찾을 수 없습니다."));

        // 사용자가 가게 소유자인지 확인
        if (!shop.getOwner().getId().equals(user.getId())) {
            throw new SecurityException("가게 폐업 권한이 없습니다.");
        }

        // 가게 상태를 폐업으로 변경
        shop.close(); // 폐업 메서드 호출
        shopRepository.save(shop); // 변경 사항 저장
    }
}
