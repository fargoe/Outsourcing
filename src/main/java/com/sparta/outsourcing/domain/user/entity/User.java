package com.sparta.outsourcing.domain.user.entity;

import com.sparta.outsourcing.domain.user.dto.UserRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;

@Entity
@Getter
@SoftDelete(columnName="deleted")
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

    @Column(name = "role", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;


    public User(UserRequestDto userRequest,String password,UserRoleEnum role) {
        this.email = userRequest.getEmail();
        this.password = password;
        this.role = role;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}

