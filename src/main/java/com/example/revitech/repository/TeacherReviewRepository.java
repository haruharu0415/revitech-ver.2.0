package com.example.revitech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.TeacherReview;

@Repository
public interface TeacherReviewRepository extends JpaRepository<TeacherReview, Integer> {

    // ★★★ エラーの原因だった以下のメソッドを完全に削除します ★★★
    // @Query("SELECT AVG(tr.score) FROM TeacherReview tr WHERE tr.teacherId = :teacherId")
    // Double findAverageScoreByTeacherId(@Param("teacherId") Integer teacherId);

}