package com.sparta.outsourcing.domain.menu.service;

import com.sparta.outsourcing.domain.menu.dto.MenuRequestDto;
import com.sparta.outsourcing.domain.menu.dto.MenuResponseDto;
import com.sparta.outsourcing.domain.menu.entity.Menu;
import com.sparta.outsourcing.domain.menu.repository.MenuRepository;
import com.sparta.outsourcing.domain.shop.entity.Shop;
import com.sparta.outsourcing.domain.shop.repository.ShopRepository;
import com.sparta.outsourcing.domain.user.dto.AuthUser;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.repository.UserRepository;
import com.sparta.outsourcing.domain.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    //메뉴 생성
    @Transactional
    public MenuResponseDto createMenu(Long shopId, MenuRequestDto requestDto, AuthUser authUser) {
        // Shop 존재 여부 확인
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("가게가 존재하지 않습니다."));

        // 사용자 존재 여부 확인
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 사장님 권한 확인
        if (!shop.getOwner().getId().equals(authUser.getId())) {
            throw new RuntimeException("권한이 없습니다. 사장님만 메뉴를 생성할 수 있습니다.");
        }

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
    public MenuResponseDto updateMenu(Long shopId, Long menuId, MenuRequestDto requestDto, AuthUser authUser) {
        // Shop 존재 여부 확인
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("가게가 존재하지 않습니다."));

        // 사용자 존재 여부 확인
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 사장님 권한 확인
        if (!shop.getOwner().getId().equals(authUser.getId())) {
            throw new RuntimeException("권한이 없습니다. 사장님만 메뉴를 수정할 수 있습니다.");
        }

        Optional<Menu> optionalMenu = menuRepository.findById(menuId);

        // 메뉴 존재 여부 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("해당 메뉴가 존재하지 않습니다."));


        if (optionalMenu.isPresent()) {
            menu = optionalMenu.get();
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
    public MenuResponseDto deleteMenu(Long shopId, Long menuId, AuthUser authUser) {
        // Shop 존재 여부 확인
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("가게가 존재하지 않습니다."));

        // 사용자 존재 여부 확인
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("사용자가 존재하지 않습니다."));

        // 사장님 권한 확인
        if (!shop.getOwner().getId().equals(authUser.getId())) {
            throw new RuntimeException("권한이 없습니다. 사장님만 메뉴를 삭제할 수 있습니다.");
        }

        // 메뉴 존재 여부 확인
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new EntityNotFoundException("해당 메뉴가 존재하지 않습니다."));

        Optional<Menu> optionalMenu = menuRepository.findById(menuId);
        if (optionalMenu.isPresent()) {
            menu = optionalMenu.get();
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