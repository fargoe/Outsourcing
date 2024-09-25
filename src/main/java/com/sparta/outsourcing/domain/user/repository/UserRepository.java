package com.sparta.outsourcing.domain.user.repository;

import com.sparta.outsourcing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<Object> findByEmail(String email);

    @Query(value = "SELECT email FROM Users u WHERE u.email = :email AND u.deleted = true", nativeQuery = true)
    Optional<Object> findDeletedEmail(String email);
}
