package com.example.revitech.repository;

import java.util.List;
import java.util.Optional; // ★追加

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    
    // 学科IDで検索
    List<Enrollment> findBySubjectId(Integer subjectId);

    // ★追加: ユーザーIDから所属情報を1件取得する
    Optional<Enrollment> findByUsersId(Integer usersId);
}