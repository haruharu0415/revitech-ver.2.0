package com.example.revitech.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.revitech.dto.QuestionAverageDto;
import com.example.revitech.entity.Question;
import com.example.revitech.entity.ReviewAnswer;
import com.example.revitech.entity.TeacherReview;
import com.example.revitech.form.ReviewForm;
import com.example.revitech.repository.QuestionRepository; // 質問保存用
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

    // ★★★ 新規追加: 質問をデータベースに保存するメソッド ★★★
    public void saveQuestions(List<String> questionBodies) {
        if (questionBodies == null || questionBodies.isEmpty()) return;
        
        questionBodies.stream()
            .filter(body -> body != null && !body.trim().isEmpty())
            .map(body -> {
                Question question = new Question();
                question.setQuestionBody(body.trim());
                return question;
            })
            .forEach(questionRepository::save);
    }

    /**
     * 教員に対するアンケート評価の平均値を項目ごとに算出します。
     */
    @Transactional(readOnly = true)
    public List<QuestionAverageDto> getTeacherQuestionAverages(Integer teacherId) {
        List<Question> questions = questionRepository.findAll();
        
        return questions.stream()
            .map(question -> {
                List<ReviewAnswer> answers = reviewAnswerRepository.findByQuestionId(question.getQuestionId());
                
                double average = answers.stream()
                                        .mapToInt(ReviewAnswer::getScore)
                                        .average()
                                        .orElse(0.0);
                
                return new QuestionAverageDto(
                    question.getQuestionId(),
                    question.getQuestionBody(),
                    answers.size(),
                    average
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    
    // レビューと回答の保存処理
    public void saveReview(Integer studentId, ReviewForm form) {
        TeacherReview review = new TeacherReview();
        review.setTeacherId(form.getTeacherId());
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