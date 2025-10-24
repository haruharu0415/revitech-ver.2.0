package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.TeacherSubject;
import com.example.revitech.entity.TeacherSubjectId;

public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, TeacherSubjectId> {

    // 特定の教員(userId)が担当する科目の関連を取得
    List<TeacherSubject> findByIdUserId(Long userId);

    // 特定の科目(subjectId)を担当する教員の関連を取得
    List<TeacherSubject> findByIdSubjectId(Long subjectId);
}