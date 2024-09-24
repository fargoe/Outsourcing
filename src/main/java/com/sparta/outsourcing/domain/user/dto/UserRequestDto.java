package com.sparta.outsourcing.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class UserRequestDto {
    @Email
    private String email;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\\p{Punct}])[A-Za-z\\d\\p{Punct}]{8,20}$",
            message = "Password must be 8-20 characters long and include at least one letter, one number, and one special character.")
    private String password;
    private boolean owner = false;
    private String ownerToken ="";

    public UserRequestDto(String email) {
        this.email = email;
    }
}
