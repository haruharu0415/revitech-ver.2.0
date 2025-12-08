package com.example.revitech.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.revitech.entity.Question;
import com.example.revitech.entity.Users;
import com.example.revitech.service.ReviewService;
import com.example.revitech.service.UsersService; // UsersServiceを使用するため追加

@Controller
public class ReviewManagementController {

    private final ReviewService reviewService;
    private final UsersService usersService;

    public ReviewManagementController(ReviewService reviewService, UsersService usersService) {
        this.reviewService = reviewService;
        this.usersService = usersService;
    }
    
    // 権限チェックヘルパー
    private boolean checkTeacherOrAdmin(User loginUser) {
        if (loginUser == null) return false;
        Optional<Users> userOpt = usersService.findByEmail(loginUser.getUsername());
        if (userOpt.isEmpty()) return false;
        
        Integer role = userOpt.get().getRole();
        // Role 2 (先生) または Role 3 (管理者) を許可
        return role != null && (role == 2 || role == 3);
    }


    /**
     * GET: アンケート作成ページを表示 (教員/管理者専用)
     */
    @GetMapping("/question/create")
    public String showQuestionCreateForm(Model model, @AuthenticationPrincipal User loginUser, RedirectAttributes redirectAttributes) {
        // 権限チェック
        if (!checkTeacherOrAdmin(loginUser)) {
            redirectAttributes.addFlashAttribute("error", "アンケート作成権限がありません。");
            return "redirect:/home";
        }
        
        // ★★★ 修正・追加: 学生リストを学科ごとにグループ化して取得 ★★★
        model.addAttribute("subjectUserMap", usersService.findAllStudentsGroupedBySubject());

        // 現在の質問リストを表示（参考用）
        List<Question> existingQuestions = reviewService.getAllQuestions();
        model.addAttribute("existingQuestions", existingQuestions);
        
        // フォーム用（Thymeleafのバインド用に初期化）
        model.addAttribute("targetUserIds", new java.util.ArrayList<Integer>());

        return "question-create";
    }

    /**
     * POST: 新しい質問をデータベースに保存 (対象者リストも受け取る)
     */
    @PostMapping("/question/create")
    public String createQuestions(@RequestParam List<String> questionBody, 
                                  @RequestParam(required = false) List<Integer> targetUserIds, // ★★★ 修正・追加: 対象者IDリストを受け取る ★★★
                                  @AuthenticationPrincipal User loginUser,
                                  RedirectAttributes redirectAttributes) {
        
        // 権限チェック
        if (!checkTeacherOrAdmin(loginUser)) {
            redirectAttributes.addFlashAttribute("error", "アンケート作成権限がありません。");
            return "redirect:/home";
        }

        reviewService.saveQuestions(questionBody);
        
        // ★★★ 暫定的な処理: targetUserIds は受け取りましたが、現状のDB構造では保存場所がないため、
        // 今後の機能拡張のためにここではログ出力などに留めます。 ★★★
        if (targetUserIds != null && !targetUserIds.isEmpty()) {
            System.out.println("アンケート対象ユーザーID: " + targetUserIds);
        }
        
        redirectAttributes.addFlashAttribute("successMessage", "新しいアンケート質問を登録しました。");
        return "redirect:/question/create";
    }
}