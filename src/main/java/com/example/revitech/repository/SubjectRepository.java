package com.example.revitech.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);
}