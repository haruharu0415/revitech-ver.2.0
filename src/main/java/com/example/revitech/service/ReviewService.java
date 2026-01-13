package com.example.revitech.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.QuestionAverageDto;
import com.example.revitech.entity.ReviewAnswer;
import com.example.revitech.entity.TeacherReview;
import com.example.revitech.repository.QuestionRepository;
import com.example.revitech.repository.ReviewAnswerRepository;
import com.example.revitech.repository.TeacherReviewRepository;
       
@Service
@Transactional
public class ReviewService {

    private final TeacherReviewRepository teacherReviewRepository;
    private final ReviewAnswerRepository reviewAnswerRepository;
    private final QuestionRepository questionRepository;

    public ReviewService(TeacherReviewRepository teacherReviewRepository,
                         ReviewAnswerRepository reviewAnswerRepository,
                         QuestionRepository questionRepository) {
        this.teacherReviewRepository = teacherReviewRepository;
        this.reviewAnswerRepository = reviewAnswerRepository;
        this.questionRepository = questionRepository;
    }

    // --- コントローラーが必要としているメソッド群 ---

    // 1. レビューIDで検索
    public TeacherReview findReviewById(Integer reviewId) {
        return teacherReviewRepository.findById(reviewId).orElse(null);
    }

    // 2. 特定の先生のレビュー一覧を取得
    public List<TeacherReview> getTeacherReviews(Integer teacherId) {
        return teacherReviewRepository.findByTeacherId(teacherId);
    }

    // 3. 開示ステータスの更新 (0:なし, 1:請求中, 2:許可)
    public void updateDisclosureStatus(Integer reviewId, Integer status) {
        Optional<TeacherReview> opt = teacherReviewRepository.findById(reviewId);
        if (opt.isPresent()) {
            TeacherReview review = opt.get();
            
            // ステータス番号に応じてフラグを操作
            if (status == 1) {
                // 請求中
                review.setIsDisclosureRequested(true);
                review.setIsDisclosureGranted(false);
            } else if (status == 2) {
                // 許可
                review.setIsDisclosureGranted(true);
            } else if (status == 0) {
                // リセット
                review.setIsDisclosureRequested(false);
                review.setIsDisclosureGranted(false);
            }
            
            teacherReviewRepository.save(review);
        }
    }

    // 4. 非表示フラグの切り替え (isHidden)
    public void toggleHiddenStatus(Integer reviewId) {
        Optional<TeacherReview> opt = teacherReviewRepository.findById(reviewId);
        if (opt.isPresent()) {
            TeacherReview review = opt.get();
            // 0なら1、1なら0にする
            int current = review.getIsHidden() == null ? 0 : review.getIsHidden();
            review.setIsHidden(current == 0 ? 1 : 0);
            teacherReviewRepository.save(review);
        }
    }

    // 5. 先生が確認したことのマーク (teacherChecked)
    public void markAsChecked(Integer reviewId) {
        Optional<TeacherReview> opt = teacherReviewRepository.findById(reviewId);
        if (opt.isPresent()) {
            TeacherReview review = opt.get();
            review.setTeacherChecked(1);
            teacherReviewRepository.save(review);
        }
    }

    // --- 集計・通知機能 ---

    /**
     * 指定された先生のレビュー評価（スコア）を項目ごとに集計して平均点を算出
     */
    public List<QuestionAverageDto> getTeacherQuestionAverages(Integer teacherId) {
        List<ReviewAnswer> answers = reviewAnswerRepository.findByTeacherId(teacherId);

        if (answers.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, Double> avgMap = answers.stream()
            .collect(Collectors.groupingBy(
                ReviewAnswer::getQuestionId,
                Collectors.averagingInt(ReviewAnswer::getScore)
            ));

        List<QuestionAverageDto> results = new ArrayList<>();
        
        avgMap.forEach((qId, avg) -> {
            String qText = questionRepository.findById(qId)
                            .map(q -> q.getQuestionBody()) 
                            .orElse("評価項目" + qId);
            
            double roundedAvg = Math.round(avg * 10.0) / 10.0;
            results.add(new QuestionAverageDto(qId, qText, roundedAvg));
        });

        return results;
    }

    public List<TeacherReview> findPendingDisclosures() {
        return teacherReviewRepository.findByIsDisclosureRequestedTrueAndIsDisclosureGrantedFalse();
    }

    public List<TeacherReview> findUncheckedGrantedDisclosures(Integer teacherId) {
        return teacherReviewRepository.findByTeacherIdAndIsDisclosureGrantedTrue(teacherId);
    }
}