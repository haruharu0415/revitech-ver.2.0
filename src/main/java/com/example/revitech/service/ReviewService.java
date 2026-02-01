package com.example.revitech.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.SurveyResponseDetailDto;
import com.example.revitech.entity.BanWord;
import com.example.revitech.entity.ReviewAnswer;
import com.example.revitech.entity.TeacherReview;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.BanWordRepository;
import com.example.revitech.repository.QuestionRepository;
import com.example.revitech.repository.ReviewAnswerRepository;
import com.example.revitech.repository.TeacherReviewRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class ReviewService {

    private final TeacherReviewRepository teacherReviewRepository;
    private final BanWordRepository banWordRepository;
    private final ReviewAnswerRepository reviewAnswerRepository;
    private final QuestionRepository questionRepository;
    private final UsersRepository usersRepository;

    public ReviewService(TeacherReviewRepository teacherReviewRepository, 
                         BanWordRepository banWordRepository,
                         ReviewAnswerRepository reviewAnswerRepository,
                         QuestionRepository questionRepository,
                         UsersRepository usersRepository) {
        this.teacherReviewRepository = teacherReviewRepository;
        this.banWordRepository = banWordRepository;
        this.reviewAnswerRepository = reviewAnswerRepository;
        this.questionRepository = questionRepository;
        this.usersRepository = usersRepository;
    }

    // --- 既存メソッド ---
    public List<TeacherReview> getReviewsByTeacherId(Integer teacherId) {
        return teacherReviewRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId);
    }

    public List<TeacherReview> findByTeacherId(Integer teacherId) {
        return getReviewsByTeacherId(teacherId);
    }
    
    public TeacherReview saveReview(TeacherReview review) {
        return teacherReviewRepository.save(review);
    }

    public TeacherReview findById(Integer reviewId) {
        return teacherReviewRepository.findById(reviewId).orElseThrow();
    }
    
    // --- NGワードチェック ---
    public boolean containsBanWord(Integer teacherId, String comment) {
        if (comment == null || comment.isEmpty()) return false;
        List<BanWord> banWords = banWordRepository.findByTeacherId(teacherId);
        for (BanWord bw : banWords) {
            if (comment.contains(bw.getWord())) {
                return true;
            }
        }
        return false;
    }

    // --- 平均スコア計算 ---
    public List<Map<String, Object>> getTeacherAverageScores(Integer teacherId) {
        List<Object[]> results = reviewAnswerRepository.findAverageScoreByQuestionForTeacher(teacherId);
        if (results == null || results.isEmpty()) return new ArrayList<>();

        List<Map<String, Object>> averages = new ArrayList<>();
        for (Object[] row : results) {
            Integer questionId = (Integer) row[0];
            Double avgScore = (Double) row[1];

            String questionText = questionRepository.findById(questionId)
                    .map(q -> q.getQuestionBody())
                    .orElse("質問 " + questionId);

            Map<String, Object> map = new HashMap<>();
            map.put("questionId", questionId);
            map.put("questionText", questionText);
            map.put("averageScore", avgScore);
            averages.add(map);
        }
        return averages;
    }

    // ★★★ 修正: 先生の総合平均スコアを取得（四捨五入して小数点第1位まで） ★★★
    public Double getTeacherOverallScore(Integer teacherId) {
        Double avg = reviewAnswerRepository.findTotalAverageScoreByTeacherId(teacherId);
        if (avg == null) return 0.0;
        // 小数点第2位を四捨五入
        return Math.round(avg * 10.0) / 10.0;
    }

    // --- 開示請求関連 ---
    public long countPendingDisclosureRequests() {
        return teacherReviewRepository.countPendingDisclosureRequests();
    }

    public List<TeacherReview> getPendingDisclosureRequests() {
        return teacherReviewRepository.findPendingDisclosureRequests();
    }

    public List<TeacherReview> findPendingDisclosures() {
        return getPendingDisclosureRequests();
    }

    public long countGrantedDisclosuresForTeacher(Integer teacherId) {
        return teacherReviewRepository.countByTeacherIdAndIsDisclosureGrantedTrue(teacherId);
    }

    public List<TeacherReview> getGrantedDisclosuresForTeacher(Integer teacherId) {
        return teacherReviewRepository.findByTeacherIdAndIsDisclosureGrantedTrue(teacherId);
    }

    public List<TeacherReview> findUncheckedGrantedDisclosures(Integer teacherId) {
        return getGrantedDisclosuresForTeacher(teacherId);
    }

    public void requestDisclosure(Integer reviewId) {
        TeacherReview review = findById(reviewId);
        review.setIsDisclosureRequested(true);
        review.setIsDisclosureGranted(false);
        teacherReviewRepository.save(review);
    }

    public void grantDisclosure(Integer reviewId) {
        TeacherReview review = findById(reviewId);
        review.setIsDisclosureGranted(true);
        teacherReviewRepository.save(review);
    }

    public void toggleHidden(Integer reviewId) {
        TeacherReview review = findById(reviewId);
        Integer current = review.getIsHidden();
        review.setIsHidden(current == 1 ? 0 : 1);
        teacherReviewRepository.save(review);
    }
    
    public void markAsChecked(Integer reviewId) {
        // 実装が必要であればここに記述
    }

    // --- アンケート結果詳細取得 ---
    public List<SurveyResponseDetailDto> getSurveyResponseDetails(Integer surveyId, Integer viewerRole) {
        List<TeacherReview> reviews = teacherReviewRepository.findBySurveyId(surveyId);
        List<SurveyResponseDetailDto> responseDtos = new ArrayList<>();

        for (TeacherReview review : reviews) {
            SurveyResponseDetailDto dto = new SurveyResponseDetailDto();
            dto.setReviewId(review.getReviewId());
            dto.setAnsweredAt(review.getCreatedAt());
            dto.setScore(review.getScore());
            dto.setComment(review.getComment());

            String studentName = "匿名";
            Optional<Users> studentOpt = usersRepository.findById(review.getStudentId());
            if (studentOpt.isPresent()) {
                studentName = studentOpt.get().getName();
            }
            dto.setStudentName(studentName);

            List<ReviewAnswer> answers = reviewAnswerRepository.findByReviewId(review.getReviewId());
            List<SurveyResponseDetailDto.Detail> details = new ArrayList<>();
            
            for (ReviewAnswer ans : answers) {
                String qText = questionRepository.findById(ans.getQuestionId())
                        .map(q -> q.getQuestionBody())
                        .orElse("質問削除済み");
                details.add(new SurveyResponseDetailDto.Detail(qText, ans.getScore()));
            }
            dto.setDetails(details);
            
            responseDtos.add(dto);
        }
        return responseDtos;
    }
}