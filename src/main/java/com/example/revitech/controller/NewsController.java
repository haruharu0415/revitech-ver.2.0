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
            if (user != null && (user.getRole() == 2 || user.getRole() == 3)) {
                canRegister = true;
            }
        }

        List<News> newsList = newsService.findNewsForUser(user);
        
        model.addAttribute("newsList", newsList);
        model.addAttribute("canRegister", canRegister);
        
        return "news/list";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            Users user = usersService.findByEmail(userDetails.getUsername()).orElse(null);
            if (user == null || user.getRole() == 1) {
                return "redirect:/news";
            }
        }
        
        model.addAttribute("news", new News());
        
        // ★★★ 修正: findAllStudents() ではなく、register.html が必要としている subjectUserMap を渡す ★★★
        // SurveyManagementControllerですでに実装済みのメソッドを利用
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

    @PostMapping("/delete/{id}")
    public String deleteNews(@PathVariable Integer id) {
        newsService.deleteNews(id);
        return "redirect:/news";
    }
}