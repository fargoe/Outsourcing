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

import java.util.List;

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
    @PutMapping("/{shop_Id}")
    public ResponseEntity<ShopResponseDto> updateShop(
            @PathVariable Long shopId,
            @RequestBody ShopRequestDto shopRequest,
            @Auth User authUser) {
        ShopResponseDto updatedShop = shopService.updateShop(shopId, shopRequest, authUser);
        return ResponseEntity.ok(updatedShop);
    }

    // 가게 다건 조회
    @GetMapping("/search")
    public ResponseEntity<List<ShopResponseDto>> getShopsByName(@RequestParam String name) {
        List<ShopResponseDto> shops = shopService.getShopsByName(name);
        return ResponseEntity.ok(shops);
    }

    // 가게 단건 조회
    @GetMapping("/{shop_Id}")
    public ResponseEntity<ShopResponseDto> getShopById(@PathVariable Long shopId) {
        ShopResponseDto shop = shopService.getShopById(shopId);
        return ResponseEntity.ok(shop);
    }

    // 가게 폐업
    @DeleteMapping("/{shop_Id}/close")
    public ResponseEntity<Void> closeShop(@PathVariable Long shopId, @Auth User authUser) {
        shopService.closeShop(shopId, authUser);
        return ResponseEntity.noContent().build(); // 204 No Content 응답
    }
}