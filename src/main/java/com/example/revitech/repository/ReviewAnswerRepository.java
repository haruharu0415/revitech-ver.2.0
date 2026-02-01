package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.revitech.entity.ReviewAnswer;

@Repository
public interface ReviewAnswerRepository extends JpaRepository<ReviewAnswer, Integer> {
    
    // レビューIDで詳細回答を取得
    List<ReviewAnswer> findByReviewId(Integer reviewId);

    // 質問ごとの平均スコア（レーダーチャート等用）
    @Query("SELECT ra.questionId, AVG(ra.score) FROM ReviewAnswer ra JOIN TeacherReview tr ON ra.reviewId = tr.reviewId WHERE tr.teacherId = :teacherId GROUP BY ra.questionId")
    List<Object[]> findAverageScoreByQuestionForTeacher(@Param("teacherId") Integer teacherId);

    // 先生に対する「全ての詳細回答」の平均スコアを算出（総合評価用）
    @Query("SELECT AVG(ra.score) FROM ReviewAnswer ra JOIN TeacherReview tr ON ra.reviewId = tr.reviewId WHERE tr.teacherId = :teacherId")
    Double findTotalAverageScoreByTeacherId(@Param("teacherId") Integer teacherId);

    // ★★★ 追加: アンケートIDに紐づく詳細回答を削除するメソッド ★★★
    void deleteBySurveyId(Integer surveyId);
}