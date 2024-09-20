package com.sparta.outsourcing.domain.shop.controller;

import com.sparta.outsourcing.domain.shop.dto.ShopRequestDto;
import com.sparta.outsourcing.domain.shop.dto.ShopResponseDto;
import com.sparta.outsourcing.domain.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shops")
@Validated
public class ShopController {
    private final ShopService shopService;

    // 가게 생성
    @PostMapping
    public ResponseEntity<ShopResponseDto> createShop(@RequestBody @Valid ShopRequestDto shopRequest) {
        ShopResponseDto createdShop = shopService.createShop(shopRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShop); // 201 Created 응답
    }
}