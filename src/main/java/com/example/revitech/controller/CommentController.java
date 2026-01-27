package com.example.revitech.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        
        Users teacher = usersService.findById(teacherId).orElse(null);
        if (teacher == null) {
            return "redirect:/teacher-list";
        }
        model.addAttribute("teacher", teacher);

        Users currentUser = null;
        if (userDetails != null) {
            currentUser = usersService.findByNameOrEmail(userDetails.getUsername()).orElse(null);
            model.addAttribute("user", currentUser);
        }

        List<TeacherReview> reviews = reviewService.findByTeacherId(teacherId);
        model.addAttribute("reviews", reviews);

        TeacherProfile profile = usersService.findTeacherProfile(teacherId);
        model.addAttribute("teacherProfile", profile);
        
        String iconPath = usersService.getUserIconPath(teacherId);
        model.addAttribute("teacherIcon", iconPath);

        // 総合スコア（既存のまま）
        double overallScore = 0.0;
        if (reviews != null && !reviews.isEmpty()) {
            OptionalDouble average = reviews.stream()
                                            .filter(r -> r != null && r.getScore() != null) 
                                            .mapToInt(TeacherReview::getScore)
                                            .average();
            if (average.isPresent()) {
                overallScore = Math.round(average.getAsDouble() * 10.0) / 10.0;
            }
        }
        model.addAttribute("overallScore", overallScore);

        // ★★★ 修正: 詳細項目の平均点をDBから取得 ★★★
        List<Map<String, Object>> averages = reviewService.getTeacherAverageScores(teacherId);
        
        // データがない場合は空リストを渡す（th:ifで「データなし」と表示される）
        if (averages == null) {
            averages = new ArrayList<>();
        }
        model.addAttribute("averages", averages);

        List<TeacherImage> profileImages = usersService.getTeacherImages(teacherId);
        model.addAttribute("profileImages", profileImages);

        model.addAttribute("hashtags", usersService.getTeacherHashtags(teacherId));
        model.addAttribute("banWords", usersService.getBanWordsByTeacherId(teacherId));
        
        return "review";
    }

    @PostMapping("/review/{teacherId}/comment")
    public String addComment(@PathVariable("teacherId") Integer teacherId,
                             @RequestParam("comment") String comment,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        
        if (userDetails == null) return "redirect:/login";
        
        if (reviewService.containsBanWord(teacherId, comment)) {
            redirectAttributes.addFlashAttribute("errorMessage", "投稿できませんでした。コメントに禁止されている言葉が含まれています。");
            return "redirect:/review/" + teacherId;
        }
        
        Users student = usersService.findByNameOrEmail(userDetails.getUsername()).orElse(null);
        if (student == null) return "redirect:/home";

        TeacherReview review = new TeacherReview();
        review.setTeacherId(teacherId);
        review.setStudentId(student.getUsersId());
        review.setComment(comment);
        review.setScore(3); 
        
        reviewService.saveReview(review);
        
        redirectAttributes.addFlashAttribute("successMessage", "コメントを投稿しました！");

        return "redirect:/review/" + teacherId;
    }

    // --- 以下、既存メソッド (変更なし) ---
    @GetMapping("/disclosure/list")
    public String disclosureList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        Users currentUser = usersService.findByNameOrEmail(userDetails.getUsername()).orElse(null);
        if (currentUser == null) return "redirect:/home";
        model.addAttribute("user", currentUser);
        List<Map<String, Object>> displayList = new ArrayList<>();
        if (currentUser.getRole() == 3) {
            model.addAttribute("pageTitle", "開示請求管理");
            for (TeacherReview review : reviewService.findPendingDisclosures()) {
                Map<String, Object> map = new HashMap<>();
                map.put("reviewId", review.getReviewId());
                map.put("comment", review.getComment());
                map.put("createdAt", review.getCreatedAt());
                map.put("teacherId", review.getTeacherId());
                map.put("disclosureStatus", 1);
                displayList.add(map);
            }
        } else if (currentUser.getRole() == 2) {
            model.addAttribute("pageTitle", "開示された情報");
            for (TeacherReview review : reviewService.findUncheckedGrantedDisclosures(currentUser.getUsersId())) {
                Map<String, Object> map = new HashMap<>();
                map.put("reviewId", review.getReviewId());
                map.put("comment", review.getComment());
                map.put("createdAt", review.getCreatedAt());
                map.put("teacherId", review.getTeacherId());
                map.put("disclosureStatus", 2);
                displayList.add(map);
            }
        }
        model.addAttribute("disclosureList", displayList);
        return "disclosure-list";
    }

    @PostMapping("/review/request-disclosure/{reviewId}")
    public String requestDisclosure(@PathVariable("reviewId") Integer reviewId,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";
        reviewService.requestDisclosure(reviewId);
        TeacherReview review = reviewService.findById(reviewId);
        return "redirect:/review/" + review.getTeacherId() + "?requested=true";
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