package com.example.revitech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.revitech.entity.ReviewQuestion;
import com.example.revitech.entity.ReviewQuestionId;

public interface ReviewQuestionRepository extends JpaRepository<ReviewQuestion, ReviewQuestionId> {

     // 特定のレビュー(reviewId)に紐づく質問を取得
     List<ReviewQuestion> findByIdReviewId(Long reviewId);

     // 特定の質問(questionId)が使われているレビューを取得
     List<ReviewQuestion> findByIdQuestionId(Long questionId);
}