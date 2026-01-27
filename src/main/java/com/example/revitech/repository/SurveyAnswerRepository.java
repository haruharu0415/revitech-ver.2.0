package com.example.revitech.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.SurveyAnswer;

@Repository
public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Integer> {
    
    List<SurveyAnswer> findByReviewId(Integer reviewId);

    // ★★★ 追加: 先生IDに紐づく、質問ごとの平均スコアを算出するクエリ ★★★
    // SurveyAnswer(a), SurveyQuestion(q), TeacherReview(r) を結合
    @Query("SELECT new map(q.questionBody as questionText, AVG(a.score) as averageScore) " +
           "FROM SurveyAnswer a, SurveyQuestion q, TeacherReview r " +
           "WHERE a.questionId = q.questionId " +
           "AND a.reviewId = r.reviewId " +
           "AND r.teacherId = :teacherId " +
           "GROUP BY q.questionBody")
    List<Map<String, Object>> findAverageScoresByTeacherId(@Param("teacherId") Integer teacherId);
}