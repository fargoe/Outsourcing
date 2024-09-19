package com.sparta.outsourcing.domain.user.entity;

import com.sparta.outsourcing.domain.user.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="user_id")
    private Long id;
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    public User(UserRequestDto userRequest){
        this.email = userRequest.getEmail();
        this.password = userRequest.getPassword();
    }
}

