package com.example.revitech.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.revitech.entity.News;
import com.example.revitech.entity.NewsImage;
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

    @GetMapping 
    public String listNews(Model model, @AuthenticationPrincipal User user) {
        
        Users loginUser = null;
        if (user != null) {
            Optional<Users> userOpt = usersService.findByEmail(user.getUsername());
            if (userOpt.isPresent()) {
                loginUser = userOpt.get();
            }
        }

        // ユーザーに応じたお知らせを取得
        List<News> newsList = newsService.findNewsForUser(loginUser);
        model.addAttribute("newsList", newsList);

        // 登録ボタン表示制御
        boolean canRegister = false;
        if (loginUser != null) {
            Integer role = loginUser.getRole();
            // roleがnullでないことを確認してからチェック
            if (role != null && (role == 2 || role == 9 || role == 3)) {
                canRegister = true;
            }
        }
        model.addAttribute("canRegister", canRegister);

        return "news/list"; 
    }
    
    @GetMapping("/{id}")
    public String showDetail(@PathVariable Integer id, Model model, @AuthenticationPrincipal User user) {
        Optional<News> newsOpt = newsService.findById(id);
        
        if (newsOpt.isPresent()) {
            model.addAttribute("news", newsOpt.get());
            
            boolean canDelete = false;
            if (user != null) {
                Optional<Users> loginUser = usersService.findByEmail(user.getUsername());
                
                if (loginUser.isPresent()) {
                    Integer role = loginUser.get().getRole();
                    // roleがnullでないことを確認してからチェック
                    if (role != null && (role == 2 || role == 9 || role == 3)) {
                        canDelete = true;
                    }
                }
            }
            model.addAttribute("canDelete", canDelete);
            
            return "news/detail";
        } else {
            return "redirect:/news";
        }
    }
    
    @GetMapping("/register") 
    public String showRegistrationForm(Model model) {
        model.addAttribute("news", new News());
        
        Map<String, List<Users>> subjectUserMap = usersService.findAllStudentsGroupedBySubject();
        model.addAttribute("subjectUserMap", subjectUserMap);
        
        return "news/register"; 
    }
    
    @PostMapping("/register")
    public String registerNews(
            @ModelAttribute News news, 
            @AuthenticationPrincipal User user,
            @RequestParam("imageFiles") List<MultipartFile> imageFiles) {
        
        Integer senderId = 1; 
        if (user != null) {
             Optional<Users> sender = usersService.findByEmail(user.getUsername());
             if (sender.isPresent()) {
                 senderId = sender.get().getUsersId();
             }
        }
        
        news.setSenderId(senderId);
        news.setUsersId(senderId); 
        news.setNewsDatetime(LocalDateTime.now());

        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    try {
                        String base64Data = Base64.getEncoder().encodeToString(file.getBytes());
                        NewsImage newsImage = new NewsImage();
                        newsImage.setContentType(file.getContentType());
                        newsImage.setImageData(base64Data);
                        news.addImage(newsImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        newsService.createNews(news);
        return "redirect:/news"; 
    }

    @PostMapping("/delete/{id}")
    public String deleteNews(@PathVariable Integer id) {
        newsService.deleteNews(id);
        return "redirect:/news";
    }
}