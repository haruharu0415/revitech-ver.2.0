package com.example.revitech.controller; // パッケージ名は実際の場所に合わせてください

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.dto.UserSearchDto;
import com.example.revitech.service.UsersService;

@Controller
public class SearchController {

    @Autowired
    private UsersService usersService;

    // ユーザー検索フォームを表示する (例)
    @GetMapping("/user-search")
    public String searchForm() {
        return "user-search"; // user-search.html テンプレート
    }

    // 検索結果を表示するメソッド
    @GetMapping("/user-search/results")
    public String searchUsers(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, Model model) {

        List<UserSearchDto> users;
        if (keyword.isBlank()) {
            // キーワードが空の場合は空リストを返す (または全件表示などの仕様にする)
            users = List.of();
            model.addAttribute("message", "検索キーワードを入力してください。");
        } else {
            // ★ UsersService の検索メソッドを呼び出す
            users = usersService.findUsersByNameOrEmail(keyword);
            if (users.isEmpty()) {
                model.addAttribute("message", "該当するユーザーが見つかりませんでした。");
            }
        }

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword); // 検索窓にキーワードを再表示するため
        return "user-search-results"; // user-search-results.html テンプレート
    }
}