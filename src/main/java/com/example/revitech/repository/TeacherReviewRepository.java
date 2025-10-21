package com.example.revitech.repository;

import com.example.revitech.entity.TeacherReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherReviewRepository extends JpaRepository<TeacherReview, Integer> {

    // ★★★ 新規追加: 特定の教員の平均スコアを計算する ★★★
    @Query("SELECT AVG(tr.score) FROM TeacherReview tr WHERE tr.teacherId = :teacherId")
    Double findAverageScoreByTeacherId(@Param("teacherId") Integer teacherId);
}