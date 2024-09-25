package com.sparta.outsourcing.domain.user.service;

import com.sparta.outsourcing.domain.user.dto.*;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import com.sparta.outsourcing.domain.user.repository.UserRepository;
import com.sparta.outsourcing.global.config.JwtUtil;
import com.sparta.outsourcing.global.config.PasswordEncoder;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    private PasswordEncoder passwordEncoder;
    @Mock
    JwtUtil jwtUtil;

    // Successfully signs up a new user with valid email and password
    @Test
    @DisplayName("정상작동 회원가입")
    public void test_successful_signup() {
        //given
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        UserService userService = new UserService(userRepository, passwordEncoder, jwtUtil);
        String email = "test@example.com";
        String password = "Password123!";
        UserRequestDto userRequestDto = UserRequestDto.builder()
                        .email(email)
                        .password(password)
                        .build();
        //when
        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        Mockito.when(userRepository.findDeletedEmail("test@example.com")).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(jwtUtil.createToken(Mockito.anyLong())).thenReturn("token");

        UserResponseDto userResponseDto = userService.signup(userRequestDto, response);
        //then
        Assertions.assertNotNull(userResponseDto);
        Assertions.assertEquals("test@example.com", userResponseDto.getEmail());
    }

    // Encrypts the user's password before saving to the database
    @Test
    @DisplayName("회원가입시 암호화 테스트")
    public void test_encrypts_password_before_saving() {
        //given
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        String email = "test@example.com";
        String password = "Password123!";
        String encodedPassword = passwordEncoder.encode(password);
        UserService userService = new UserService(userRepository, passwordEncoder, jwtUtil);

        UserRequestDto userRequestDto = UserRequestDto.builder()
                        .email(email)
                        .password(password)
                        .build();
        //when
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findDeletedEmail(email)).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(jwtUtil.createToken(Mockito.anyLong())).thenReturn("token");

        UserResponseDto userResponseDto = userService.signup(userRequestDto, response);
        User user = new User(userRequestDto,encodedPassword, UserRoleEnum.USER);
        //then
        Assertions.assertNotNull(userResponseDto);
        Assertions.assertTrue(passwordEncoder.matches(password,user.getPassword()));
        Assertions.assertFalse(passwordEncoder.matches(encodedPassword,user.getPassword()));
    }

    @Test
    @DisplayName("이메일 중복 검증 테스트")
    public void test_email_already_registered() {
        //given
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        UserService userService = new UserService(userRepository, passwordEncoder, jwtUtil);
        String email = "test@example.com";
        String password = "Password123!";
        UserRequestDto userRequestDto = UserRequestDto.builder()
                        .email(email)
                        .password(password)
                        .build();
        //when
        Mockito.when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new Object()));

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(userRequestDto, response);
        });
        //then
        Assertions.assertEquals("이미 가입된 이메일입니다.", exception.getMessage());
    }

    // Successful login with valid email and password
    @Test
    @DisplayName("로그인 성공")
    public void test_successful_login_with_valid_email_and_password() {
        //given
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        UserService userService = new UserService(userRepository, passwordEncoder, jwtUtil);
        String email = "test@example.com";
        String password = "ValidPassword1!";
        String encodedPassword = passwordEncoder.encode(password);
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email(email)
                .password(password)
                .build();

        User user = new User(userRequestDto, encodedPassword, UserRoleEnum.USER);
        //when
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findDeletedEmail(email)).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        Mockito.when(jwtUtil.createToken(user.getId())).thenReturn("mockToken");

        LoginResponseDto responseDto = userService.login(userRequestDto, response);
        //then
        Assertions.assertEquals("mockToken", responseDto.getToken());
        Mockito.verify(jwtUtil).addJwtToCookie("mockToken", response);
    }

    @Test
    @DisplayName("존재하지 않는 이메일 로그인")
    public void test_login_attempt_with_non_existent_email() {
        //given
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        UserService userService = new UserService(userRepository, passwordEncoder, jwtUtil);
        String email = "nonexistent@example.com";
        String password = "ValidPassword1!";

        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        //when
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        //then
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.login(userRequestDto, response);
        });

        Assertions.assertEquals("해당 유저를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    public void test_successful_password_change() {
        //given
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, passwordEncoder, new JwtUtil());

        Long userId = 1L;
        String email = "test@example.com";
        AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, email);
        ChangePasswordRequestDto passwordRequest = new ChangePasswordRequestDto();
        ReflectionTestUtils.setField(passwordRequest, "oldPassword", "oldPass123!");
        ReflectionTestUtils.setField(passwordRequest, "newPassword", "newPass123!");
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email(email)
                .password(passwordRequest.getOldPassword())
                .build();
        String encodedOldPassword = passwordEncoder.encode(passwordRequest.getOldPassword());
        String encodedNewPassword = passwordEncoder.encode(passwordRequest.getNewPassword());
        User user = new User(userRequestDto, encodedOldPassword, UserRoleEnum.USER);
        //when
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches("oldPass123!", user.getPassword())).thenReturn(true);
        Mockito.when(passwordEncoder.encode("newPass123!")).thenReturn(encodedNewPassword);
        //then
        String result = userService.changePassword(userId, passwordRequest, authUser);

        Assertions.assertEquals("비밀번호 변경 완료", result);
        Mockito.verify(userRepository).save(user);
    }

    // Throws IllegalArgumentException when old password does not match the current password
    @Test
    @DisplayName("비밀번호 변경 현재 비밀번호 불일치 에러")
    public void test_throws_exception_when_old_password_does_not_match2() {
        // given
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        Long userId = 1L;
        String email = "test@example.com";
        String password = "oldPass123!";
        String wrongPassword = "worngPass123!";
        String encodedPassword = passwordEncoder.encode(password);
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        ChangePasswordRequestDto passwordRequest = Mockito.mock(ChangePasswordRequestDto.class);
        ReflectionTestUtils.setField(passwordRequest, "oldPassword", wrongPassword);
        ReflectionTestUtils.setField(passwordRequest, "newPassword", "newPass123!");

        AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, email);
        User user = new User(userRequestDto, encodedPassword, UserRoleEnum.USER);

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        //when
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordRequest.getOldPassword()).thenReturn(wrongPassword);
        Mockito.when(passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())).thenReturn(false);

        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        UserService userService = new UserService(userRepository, passwordEncoder, jwtUtil);

        //then
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> userService.changePassword(userId, passwordRequest, authUser));
        Assertions.assertEquals("현제 비밀번호가 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원탈퇴 완료 성공")
    public void test_delete_user_success() {
        //given
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, passwordEncoder, new JwtUtil());
        String email = "test@example.com";
        String password = "Password123!";
        String encodedPassword = passwordEncoder.encode(password);
        Long userId = 1L;
        AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, email);
        UserRequestDto userRequest = UserRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        User user = new User(userRequest, encodedPassword, UserRoleEnum.USER);
        //when
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        String result = userService.deleteUser(userId, authUser, userRequest);
        //then
        Mockito.verify(userRepository).delete(user);
        Assertions.assertEquals("회원탈퇴 완료", result);
    }

    @Test
    @DisplayName("회원탈퇴 암호 불일치 실패")
    public void test_delete_user_password_mismatch() {
        //given
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, passwordEncoder, new JwtUtil());
        String email = "test@example.com";
        String password = "Password123!";
        String encodedPassword = passwordEncoder.encode(password);
        String wrongPassword = "wrongPass12!";
        Long userId = 1L;
        AuthUser authUser = new AuthUser(userId, UserRoleEnum.USER, email);
        UserRequestDto userRequest = UserRequestDto.builder()
                .email(email)
                .password(password)
                .build();
        User user = new User(userRequest, encodedPassword, UserRoleEnum.USER);
        //when
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);
        //then
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(userId, authUser, userRequest);
        });
        Assertions.assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }
}
