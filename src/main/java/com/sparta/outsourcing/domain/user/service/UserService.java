package com.sparta.outsourcing.domain.user.service;

import com.sparta.outsourcing.domain.user.dto.*;
import com.sparta.outsourcing.global.config.JwtUtil;
import com.sparta.outsourcing.global.config.PasswordEncoder;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import com.sparta.outsourcing.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Value("${spring.owner.token}")
    private String OWNER_TOKEN;


    public UserResponseDto signup(@Valid UserRequestDto userRequest, HttpServletResponse res) {
        String email = userRequest.getEmail();
        //비밀번호 암호와
        String password = passwordEncoder.encode(userRequest.getPassword());
        //이메일 중복 검증
        if (userRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }

        UserRoleEnum role = UserRoleEnum.USER;

        //관리자 권한 검증
        if (userRequest.isOwner()) {
            if (!OWNER_TOKEN.equals(userRequest.getOwnerToken())) {
                throw new IllegalArgumentException("관리자 암호가 일치 하지 않아 등록이 불가능합니다.");
            }
            role = UserRoleEnum.OWNER;
        }

        // RequestDto -> Entity
        User user = new User(userRequest,password,role);
        //DB저장
        User saveUser = userRepository.save(user);
        //token생성 및 쿠키
        String token = jwtUtil.createToken(saveUser.getId());
        jwtUtil.addJwtToCookie(token,res);
        return new UserResponseDto(saveUser);
    }

    public LoginResponseDto login(UserRequestDto userRequest, HttpServletResponse res) {
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        User user = (User) userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을수없습니다"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀 번호를 입력하셨습니다");
        }
        String token = jwtUtil.createToken(user.getId());
        jwtUtil.addJwtToCookie(token, res);
        return new LoginResponseDto(token);
    }

    public String changePassword(Long userId, ChangePasswordRequestDto passwordRequest, AuthUser authUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을수 없습니다"));
        if(!userId.equals(authUser.getId())) {
            throw new IllegalArgumentException("유저 정보가 일치 하지 않습니다.");
        }
        if(!passwordEncoder.matches(passwordRequest.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현제 비밀번호가 일치하지않습니다.");
        }
        System.out.println(passwordRequest.getNewPassword());
        String password = passwordEncoder.encode(passwordRequest.getNewPassword());
        user.changePassword(password);
        userRepository.save(user);
        return "비밀번호 변경 완료";
    }

    public String deleteUser(Long userId, AuthUser authUser, UserRequestDto userRequest) {
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저를 찾을수 없습니다"));
            String password = userRequest.getPassword();

            if(!userId.equals(authUser.getId())) {
                throw new IllegalArgumentException("유저 정보가 일치 하지 않습니다.");
            }

            if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치 하지 않습니다.");
            }

            userRepository.delete(user);
            return "회원탈퇴 완료";
    }
}
