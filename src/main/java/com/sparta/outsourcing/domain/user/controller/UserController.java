package com.sparta.outsourcing.domain.user.controller;

import com.sparta.outsourcing.domain.user.dto.UserRequestDto;
import com.sparta.outsourcing.domain.user.dto.UserResponseDto;
import com.sparta.outsourcing.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRequestDto userRequest, HttpServletResponse res) {
        return ResponseEntity.ok(userService.signup(userRequest, res));
    }
}
