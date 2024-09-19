package com.sparta.outsourcing.domain.user.service;

import com.sparta.outsourcing.domain.user.dto.UserRequestDto;
import com.sparta.outsourcing.domain.user.dto.UserResponseDto;
import com.sparta.outsourcing.domain.user.entity.User;
import com.sparta.outsourcing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public UserResponseDto signup(UserRequestDto userRequest) {
        String email = userRequest.getEmail();
        if (userRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("이미 가입된 이메일 입니다.");
        }
        User user = new User(userRequest);
        User saveUser = userRepository.save(user);
        return new UserResponseDto(saveUser);
    }
}
