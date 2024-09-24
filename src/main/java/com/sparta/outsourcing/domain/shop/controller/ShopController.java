package com.sparta.outsourcing.domain.shop.controller;

import com.sparta.outsourcing.domain.shop.dto.ShopRequestDto;
import com.sparta.outsourcing.domain.shop.dto.ShopResponseDto;
import com.sparta.outsourcing.domain.shop.service.ShopService;
import com.sparta.outsourcing.domain.user.dto.AuthUser;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.repository.UserRepository;
import com.sparta.outsourcing.global.annotation.Auth;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
@Validated
public class ShopController {
    private final ShopService shopService;
    private final UserRepository userRepository;

    // 가게 생성
    @PostMapping
    public ResponseEntity<ShopResponseDto> createShop(@RequestBody @Valid ShopRequestDto shopRequest, @Auth AuthUser authUser) {
        // AuthUser의 정보를 사용해 User 엔티티를 찾음
        User user = userRepository.findById(authUser.getId())  // authUser.getId() 또는 적절한 필드를 사용
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        ShopResponseDto createdShop = shopService.createShop(shopRequest, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShop); // 201 Created 응답
    }

    // 가게 수정
    @PutMapping("/{shopId}")
    public ResponseEntity<ShopResponseDto> updateShop(
            @PathVariable Long shopId,
            @RequestBody ShopRequestDto shopRequest,
            @Auth AuthUser authUser) {

        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        ShopResponseDto updatedShop = shopService.updateShop(shopId, shopRequest, user);
        return ResponseEntity.ok(updatedShop);
    }

    // 가게 다건 조회
    @GetMapping("/search")
    public ResponseEntity<List<ShopResponseDto>> getShopsByName(@RequestParam(value = "name", required = false, defaultValue = "defaultName") String name) {
        List<ShopResponseDto> shops = shopService.getShopsByName(name);
        return ResponseEntity.ok(shops);
    }

    // 가게 단건 조회
    @GetMapping("/{shopId}")
    public ResponseEntity<ShopResponseDto> getShopById(@PathVariable Long shopId) {
        ShopResponseDto shop = shopService.getShopById(shopId);
        return ResponseEntity.ok(shop);
    }

    // 가게 폐업
    @DeleteMapping("/{shopId}/close")
    public ResponseEntity<Void> closeShop(@PathVariable Long shopId, @Auth AuthUser authUser) {

        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        shopService.closeShop(shopId, user);
        return ResponseEntity.noContent().build(); // 204 No Content 응답
    }
}
