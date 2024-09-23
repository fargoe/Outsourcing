package com.sparta.outsourcing.domain.user.controller;

import com.sparta.outsourcing.domain.user.dto.*;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import com.sparta.outsourcing.domain.user.service.UserService;
import com.sparta.outsourcing.global.config.PasswordEncoder;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserController.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class UserControllerTest {
    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;
    @InjectMocks
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 컨트롤러 성공")
    public void test_successful_signup() {
        //given
        UserService userService = mock(UserService.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        String email = "test@example.com";
        String password = "Password123!";
        String encodedPass = passwordEncoder.encode(password);
        UserRequestDto userRequest = UserRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        HttpServletResponse response = mock(HttpServletResponse.class);
        UserResponseDto userResponse = new UserResponseDto(new User(userRequest, encodedPass, UserRoleEnum.USER));
        //when
        when(userService.signup(any(UserRequestDto.class), any(HttpServletResponse.class))).thenReturn(userResponse);

        ResponseEntity<UserResponseDto> result = new UserController(userService).signup(userRequest, response);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(email, result.getBody().getEmail());
    }

    @Test
    @DisplayName("로그인 성공")
    public void test_successful_login_with_valid_credentials() {
        //given
        UserService userService = mock(UserService.class);
        String email = "test@example.com";
        String password = "Password123!";
        UserRequestDto userRequest = UserRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        User user = new User(userRequest, passwordEncoder.encode(password), UserRoleEnum.USER);
        HttpServletResponse response = mock(HttpServletResponse.class);
        LoginResponseDto loginResponse = new LoginResponseDto("validToken");
        //when
        when(userService.login(userRequest, mock(HttpServletResponse.class))).thenReturn(new LoginResponseDto("validToken"));
        when(userService.login(userRequest, response)).thenReturn(loginResponse);
        UserController userController = new UserController(userService);
        ResponseEntity<LoginResponseDto> result = userController.login(userRequest, response);
        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("validToken", result.getBody().getToken());
    }

    // Successfully deletes a user when correct user ID, authUser, and password are provided
    @Test
    public void test_successful_user_deletion() {
        //given
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);
        Long userId = 1L;
        String password = "Password123!";
        String email = "Password123!";
        AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, email);
        UserRequestDto userRequest = UserRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        //when
        when(userService.deleteUser(userId, authUser, userRequest)).thenReturn("회원탈퇴 완료");
        //then
        ResponseEntity<String> response = userController.deleteUser(userId, authUser, userRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("회원탈퇴 완료", response.getBody());
    }

    @Test
    @DisplayName("존제 하지않ㄴ는 유저 탈퇴 에러")
    public void test_unsuccessful_user_deletion() {
        //given
        UserService userService = mock(UserService.class);
        UserController userController = new UserController(userService);
        Long userId = 1L;
        String password = "Password123!";
        String email = "Password123!";
        AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, email);
        UserRequestDto userRequest = UserRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        //when
        when(userService.deleteUser(userId, authUser, userRequest)).thenThrow(new IllegalArgumentException("유저를 찾을수 없습니다"));
        //then
        assertThrows(IllegalArgumentException.class, () -> userController.deleteUser(userId, authUser, userRequest));
    }

    @Test
    @DisplayName("비밀번호 변경 완료")
    public void test_change_password_success() {
        UserService userService = mock(UserService.class);
        ChangePasswordRequestDto changePasswordRequest = new ChangePasswordRequestDto();
        String email = "test@example.com";
        AuthUser authUser = new AuthUser(1L, UserRoleEnum.USER, email);
        Long userId = 1L;

        when(userService.changePassword(userId, changePasswordRequest, authUser)).thenReturn("비밀번호 변경 완료");
        ResponseEntity<String> response = new UserController(userService).changePassword(userId, changePasswordRequest, authUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("비밀번호 변경 완료", response.getBody());
    }

}
