package com.example.revitech.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.entity.Question;
import com.example.revitech.entity.ReviewAnswer;
import com.example.revitech.entity.Survey;
import com.example.revitech.entity.SurveyTarget;
import com.example.revitech.entity.TeacherReview;
import com.example.revitech.form.ReviewForm;
import com.example.revitech.repository.QuestionRepository;
import com.example.revitech.repository.ReviewAnswerRepository;
import com.example.revitech.repository.SurveyRepository;
import com.example.revitech.repository.SurveyTargetRepository;
import com.example.revitech.repository.TeacherReviewRepository;

@Service
@Transactional
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final SurveyTargetRepository surveyTargetRepository;
    private final QuestionRepository questionRepository;
    private final TeacherReviewRepository teacherReviewRepository;
    private final ReviewAnswerRepository reviewAnswerRepository;

    public SurveyService(SurveyRepository surveyRepository, SurveyTargetRepository surveyTargetRepository, QuestionRepository questionRepository, TeacherReviewRepository teacherReviewRepository, ReviewAnswerRepository reviewAnswerRepository) {
        this.surveyRepository = surveyRepository;
        this.surveyTargetRepository = surveyTargetRepository;
        this.questionRepository = questionRepository;
        this.teacherReviewRepository = teacherReviewRepository;
        this.reviewAnswerRepository = reviewAnswerRepository;
    }

    // ★★★ 修正: targetTeacherId を受け取る ★★★
    public void createSurvey(String title, Integer creatorTeacherId, Integer targetTeacherId, List<String> questionBodies, List<Integer> targetUserIds) {
        // 1. Survey本体の保存
        Survey survey = new Survey();
        survey.setTitle(title);
        survey.setTeacherId(creatorTeacherId); // 作成者
        survey.setTargetTeacherId(targetTeacherId); // ★ 紐付け先
        Survey savedSurvey = surveyRepository.save(survey);

        // 2. 質問の保存 (変更なし)
        if (questionBodies != null) {
            for (String body : questionBodies) {
                if (body != null && !body.trim().isEmpty()) {
                    Question q = new Question();
                    q.setSurveyId(savedSurvey.getSurveyId());
                    q.setQuestionBody(body.trim());
                    questionRepository.save(q);
                }
            }
        }

        // 3. 対象者の保存 (変更なし)
        if (targetUserIds != null) {
            for (Integer studentId : targetUserIds) {
                SurveyTarget target = new SurveyTarget();
                target.setSurveyId(savedSurvey.getSurveyId());
                target.setStudentId(studentId);
                surveyTargetRepository.save(target);
            }
        }
    }
    
    // ... (その他のメソッドは省略) ...
    // アンケート削除 (既存)
    public void deleteSurvey(Integer surveyId) {
        surveyTargetRepository.deleteBySurveyId(surveyId);
        teacherReviewRepository.deleteBySurveyId(surveyId);
        questionRepository.deleteBySurveyId(surveyId);
        surveyRepository.deleteById(surveyId);
    }

    // 先生用一覧 (既存)
    public List<Survey> getSurveysByTeacher(Integer teacherId) {
        return surveyRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId);
    }

    // 生徒用一覧 (既存)
    public List<Survey> getSurveysForStudent(Integer studentId) {
        return surveyRepository.findByTargetStudentId(studentId);
    }

    public Optional<Survey> findSurveyById(Integer surveyId) {
        return surveyRepository.findById(surveyId);
    }

    public List<Question> getQuestionsBySurveyId(Integer surveyId) {
        return questionRepository.findBySurveyId(surveyId);
    }
    
    public boolean hasStudentAnswered(Integer surveyId, Integer studentId) {
        return teacherReviewRepository.existsBySurveyIdAndStudentId(surveyId, studentId);
    }

    public void saveSurveyResponse(Integer studentId, ReviewForm form) {
        if (hasStudentAnswered(form.getSurveyId(), studentId)) {
            throw new IllegalStateException("ALREADY_ANSWERED");
        }

        TeacherReview review = new TeacherReview();
        review.setSurveyId(form.getSurveyId());
        review.setTeacherId(form.getTeacherId()); // ★ TeacherReviewのteacherIdは、targetTeacherIdが入る
        review.setStudentId(studentId);
        review.setScore(form.getScore());
        review.setComment(form.getComment());
        
        TeacherReview savedReview = teacherReviewRepository.save(review);
        
        if (form.getAnswers() != null) {
            for (Map.Entry<Integer, Integer> entry : form.getAnswers().entrySet()) {
                Integer questionId = entry.getKey();
                Integer score = entry.getValue();
                
                if (score != null) {
                    ReviewAnswer answer = new ReviewAnswer();
                    answer.setReviewId(savedReview.getReviewId());
                    answer.setQuestionId(questionId);
                    answer.setScore(score);
                    reviewAnswerRepository.save(answer);
                }
            }
        }
    }
}