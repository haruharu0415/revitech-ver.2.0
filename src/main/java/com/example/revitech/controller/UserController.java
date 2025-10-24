package com.example.revitech.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
// import java.util.UUID; // ★ UUID は使わない
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.revitech.dto.TeacherListDto; // ★ id は Long
import com.example.revitech.dto.UserProfileDto; // ★ id は Long
import com.example.revitech.entity.StudentProfile;
import com.example.revitech.entity.TeacherProfile;
import com.example.revitech.entity.Users; // ★ id は Long
import com.example.revitech.repository.StudentProfileRepository; // ★ 主キー型は Long
import com.example.revitech.repository.TeacherProfileRepository; // ★ 主キー型は Long
import com.example.revitech.service.UsersService; // ★ findById(Long)
// import com.example.revitech.service.ReviewService;

@Controller
public class UserController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private TeacherProfileRepository teacherProfileRepository;
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    // @Autowired
    // private ReviewService reviewService;

    // --- terms, group, groupCreate ---
    @GetMapping("/terms")
    public String terms() { return "terms"; }
    @GetMapping("/group")
    public String group() { return "group"; }
    
    @GetMapping("/group-create")
    public String groupCreate(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());
        if (userOpt.isPresent()) {
            model.addAttribute("userId", userOpt.get().getId()); // ★ Long 型の ID
        } else {
            return "redirect:/login";
        }
        return "group-create";
    }

    // --- teacher-list ---
    @GetMapping("/teacher-list")
    public String showTeacherList(@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
                                  Model model) {
        List<Users> teachers = usersService.findAllUsers().stream()
                .filter(u -> u.getRole() != null && u.getRole() == 2) // Role 2 is Teacher
                .toList();

        List<TeacherListDto> teacherDtos = new ArrayList<>();
        for (Users teacher : teachers) {
            // ★ findIconUrl に Long を渡す ★
            String iconUrl = findIconUrl(teacher.getId(), teacher.getRole());
            // double averageRating = reviewService.getAverageRatingForTeacher(teacher.getId()); // ★ Long を渡す
            double averageRating = Math.round((Math.random() * 4.0 + 1.0) * 10.0) / 10.0; // Placeholder

            // ★ TeacherListDto コンストラクタに Long を渡す ★
            teacherDtos.add(new TeacherListDto(teacher.getId(), teacher.getName(), iconUrl, averageRating));
        }

        List<TeacherListDto> filteredTeachers;
        if (!keyword.isBlank()) {
            String lowerKeyword = keyword.toLowerCase();
            filteredTeachers = teacherDtos.stream()
                .filter(t -> t.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
            model.addAttribute("message", filteredTeachers.isEmpty() ? "「" + keyword + "」に一致する教員は見つかりません。" : null);
        } else {
            filteredTeachers = teacherDtos;
        }

        model.addAttribute("teachers", filteredTeachers);
        model.addAttribute("keyword", keyword);

        return "teacher-list";
    }

    // --- option ---
    @GetMapping("/option")
    public String option(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());

        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            // ★ findIconUrl に Long を渡す ★
            String iconUrl = findIconUrl(user.getId(), user.getRole());
            // ★ UserProfileDto コンストラクタに Long を渡す ★
            UserProfileDto userDto = new UserProfileDto(user.getId(), user.getName(), iconUrl);
            model.addAttribute("user", userDto);
        }
        return "option";
    }

    // --- profile/edit form ---
    @GetMapping("/profile/edit")
    public String profileEditForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<Users> userOpt = usersService.findByEmail(auth.getName());

        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            // ★ findIconUrl に Long を渡す ★
            String iconUrl = findIconUrl(user.getId(), user.getRole());
             // ★ UserProfileDto コンストラクタに Long を渡す ★
            UserProfileDto userDto = new UserProfileDto(user.getId(), user.getName(), iconUrl);
            model.addAttribute("user", userDto);
        } else {
            return "redirect:/login";
        }
        return "edit_profile";
    }

    // --- Helper method: findIconUrl ---
    // ★ userId の型を Long に戻す ★
    private String findIconUrl(Long userId, Integer role) {
        String iconUrl = null;
        if (role != null) {
            if (role == 2) { // Teacher
                // ★ findById に Long を渡す ★
                Optional<TeacherProfile> profileOpt = teacherProfileRepository.findById(userId);
                if (profileOpt.isPresent()) iconUrl = profileOpt.get().getIconPicture();
            } else if (role == 3) { // Student
                // ★ findById に Long を渡す ★
                Optional<StudentProfile> profileOpt = studentProfileRepository.findById(userId);
                if (profileOpt.isPresent()) iconUrl = profileOpt.get().getIconPicture();
            }
        }
        return iconUrl;
    }
}