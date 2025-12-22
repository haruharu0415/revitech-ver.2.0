package com.example.revitech.service;

import java.util.List;

// ... (importsは既存のまま)
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.TeacherReview;
import com.example.revitech.repository.QuestionRepository;
import com.example.revitech.repository.ReviewAnswerRepository;
import com.example.revitech.repository.TeacherReviewRepository;
// ... 他import

@Service
@Transactional
public class ReviewService {

    private final TeacherReviewRepository teacherReviewRepository;
    // ... (他のRepositoryフィールドとコンストラクタは既存のまま)
    private final ReviewAnswerRepository reviewAnswerRepository;
    private final QuestionRepository questionRepository;

    public ReviewService(TeacherReviewRepository teacherReviewRepository, ReviewAnswerRepository reviewAnswerRepository, QuestionRepository questionRepository) {
        this.teacherReviewRepository = teacherReviewRepository;
        this.reviewAnswerRepository = reviewAnswerRepository;
        this.questionRepository = questionRepository;
    }

    // ... (既存メソッドは省略) ...

    // ★★★ 追加: 管理者への通知 (請求中のリスト) ★★★
    public List<TeacherReview> findPendingDisclosures() {
        return teacherReviewRepository.findByDisclosureStatus(1); // 1:請求中
    }

    // ★★★ 追加: 先生への通知 (開示済みかつ未確認のリスト) ★★★
    public List<TeacherReview> findUncheckedGrantedDisclosures(Integer teacherId) {
        // status=2(開示済み) AND checked=0(未確認)
        return teacherReviewRepository.findByTeacherIdAndDisclosureStatusAndTeacherChecked(teacherId, 2, 0);
    }

    // ★★★ 追加: 先生が確認したことにする ★★★
    public void markAsChecked(Integer reviewId) {
        teacherReviewRepository.findById(reviewId).ifPresent(review -> {
            review.setTeacherChecked(1); // 1:確認済み
            teacherReviewRepository.save(review);
        });
    }
    
    // ... (既存の updateDisclosureStatus 等はそのまま) ...
    public TeacherReview findReviewById(Integer reviewId) {
        return teacherReviewRepository.findById(reviewId).orElse(null);
    }

    public void updateDisclosureStatus(Integer reviewId, Integer status) {
        TeacherReview review = teacherReviewRepository.findById(reviewId).orElseThrow();
        review.setDisclosureStatus(status);
        teacherReviewRepository.save(review);
    }

    public void toggleHiddenStatus(Integer reviewId) {
        TeacherReview review = teacherReviewRepository.findById(reviewId).orElseThrow();
        review.setIsHidden(review.getIsHidden() == 0 ? 1 : 0);
        teacherReviewRepository.save(review);
    }
    
    // ... (その他のメソッド)
    // コンパイルエラー防止のため、以前提示したメソッドの省略形を記載していますが、
    // 実装時は既存のコードを消さないでください。
    public void deleteQuestion(Integer id) { /*...*/ }
    public List<com.example.revitech.dto.QuestionAverageDto> getTeacherQuestionAverages(Integer id) { return List.of(); /*ダミー*/ }
    public List<TeacherReview> getTeacherReviews(Integer id) { return teacherReviewRepository.findByTeacherIdOrderByCreatedAtDesc(id); }
}