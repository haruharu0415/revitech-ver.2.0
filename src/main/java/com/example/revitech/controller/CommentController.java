package com.example.revitech.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.dto.QuestionAverageDto;
import com.example.revitech.entity.Question;
import com.example.revitech.entity.Users;
import com.example.revitech.form.ReviewForm;
import com.example.revitech.service.ReviewService;
import com.example.revitech.service.UsersService;

@Controller
public class CommentController {

    private final UsersService usersService;
    private final ReviewService reviewService;

    public CommentController(UsersService usersService, ReviewService reviewService) {
        this.usersService = usersService;
        this.reviewService = reviewService;
    }

    /**
     * レビュー結果/アンケート遷移ボタン表示ページ
     */
    @GetMapping("/review/{teacherId}")
    public String showReviewPage(@PathVariable("teacherId") Integer teacherId, Model model, @AuthenticationPrincipal User loginUser) {
        Optional<Users> teacherOpt = usersService.findById(teacherId);

        if (teacherOpt.isPresent()) {
            Users teacher = teacherOpt.get();
            model.addAttribute("teacher", teacher);
            
            boolean isTeacher = false;
            
            if (loginUser != null) {
                Optional<Users> currentUserOpt = usersService.findByEmail(loginUser.getUsername());
                if (currentUserOpt.isPresent() && currentUserOpt.get().getRole() == 2) {
                    isTeacher = true;
                }
            }
            model.addAttribute("isTeacher", isTeacher);
            
            // 先生の場合: 評価結果（平均点）を取得
            if (isTeacher) {
                List<QuestionAverageDto> averages = reviewService.getTeacherQuestionAverages(teacherId);
                model.addAttribute("averages", averages);
            }
            
        } else {
            return "redirect:/teacher-list";
        }
        
        return "review";
    }

    /**
     * 【修正】アンケート回答フォーム表示ページ
     */
    @GetMapping("/review/form/{teacherId}")
    public String showReviewFormPage(@PathVariable("teacherId") Integer teacherId, Model model, @AuthenticationPrincipal User loginUser) {
        Optional<Users> teacherOpt = usersService.findById(teacherId);

        if (teacherOpt.isEmpty()) {
            return "redirect:/teacher-list";
        }
        
        // ログインチェックとRoleチェック（生徒以外は回答させない）
        if (loginUser == null) {
             return "redirect:/login";
        }
        Optional<Users> currentUserOpt = usersService.findByEmail(loginUser.getUsername());
        if (currentUserOpt.isEmpty() || currentUserOpt.get().getRole() != 1) { // Roleが1(生徒)以外は拒否
            // 権限エラーとして処理
            return "redirect:/review/" + teacherId + "?error=permission"; 
        }

        Users teacher = teacherOpt.get();
        model.addAttribute("teacher", teacher);

        // 質問リストを取得
        List<Question> questions = reviewService.getAllQuestions();
        model.addAttribute("questions", questions);
        
        // フォームオブジェクトの初期化
        ReviewForm form = new ReviewForm();
        form.setTeacherId(teacherId);
        model.addAttribute("reviewForm", form);
        
        return "review-form"; // ★★★ ファイル名を review-form に修正 ★★★
    }


    @PostMapping("/review/submit")
    public String submitReview(@ModelAttribute ReviewForm reviewForm, 
                               @AuthenticationPrincipal User loginUser,
                               RedirectAttributes redirectAttributes) {
        
        if (loginUser == null) {
            return "redirect:/login";
        }

        Optional<Users> currentUserOpt = usersService.findByEmail(loginUser.getUsername());
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }
        
        Integer studentId = currentUserOpt.get().getUsersId();
        
        // 保存処理
        reviewService.saveReview(studentId, reviewForm);
        
        redirectAttributes.addFlashAttribute("successMessage", "レビューを送信しました。ご協力ありがとうございました！");
        
        // レビューページに戻る
        return "redirect:/review/" + reviewForm.getTeacherId();
    }

    @GetMapping("/comment")
    public String showCommentPage(Model model) {
        return "comment";
    }
}