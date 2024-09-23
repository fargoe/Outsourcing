package com.sparta.outsourcing.domain.menu.service;

import com.sparta.outsourcing.domain.menu.dto.MenuRequestDto;
import com.sparta.outsourcing.domain.menu.dto.MenuResponseDto;
import com.sparta.outsourcing.domain.menu.entity.Menu;
import com.sparta.outsourcing.domain.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    //메뉴 생성
    @Transactional
    public MenuResponseDto createMenu(Long shopId, MenuRequestDto requestDto) {
        Menu menu = Menu.builder()
                .shopId(shopId)
                .menuName(requestDto.getMenu_name())
                .price(requestDto.getPrice())
                .build();

        menuRepository.save(menu);

        return MenuResponseDto.builder()
                .message("메뉴 생성 완료")
                .data(MenuResponseDto.Data.builder()
                        .status("un_deletable")
                        .build())
                .build();
    }

    //메뉴 수정
    @Transactional
    public MenuResponseDto updateMenu(Long shopId, Long menuId, MenuRequestDto requestDto) {
        Optional<Menu> optionalMenu = menuRepository.findById(menuId);
        if (optionalMenu.isPresent()) {
            Menu menu = optionalMenu.get();
            if (menu.getShopId().equals(shopId)) {
                menu = menu.toBuilder()
                        .menuName(requestDto.getMenu_name())
                        .price(requestDto.getPrice())
                        .build();
                menuRepository.save(menu);
                return MenuResponseDto.builder()
                        .message("메뉴 수정 완료")
                        .data(MenuResponseDto.Data.builder()
                                .status("un_deletable")
                                .build())
                        .build();
            }
        }
        throw new RuntimeException("메뉴를 수정할 수 없습니다.");
    }

    //메뉴 삭제
    @Transactional
    public MenuResponseDto deleteMenu(Long shopId, Long menuId) {
        Optional<Menu> optionalMenu = menuRepository.findById(menuId);
        if (optionalMenu.isPresent()) {
            Menu menu = optionalMenu.get();
            if (menu.getShopId().equals(shopId)) {
                menu = menu.toBuilder()
                        .status("deleted")
                        .build();
                menuRepository.save(menu);

                return MenuResponseDto.builder()
                        .message("메뉴 삭제 완료")
                        .data(MenuResponseDto.Data.builder()
                                .status("deleted")
                                .build())
                        .build();
            }
        }
        throw new RuntimeException("메뉴를 삭제할 수 없습니다.");
    }
}