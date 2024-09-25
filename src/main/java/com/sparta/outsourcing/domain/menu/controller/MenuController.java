package com.sparta.outsourcing.domain.menu.controller;

import com.sparta.outsourcing.domain.menu.dto.MenuRequestDto;
import com.sparta.outsourcing.domain.menu.dto.MenuResponseDto;
import com.sparta.outsourcing.domain.menu.service.MenuService;
import com.sparta.outsourcing.domain.user.dto.AuthUser;
import com.sparta.outsourcing.domain.user.repository.UserRepository;
import com.sparta.outsourcing.global.annotation.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final UserRepository userRepository;

    // 메뉴 생성
    @PostMapping("/{shopId}")
    public ResponseEntity<MenuResponseDto> createMenu(@PathVariable Long shopId,
                                                      @RequestBody MenuRequestDto requestDto,
                                                      @Auth AuthUser authUser) {
        MenuResponseDto response = menuService.createMenu(shopId, requestDto, authUser);
        return ResponseEntity.ok(response);
    }

    // 메뉴 수정
    @PutMapping("/{shopId}/{menuId}")
    public ResponseEntity<MenuResponseDto> updateMenu(@PathVariable Long shopId,
                                                      @PathVariable Long menuId,
                                                      @RequestBody MenuRequestDto requestDto,
                                                      @Auth AuthUser authUser) {
        MenuResponseDto response = menuService.updateMenu(shopId, menuId, requestDto, authUser);
        return ResponseEntity.ok(response);
    }

    // 메뉴 삭제
    @DeleteMapping("/{shopId}/{menuId}")
    public ResponseEntity<MenuResponseDto> deleteMenu(@PathVariable Long shopId,
                                                      @PathVariable Long menuId,
                                                      @Auth AuthUser authUser) {
        MenuResponseDto response = menuService.deleteMenu(shopId, menuId, authUser);
        return ResponseEntity.ok(response);
    }

}