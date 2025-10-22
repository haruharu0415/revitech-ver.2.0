package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.StudentProfile;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    // 主キーが userId なので、findById(userId) で検索可能
}