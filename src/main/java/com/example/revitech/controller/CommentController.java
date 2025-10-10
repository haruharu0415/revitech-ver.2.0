package com.example.revitech.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class CommentController {

    /**
     * URL: /comment へのGETリクエストを処理します。
     * * 現状はどの教員か特定せずコメントページに遷移させるための最低限の実装です。
     * 今後、特定の教員IDを受け取って処理を分岐させる必要があります。（後述の補足を参照）
     * * @return "comment" (src/main/resources/templates/comment.html を指します)
     */
    @GetMapping("/comment")
    public String showCommentPage(Model model) {
        // 必要に応じて、ここでコメント一覧のデータなどをModelに追加します
        return "comment"; 
    }
}