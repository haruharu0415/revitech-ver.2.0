package com.example.revitech.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Users user = null;
        boolean canRegister = false;

        if (userDetails != null) {
            user = usersService.findByEmail(userDetails.getUsername()).orElse(null);
            // Roleが2(教員)または3(管理者)なら操作権限あり
            if (user != null && (user.getRole() == 2 || user.getRole() == 3)) {
                canRegister = true;
            }
        }

        List<News> newsList = newsService.findNewsForUser(user);
        
        model.addAttribute("newsList", newsList);
        // このフラグを表示・削除ボタンの制御に使用
        model.addAttribute("canRegister", canRegister);
        
        return "news/list";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            Users user = usersService.findByEmail(userDetails.getUsername()).orElse(null);
            // 生徒(1)は登録画面にアクセス不可
            if (user == null || user.getRole() == 1) {
                return "redirect:/news";
            }
        }
        
        model.addAttribute("news", new News());
        model.addAttribute("subjectUserMap", usersService.findAllStudentsGroupedBySubject());
        
        return "news/register";
    }

    @PostMapping("/create")
    public String createNews(@ModelAttribute News news, 
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("imageFiles") List<MultipartFile> imageFiles) {
        
        Integer senderId = 1; 
        if (userDetails != null) {
             Optional<Users> sender = usersService.findByEmail(userDetails.getUsername());
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
    
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Optional<News> news = newsService.findById(id);
        if (news.isPresent()) {
            model.addAttribute("news", news.get());
            return "news/detail";
        }
        return "redirect:/news";
    }

    // ★★★ 修正: 権限チェックを追加して削除を実行 ★★★
    @PostMapping("/delete/{id}")
    public String deleteNews(@PathVariable Integer id, 
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        
        if (userDetails == null) {
            return "redirect:/login";
        }

        Users user = usersService.findByEmail(userDetails.getUsername()).orElse(null);

        // Role=2(教員) または Role=3(管理者) の場合のみ削除実行
        if (user != null && (user.getRole() == 2 || user.getRole() == 3)) {
            newsService.deleteNews(id);
            redirectAttributes.addFlashAttribute("successMessage", "お知らせを削除しました。");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "削除権限がありません。");
        }
        
        return "redirect:/news";
    }
}