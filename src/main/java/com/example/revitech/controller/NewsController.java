package com.example.revitech.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.revitech.entity.News;
import com.example.revitech.entity.Users;
import com.example.revitech.service.NewsService;
import com.example.revitech.service.UsersService;

@Controller
@RequestMapping("/news")
public class NewsController {
    
    private final NewsService newsService; 
    private final UsersService usersService;
    
    public NewsController (NewsService newsService, UsersService usersService){
        this.newsService = newsService;
        this.usersService = usersService;
    }

    // 生徒向け: お知らせ一覧画面を表示する
    @GetMapping 
    public String listNews(Model model) {
        List<News> newsList = newsService.findAllNews();
        model.addAttribute("newsList", newsList);
        return "news/list"; 
    }
    
    // 教員向け: お知らせ登録フォームを表示する
    @GetMapping("/register") 
    public String showRegistrationForm(Model model) {
        model.addAttribute("news", new News());
        
        // 【★★ 修正箇所 ★★】
        // 生徒を学科ごとにグループ化して取得
        Map<String, List<Users>> subjectUserMap = usersService.findAllStudentsGroupedBySubject();
        model.addAttribute("subjectUserMap", subjectUserMap);
        
        return "news/register"; 
    }
    
    // 教員向け: お知らせ登録処理を実行する
    @PostMapping("/register")
    public String registerNews(@ModelAttribute News news, @AuthenticationPrincipal User user) {
        
        // ログインユーザーIDを取得して設定
        Integer senderId = 1; // デフォルト値
        if (user != null) {
             Optional<Users> sender = usersService.findByEmail(user.getUsername());
             if (sender.isPresent()) {
                 senderId = sender.get().getUsersId();
             }
        }
        news.setSenderId(senderId);

        news.setNewsDatetime(LocalDateTime.now());
        newsService.createNews(news);
        
        return "redirect:/news"; 
    }
}