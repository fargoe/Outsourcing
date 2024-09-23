package com.sparta.outsourcing.domain.menu.controller;

import com.sparta.outsourcing.domain.menu.dto.MenuRequestDto;
import com.sparta.outsourcing.domain.menu.dto.MenuResponseDto;
import com.sparta.outsourcing.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/{shopId}")
    public ResponseEntity<MenuResponseDto> createMenu(@PathVariable Long shopId,
                                                      @RequestBody MenuRequestDto requestDto) {
        MenuResponseDto response = menuService.createMenu(shopId, requestDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{shopId}/{menuId}")
    public ResponseEntity<MenuResponseDto> updateMenu(@PathVariable Long shopId,
                                                      @PathVariable Long menuId,
                                                      @RequestBody MenuRequestDto requestDto) {
        MenuResponseDto response = menuService.updateMenu(shopId, menuId, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{shopId}/{menuId}")
    public ResponseEntity<MenuResponseDto> deleteMenu(@PathVariable Long shopId,
                                                      @PathVariable Long menuId) {
        MenuResponseDto response = menuService.deleteMenu(shopId, menuId);
        return ResponseEntity.ok(response);
    }

}