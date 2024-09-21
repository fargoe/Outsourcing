package com.sparta.outsourcing.domain.shop.controller;

import com.sparta.outsourcing.domain.shop.dto.ShopRequestDto;
import com.sparta.outsourcing.domain.shop.dto.ShopResponseDto;
import com.sparta.outsourcing.domain.shop.service.ShopService;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.global.annotation.Auth;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
@Validated
public class ShopController {
    private final ShopService shopService;

    // 가게 생성
    @PostMapping
    public ResponseEntity<ShopResponseDto> createShop(@RequestBody @Valid ShopRequestDto shopRequest, @Auth User authUser) {
        ShopResponseDto createdShop = shopService.createShop(shopRequest, authUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShop); // 201 Created 응답
    }

    // 가게 수정
    @PutMapping("/{shopId}")
    public ResponseEntity<ShopResponseDto> updateShop(
            @PathVariable Long shopId,
            @RequestBody ShopRequestDto shopRequest,
            @Auth User authUser) {
        ShopResponseDto updatedShop = shopService.updateShop(shopId, shopRequest, authUser);
        return ResponseEntity.ok(updatedShop);
    }
}