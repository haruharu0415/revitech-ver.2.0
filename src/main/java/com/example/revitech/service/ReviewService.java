package com.example.revitech.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.SurveyResponseDetailDto;
import com.example.revitech.dto.SurveyResponseDetailDto.QuestionAnswerDto;
import com.example.revitech.entity.BanWord;
import com.example.revitech.entity.SurveyAnswer;
import com.example.revitech.entity.SurveyQuestion;
import com.example.revitech.entity.TeacherReview;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.BanWordRepository;
import com.example.revitech.repository.SurveyAnswerRepository;
import com.example.revitech.repository.SurveyQuestionRepository;
import com.example.revitech.repository.TeacherReviewRepository;
import com.example.revitech.repository.UsersRepository;

@Service
@Transactional
public class ReviewService {

    private final TeacherReviewRepository teacherReviewRepository;
    private final UsersRepository usersRepository;
    private final BanWordRepository banWordRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;

    public ReviewService(TeacherReviewRepository teacherReviewRepository, 
                         UsersRepository usersRepository,
                         BanWordRepository banWordRepository,
                         SurveyQuestionRepository surveyQuestionRepository,
                         SurveyAnswerRepository surveyAnswerRepository) {
        this.teacherReviewRepository = teacherReviewRepository;
        this.usersRepository = usersRepository;
        this.banWordRepository = banWordRepository;
        this.surveyQuestionRepository = surveyQuestionRepository;
        this.surveyAnswerRepository = surveyAnswerRepository;
    }

    // ★★★ 追加: 先生ごとの詳細項目平均点を取得 ★★★
    public List<Map<String, Object>> getTeacherAverageScores(Integer teacherId) {
        return surveyAnswerRepository.findAverageScoresByTeacherId(teacherId);
    }

    // --- 以下、既存メソッド (変更なし) ---

    public List<SurveyResponseDetailDto> getSurveyResponseDetails(Integer surveyId, Integer viewerRole) {
        List<TeacherReview> reviews = teacherReviewRepository.findBySurveyId(surveyId);
        List<SurveyQuestion> questions = surveyQuestionRepository.findBySurveyIdOrderByQuestionIdAsc(surveyId);

        return reviews.stream().map(review -> {
            SurveyResponseDetailDto dto = new SurveyResponseDetailDto();
            dto.setReviewId(review.getReviewId());
            dto.setScore(review.getScore());
            dto.setComment(review.getComment());
            dto.setCreatedAt(review.getCreatedAt());
            dto.setAnsweredAt(review.getCreatedAt());
            
            Users student = usersRepository.findById(review.getStudentId()).orElse(null);
            if (student != null) {
                dto.setStudentName("匿名ユーザー"); 
            } else {
                dto.setStudentName("不明なユーザー");
            }

            List<SurveyAnswer> answers = surveyAnswerRepository.findByReviewId(review.getReviewId());
            Map<Integer, Integer> scoreMap = answers.stream()
                .collect(Collectors.toMap(SurveyAnswer::getQuestionId, SurveyAnswer::getScore, (v1, v2) -> v1));

            List<QuestionAnswerDto> details = new ArrayList<>();
            for (SurveyQuestion q : questions) {
                Integer score = scoreMap.get(q.getQuestionId());
                if (score == null) score = 0;
                details.add(new QuestionAnswerDto(q.getQuestionBody(), score));
            }
            dto.setDetails(details);
            
            return dto;
        }).collect(Collectors.toList());
    }

    public boolean containsBanWord(Integer teacherId, String comment) {
        if (comment == null || comment.trim().isEmpty()) return false;
        List<BanWord> banWords = banWordRepository.findByTeacherId(teacherId);
        String lowerComment = comment.toLowerCase();
        for (BanWord bw : banWords) {
            if (bw.getWord() != null && lowerComment.contains(bw.getWord().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public void saveReview(TeacherReview review) {
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(LocalDateTime.now());
        }
        if (review.getIsHidden() == null) review.setIsHidden(0);
        if (review.getDisclosureStatus() == null) review.setDisclosureStatus(0);
        if (review.getIsDisclosureRequested() == null) review.setIsDisclosureRequested(false);
        if (review.getIsDisclosureGranted() == null) review.setIsDisclosureGranted(false);
        if (review.getTeacherChecked() == null) review.setTeacherChecked(0);
        teacherReviewRepository.save(review);
    }

    public List<TeacherReview> findByTeacherId(Integer teacherId) { return teacherReviewRepository.findByTeacherId(teacherId); }
    public TeacherReview findById(Integer id) { return teacherReviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found with id: " + id)); }
    public void requestDisclosure(Integer reviewId) {
        TeacherReview review = findById(reviewId);
        review.setIsDisclosureRequested(true);
        review.setDisclosureStatus(1);
        teacherReviewRepository.save(review);
    }
    public void grantDisclosure(Integer reviewId) {
        TeacherReview review = findById(reviewId);
        review.setIsDisclosureGranted(true);
        review.setDisclosureStatus(2);
        review.setTeacherChecked(0);
        teacherReviewRepository.save(review);
    }
    public void toggleHidden(Integer reviewId) {
        TeacherReview review = findById(reviewId);
        int current = (review.getIsHidden() == null) ? 0 : review.getIsHidden();
        review.setIsHidden(current == 0 ? 1 : 0);
        teacherReviewRepository.save(review);
    }
    public List<TeacherReview> findPendingDisclosures() { return teacherReviewRepository.findByDisclosureStatus(1); }
    public List<TeacherReview> findUncheckedGrantedDisclosures(Integer teacherId) {
        return teacherReviewRepository.findByTeacherId(teacherId).stream()
                .filter(r -> r.getDisclosureStatus() != null && r.getDisclosureStatus() == 2)
                .filter(r -> r.getTeacherChecked() == null || r.getTeacherChecked() == 0)
                .collect(Collectors.toList());
    }
    public void markAsChecked(Integer reviewId) {
        TeacherReview review = findById(reviewId);
        review.setTeacherChecked(1);
        teacherReviewRepository.save(review);
    }
}