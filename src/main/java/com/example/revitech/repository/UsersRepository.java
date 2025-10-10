package com.example.revitech.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
    List<Users> findAll();
    Optional<Users> findByEmail(String email);
}
