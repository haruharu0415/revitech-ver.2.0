package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
     // DB設計修正後、findByTeacherUserId や findByStudentUserId を追加
}