package com.example.revitech.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.dto.QuestionAverageDto;
import com.example.revitech.entity.BanWord;
import com.example.revitech.entity.TeacherImage;
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.entity.TeacherReview;
import com.example.revitech.entity.Users;
import com.example.revitech.repository.TeacherReviewRepository;
import com.example.revitech.service.BanWordService;
import com.example.revitech.service.ReviewService;
import com.example.revitech.service.TeacherProfileService;
import com.example.revitech.service.UsersService;

@Controller
public class CommentController {

    private final UsersService usersService;
    private final ReviewService reviewService;
    private final TeacherReviewRepository teacherReviewRepository;
    private final BanWordService banWordService;
    private final TeacherProfileService teacherProfileService;

    public CommentController(UsersService usersService, 
                             ReviewService reviewService, 
                             TeacherReviewRepository teacherReviewRepository, 
                             BanWordService banWordService,
                             TeacherProfileService teacherProfileService) {
        this.usersService = usersService;
        this.reviewService = reviewService;
        this.teacherReviewRepository = teacherReviewRepository;
        this.banWordService = banWordService;
        this.teacherProfileService = teacherProfileService;
    }

    @GetMapping("/review/{teacherId}")
    public String showReviewPage(@PathVariable("teacherId") Integer teacherId, Model model, @AuthenticationPrincipal User loginUser) {
        Optional<Users> teacherOpt = usersService.findById(teacherId);

        if (teacherOpt.isPresent()) {
            Users teacher = teacherOpt.get();
            model.addAttribute("teacher", teacher);
            
            Users currentUser = null;
            if (loginUser != null) {
                currentUser = usersService.findByEmail(loginUser.getUsername()).orElse(null);
                model.addAttribute("user", currentUser);

                if (currentUser != null && currentUser.getRole() == 2 && currentUser.getUsersId().equals(teacherId)) {
                    List<BanWord> banWords = banWordService.getBanWords(teacherId);
                    model.addAttribute("banWords", banWords);
                }
            }
            
            // ★★★ 重要: プロフィール情報を取得して画面に渡す ★★★
            TeacherProfile profile = teacherProfileService.getProfile(teacherId);
            List<TeacherImage> profileImages = teacherProfileService.getImages(teacherId);
            
            model.addAttribute("teacherProfile", profile);
            model.addAttribute("profileImages", profileImages);
            
            List<QuestionAverageDto> averages = reviewService.getTeacherQuestionAverages(teacherId);
            model.addAttribute("averages", averages);
            
            List<TeacherReview> allReviews = reviewService.getTeacherReviews(teacherId);
            
            final Users finalCurrentUser = currentUser;
            List<TeacherReview> filteredReviews = allReviews.stream()
                .filter(r -> {
                    if (r.getIsHidden() == 0) return true;
                    if (finalCurrentUser != null) {
                        boolean isAdmin = finalCurrentUser.getRole() == 3;
                        boolean isOwner = finalCurrentUser.getRole() == 2 && finalCurrentUser.getUsersId().equals(teacherId);
                        return isAdmin || isOwner;
                    }
                    return false; 
                })
                .collect(Collectors.toList());

            model.addAttribute("reviews", filteredReviews);
            
        } else {
            return "redirect:/teacher-list";
        }
        
        return "review";
    }
    
    @PostMapping("/review/{teacherId}/comment")
    public String submitComment(@PathVariable("teacherId") Integer teacherId,
                                @RequestParam("comment") String comment,
                                @AuthenticationPrincipal User loginUser,
                                RedirectAttributes redirectAttributes) {
        
        if (loginUser == null) return "redirect:/login";
        
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        
        if (currentUser.getRole() != 1) {
            redirectAttributes.addFlashAttribute("errorMessage", "コメント投稿は生徒のみ可能です。");
            return "redirect:/review/" + teacherId;
        }
        
        if (banWordService.containsBanWord(teacherId, comment)) {
            redirectAttributes.addFlashAttribute("errorMessage", "コメントに不適切な言葉が含まれているため、投稿できませんでした。");
            return "redirect:/review/" + teacherId;
        }

        if (comment != null && !comment.trim().isEmpty()) {
            TeacherReview review = new TeacherReview();
            review.setTeacherId(teacherId);
            review.setStudentId(currentUser.getUsersId());
            review.setComment(comment);
            review.setSurveyId(null);
            review.setScore(null);
            review.setIsHidden(0);
            review.setDisclosureStatus(0);
            review.setTeacherChecked(0);
            
            teacherReviewRepository.save(review);
            redirectAttributes.addFlashAttribute("successMessage", "コメントを投稿しました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "コメントを入力してください。");
        }

        return "redirect:/review/" + teacherId;
    }

    @PostMapping("/review/request-disclosure/{reviewId}")
    public String requestDisclosure(@PathVariable("reviewId") Integer reviewId,
                                    @AuthenticationPrincipal User loginUser,
                                    RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        TeacherReview review = reviewService.findReviewById(reviewId);

        if (review != null && currentUser.getRole() == 2 && currentUser.getUsersId().equals(review.getTeacherId())) {
            reviewService.updateDisclosureStatus(reviewId, 1);
            redirectAttributes.addFlashAttribute("successMessage", "管理者へ開示請求を行いました。");
            return "redirect:/review/" + review.getTeacherId();
        }
        return "redirect:/home";
    }

    @PostMapping("/review/grant-disclosure/{reviewId}")
    public String grantDisclosure(@PathVariable("reviewId") Integer reviewId,
                                  @AuthenticationPrincipal User loginUser,
                                  RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        TeacherReview review = reviewService.findReviewById(reviewId);

        if (review != null && currentUser.getRole() == 3) {
            reviewService.updateDisclosureStatus(reviewId, 2);
            redirectAttributes.addFlashAttribute("successMessage", "開示請求を許可しました。");
            return "redirect:/review/" + review.getTeacherId();
        }
        return "redirect:/home";
    }

    @PostMapping("/review/toggle-hidden/{reviewId}")
    public String toggleHidden(@PathVariable("reviewId") Integer reviewId,
                               @AuthenticationPrincipal User loginUser,
                               RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        TeacherReview review = reviewService.findReviewById(reviewId);

        if (review != null && currentUser.getRole() == 3) {
            reviewService.toggleHiddenStatus(reviewId);
            redirectAttributes.addFlashAttribute("successMessage", "表示状態を変更しました。");
            return "redirect:/review/" + review.getTeacherId();
        }
        return "redirect:/home";
    }

    @GetMapping("/review/disclosure-info/{reviewId}")
    public String showDisclosureInfo(@PathVariable("reviewId") Integer reviewId,
                                     Model model,
                                     @AuthenticationPrincipal User loginUser) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        TeacherReview review = reviewService.findReviewById(reviewId);

        if (review == null) return "redirect:/home";

        boolean isOwner = currentUser.getUsersId().equals(review.getTeacherId());
        boolean isGranted = review.getDisclosureStatus() == 2;

        if (!isOwner || !isGranted) {
            return "redirect:/review/" + review.getTeacherId();
        }
        
        reviewService.markAsChecked(reviewId);
        Users student = usersService.findById(review.getStudentId()).orElse(new Users()); 

        model.addAttribute("review", review);
        model.addAttribute("student", student);

        return "disclosure-info";
    }

    @GetMapping("/disclosure/list")
    public String listDisclosures(Model model, @AuthenticationPrincipal User loginUser) {
        Users currentUser = usersService.findByEmail(loginUser.getUsername()).orElseThrow();
        model.addAttribute("user", currentUser);
        
        if (currentUser.getRole() == 3) {
            List<TeacherReview> requests = reviewService.findPendingDisclosures();
            model.addAttribute("disclosureList", requests);
            model.addAttribute("pageTitle", "開示請求一覧 (管理者)");
        }
        else if (currentUser.getRole() == 2) {
            List<TeacherReview> allMyReviews = reviewService.getTeacherReviews(currentUser.getUsersId());
            List<TeacherReview> myGranted = allMyReviews.stream()
                .filter(r -> r.getDisclosureStatus() == 2)
                .collect(Collectors.toList());
            
            model.addAttribute("disclosureList", myGranted);
            model.addAttribute("pageTitle", "開示された情報一覧");
        } else {
            return "redirect:/home";
        }
        
        return "disclosure-list";
    }

    @GetMapping("/comment")
    public String showCommentPage(Model model) {
        return "comment";
    }
}