package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.TeacherComment;

public interface TeacherCommentRepository extends JpaRepository<TeacherComment, Long> {
    // DB設計修正後、findByTeacherUserId や findByStudentUserId を追加
    // List<TeacherComment> findByUserId(Long userId); // 現在の設計の場合
}