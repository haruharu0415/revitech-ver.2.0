package com.example.revitech.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.revitech.entity.Users;
import com.example.revitech.service.UsersService;

@Controller
public class CommentController {

    private final UsersService usersService;

    // UsersServiceを注入して、教員情報を取得できるようにする
    public CommentController(UsersService usersService) {
        this.usersService = usersService;
    }

    /**
     * ★★★ 修正 ★★★
     * 特定の教員の評価ページを表示します。
     * URL: /review/{teacherId}
     * @param teacherId URLから受け取る教員のID
     * @param model ビューに渡すためのモデル
     * @return review.htmlテンプレート
     */
    @GetMapping("/review/{teacherId}")
    public String showReviewPage(@PathVariable("teacherId") Integer teacherId, Model model) {
        // IDを使って教員情報をデータベースから取得
        Optional<Users> teacherOpt = usersService.findById(teacherId);

        if (teacherOpt.isPresent()) {
            // 教員が見つかった場合、その情報をモデルに追加してビューに渡す
            model.addAttribute("teacher", teacherOpt.get());
        } else {
            // 教員が見つからなかった場合（エラーハンドリング）
            // 必要に応じて、エラーページにリダイレクトするなどの処理を追加できます
            model.addAttribute("teacher", null);
        }
        
        return "review"; // templates/review.html を表示
    }

    /**
     * 汎用のコメントページ（現在は未使用ですが、念のため残します）
     */
    @GetMapping("/comment")
    public String showCommentPage(Model model) {
        return "comment";
    }
}