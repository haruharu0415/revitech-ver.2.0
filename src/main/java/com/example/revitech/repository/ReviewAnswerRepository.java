package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ReviewAnswer;

@Repository
public interface ReviewAnswerRepository extends JpaRepository<ReviewAnswer, Integer> {
    
    // 特定のレビューに紐づく回答を取得
    List<ReviewAnswer> findByReviewId(Integer reviewId);

    // ★★★ エラー解消用: 先生ごとの質問別平均スコアを算出するクエリ ★★★
    @Query("SELECT ra.questionId, AVG(ra.score) FROM ReviewAnswer ra JOIN TeacherReview tr ON ra.reviewId = tr.reviewId WHERE tr.teacherId = :teacherId GROUP BY ra.questionId")
    List<Object[]> findAverageScoreByQuestionForTeacher(@Param("teacherId") Integer teacherId);
}