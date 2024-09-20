package com.sparta.outsourcing.domain.user.service;

import com.sparta.outsourcing.domain.user.config.PasswordEncoder;
import com.sparta.outsourcing.domain.user.dto.UserRequestDto;
import com.sparta.outsourcing.domain.user.dto.UserResponseDto;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.entity.UserRoleEnum;
import com.sparta.outsourcing.domain.user.repository.UserRepository;
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

    @Value("${spring.owner.token}")
    private String OWNER_TOKEN;


    public UserResponseDto signup(@Valid UserRequestDto userRequest) {
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
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.OWNER;
        }

        // RequestDto -> Entity
        User user = new User(userRequest,password,role);
        //DB저장
        User saveUser = userRepository.save(user);
        return new UserResponseDto(saveUser);
    }
}
