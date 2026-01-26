package com.example.revitech.repository;

import java.util.List;
import java.util.Optional; // ★追加

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    
    List<Enrollment> findBySubjectId(Integer subjectId);

    // ★★★ 追加: ユーザーIDから所属情報を探すメソッド ★★★
    Optional<Enrollment> findByUsersId(Integer usersId);
}