package com.example.revitech.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.BanWord;
import com.example.revitech.entity.TeacherHashtag;
import com.example.revitech.entity.TeacherImage;
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.entity.TeacherReview;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ReviewService;
import com.example.revitech.service.UsersService;

@Controller
public class CommentController {

    private final ReviewService reviewService;
    private final UsersService usersService;

    public CommentController(ReviewService reviewService, UsersService usersService) {
        this.reviewService = reviewService;
        this.usersService = usersService;
    }

    @GetMapping("/review/{teacherId}")
    public String showReviewPage(@PathVariable("teacherId") Integer teacherId,
                                 Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        
        // 教員情報の取得
        Users teacher = usersService.findById(teacherId).orElse(null);
        if (teacher == null) {
            return "redirect:/teacher-list"; 
        }
        model.addAttribute("teacher", teacher);

        // 教員プロフィール詳細の取得
        TeacherProfile profile = usersService.findTeacherProfile(teacherId);
        model.addAttribute("teacherProfile", profile);
        
        // アイコン画像
        String teacherIcon = usersService.getUserIconPath(teacherId);
        model.addAttribute("teacherIcon", teacherIcon);

        // ギャラリー画像
        List<TeacherImage> images = usersService.getTeacherImages(teacherId);
        model.addAttribute("profileImages", images);

        // ハッシュタグ
        List<TeacherHashtag> hashtags = usersService.getTeacherHashtags(teacherId);
        model.addAttribute("hashtags", hashtags);

        // NGワード (本人確認用)
        if (userDetails != null) {
            Users loginUser = usersService.findByNameOrEmail(userDetails.getUsername()).orElse(null);
            model.addAttribute("user", loginUser);
            if (loginUser != null && loginUser.getUsersId().equals(teacherId)) {
                List<BanWord> banWords = usersService.getBanWordsByTeacherId(teacherId);
                model.addAttribute("banWords", banWords);
            }
        }

        // レビュー一覧取得
        List<TeacherReview> reviews = reviewService.findByTeacherId(teacherId);
        model.addAttribute("reviews", reviews);

        // ★★★ 修正箇所: 総合スコアの計算ロジックを変更 ★★★
        // 以前のTeacherReview.score (全体評価) は使わず、詳細回答(ReviewAnswer)の平均を使う
        Double overallScore = reviewService.getTeacherOverallScore(teacherId);
        model.addAttribute("overallScore", overallScore);

        // 項目別平均スコア (グラフ用)
        List<Map<String, Object>> averages = reviewService.getTeacherAverageScores(teacherId);
        model.addAttribute("averages", averages);

        return "review";
    }

    // コメント投稿
    @PostMapping("/review/{teacherId}/comment")
    public String postComment(@PathVariable("teacherId") Integer teacherId,
                              @RequestParam("comment") String comment,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        
        if (userDetails == null) return "redirect:/login";
        Users student = usersService.findByNameOrEmail(userDetails.getUsername()).orElseThrow();
        
        if (student.getRole() != 1) {
            redirectAttributes.addFlashAttribute("errorMessage", "コメントできるのは生徒だけです。");
            return "redirect:/review/" + teacherId;
        }

        // NGワードチェック
        if (reviewService.containsBanWord(teacherId, comment)) {
            redirectAttributes.addFlashAttribute("errorMessage", "コメントに禁止ワードが含まれています。");
            return "redirect:/review/" + teacherId;
        }

        // コメントのみの投稿として保存 (scoreは0)
        TeacherReview review = new TeacherReview();
        review.setTeacherId(teacherId);
        review.setStudentId(student.getUsersId());
        review.setComment(comment);
        review.setScore(0); // スコアなし
        review.setIsHidden(0);
        review.setIsDisclosureRequested(false);
        review.setIsDisclosureGranted(false);
        
        reviewService.saveReview(review);
        
        redirectAttributes.addFlashAttribute("successMessage", "コメントを投稿しました！");
        return "redirect:/review/" + teacherId;
    }

    // --- 開示請求関連 ---
    @PostMapping("/review/request-disclosure/{reviewId}")
    public String requestDisclosure(@PathVariable("reviewId") Integer reviewId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.requestDisclosure(reviewId);
        return "redirect:/review/" + reviewService.findById(reviewId).getTeacherId();
    }

    @PostMapping("/review/grant-disclosure/{reviewId}")
    public String grantDisclosure(@PathVariable("reviewId") Integer reviewId,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.grantDisclosure(reviewId);
        return "redirect:/disclosure/list";
    }

    @PostMapping("/review/toggle-hidden/{reviewId}")
    public String toggleHidden(@PathVariable("reviewId") Integer reviewId,
                               @AuthenticationPrincipal UserDetails userDetails) {
        reviewService.toggleHidden(reviewId);
        TeacherReview review = reviewService.findById(reviewId);
        return "redirect:/review/" + review.getTeacherId();
    }

    @GetMapping("/review/disclosure-info/{id}")
    public String disclosureInfo(@PathVariable("id") Integer reviewId,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        if (userDetails == null) return "redirect:/login";
        TeacherReview review = reviewService.findById(reviewId);
        Users student = usersService.findById(review.getStudentId()).orElse(null);
        if (student != null) {
            model.addAttribute("review", review);
            model.addAttribute("student", student);
            String subjectName = usersService.getStudentSubjectName(student.getUsersId());
            model.addAttribute("subjectName", subjectName);
            reviewService.markAsChecked(reviewId);
        }
        return "disclosure-info";
    }
}