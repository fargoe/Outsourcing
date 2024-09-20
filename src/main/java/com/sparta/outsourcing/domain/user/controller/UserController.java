package com.sparta.outsourcing.domain.user.controller;

import com.sparta.outsourcing.domain.user.dto.*;
import com.sparta.outsourcing.domain.user.service.UserService;
import com.sparta.outsourcing.global.annotation.Auth;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDto> signup(@RequestBody UserRequestDto userRequest, HttpServletResponse res) {
        return ResponseEntity.ok(userService.signup(userRequest, res));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody UserRequestDto userRequest, HttpServletResponse res) {
        return ResponseEntity.ok(userService.login(userRequest, res));
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id, @Auth AuthUser authUser, @RequestBody UserRequestDto userRequest) {
        return ResponseEntity.ok(userService.deleteUser(user_id, authUser, userRequest));
    }

    @PatchMapping("/{user_id}/changepassword")
    public ResponseEntity<String> changePassword(@PathVariable Long user_id, @RequestBody ChangePasswordRequestDto changePasswordRequest, @Auth AuthUser authUser) {
        return ResponseEntity.ok(userService.changePassword(user_id, changePasswordRequest, authUser));
    }
}
