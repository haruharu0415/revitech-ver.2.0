package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
}
